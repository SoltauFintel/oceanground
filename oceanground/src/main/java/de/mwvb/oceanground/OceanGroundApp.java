package de.mwvb.oceanground;

import org.pmw.tinylog.Logger;

import de.mwvb.auth.schild.SchildAuthPlugin;
import de.mwvb.maja.auth.AuthPlugin;
import de.mwvb.maja.auth.OneUserAuthorization;
import de.mwvb.maja.auth.rememberme.KnownUser;
import de.mwvb.maja.auth.rememberme.RememberMeInMongoDB;
import de.mwvb.maja.mongo.Database;
import de.mwvb.maja.web.AbstractWebApp;
import de.mwvb.maja.web.Action;
import de.mwvb.maja.web.NoOpAuthPlugin;
import de.mwvb.oceanground.actions.ContainerConsole;
import de.mwvb.oceanground.actions.ContainerDetails;
import de.mwvb.oceanground.actions.ContainerTable;
import de.mwvb.oceanground.actions.DeleteContainer;
import de.mwvb.oceanground.actions.DeleteSimilarImages;
import de.mwvb.oceanground.actions.DockerPS;
import de.mwvb.oceanground.actions.EditContainer;
import de.mwvb.oceanground.actions.HealthCheck;
import de.mwvb.oceanground.actions.ImageDetails;
import de.mwvb.oceanground.actions.Images;
import de.mwvb.oceanground.actions.Index;
import de.mwvb.oceanground.actions.Info;
import de.mwvb.oceanground.actions.ManageCopyContainer;
import de.mwvb.oceanground.actions.ManageDeleteContainer;
import de.mwvb.oceanground.actions.ManageDeleteContainerQuery;
import de.mwvb.oceanground.actions.NRContainer;
import de.mwvb.oceanground.actions.NewContainer;
import de.mwvb.oceanground.actions.NewContainerSave;
import de.mwvb.oceanground.actions.SaveContainer;
import de.mwvb.oceanground.actions.StartContainer;
import de.mwvb.oceanground.actions.StopContainer;
import de.mwvb.oceanground.actions.UpdateContainer;
import de.mwvb.oceanground.docker.AbstractDocker;
import de.mwvb.oceanground.docker.OGImage;
import de.mwvb.oceanground.docker.UnixDocker;
import de.mwvb.oceanground.docker.WindowsDocker;
import de.mwvb.oceanground.model.Container;
import spark.Request;

public class OceanGroundApp extends AbstractWebApp {
	public static final String VERSION = "1.0.2";
	// 0.2: Dependencies update, Java 8u121
	// 0.2.1: LogConfig, always with pull
	// 0.3: Umstellung auf plutoweb, Thymeleaf -> Velocity
	// 0.3.1: OceanGroundUpdater
	// 0.3.2: Action-Klassen, Bootstrap
	// 0.3.3.x: Handytauglichkeit (Firefox auf Tablet ist tlw. problematisch, Chrome geht, PC geht)
	// 0.3.4: SB Admin 2 (Bootstrap template)
	// 0.3.5: Containerliste nun als Tabelle
	// 0.3.6: Images
	// 0.3.7: Container Details
	// 0.3.8: alle Container, Images
	// 0.3.9: Intercooler-JS
	// 0.3.10: Logging
	// 0.3.11: +benzin image
	// 0.3.12: ping und info Ausgabe
	// 0.3.13: ähnliche Images löschen, Container löschen
	// 0.4.0: Container durchstarten
	// 0.5.0: Umstellung auf Maja, Container editor
	// 0.5.1: Removed old code, HTML escaping of DockerLogs output
	// 0.6.0: Umstellung auf Maja 0.2
	// 0.6.1: Container Speicher ausgeben
	// 0.6.2: MaxMemory, Restart Count, Umordnung der Container Details Felder
	// 0.6.3: Verbesserungen bzgl. Memory Thema
	// 0.6.4: Verbesserungen bzgl. Memory Thema
	// 0.6.5: Verbesserungen bzgl. Memory Thema
	// 0.6.6: Schild auth
	// 0.6.7: Schild auth
	// 0.7.0: Container starten und stoppen
	// 0.7.1: new config parameters: auth, docker.version
	// 0.7.2: Stdout complete
	// 0.7.3: Logging für Fehlersuche
	// 1.0.0: Intercooler + jquery inline nachdem Intercooler nicht mehr erreichbar war
	// 1.0.2: PortBindings NPE fix
	public static final String TITLE = "OceanGround";
	public static AbstractDocker docker;

	@Override
	protected void routes() {
		_get("/", Index.class);
		_get("/index", Index.class);
		_get("/dockerps", DockerPS.class);
		_get("/nrcontainer", NRContainer.class);
		_get("/container/:container/console/std", ContainerConsole.class);
		_get("/container/:container", ContainerDetails.class);
		_get("/container/:id/delete", DeleteContainer.class);
		_get("/container/:id/start", StartContainer.class);
		_get("/container/:id/stop", StopContainer.class);
		_get("/images", Images.class);
		_get("/image/:imageid", ImageDetails.class);
		_get("/image/:imageid/delete-similar", DeleteSimilarImages.class);
		_get("/manage/new", NewContainer.class);
		_get("/manage/savenew", new NewContainerSave(config.get("imagePrefix")));
		_get("/manage/container/:container", EditContainer.class);
		_get("/manage/container/:container/save", SaveContainer.class);
		_get("/manage/container/:container/delete", ManageDeleteContainer.class);
		_get("/manage/container/:container/deletequery", ManageDeleteContainerQuery.class);
		_get("/manage/container/:container/copy", ManageCopyContainer.class);
		_get("/containertable", ContainerTable.class);
		_get("/container/:container/update", UpdateContainer.class);
		_get("/rest/_info", Info.class);
		_get("/rest/_healthcheck", HealthCheck.class);
	}

	public static void main(String[] args) {
		new OceanGroundApp().start(VERSION);
	}

	@Override
	protected void initDatabase() {
		System.out.println("connecting to database...");
		Database.open(Container.class, KnownUser.class);
	}

	@Override
	protected void init() {
		initAuth();
		initApp();
	}

	private void initAuth() {
		if ("false".equals(config.get("auth"))) {
			Logger.warn("auth off");
			auth = new NoOpAuthPlugin();
			return;
		}
		String idOfAllowedUser = config.get("IdOfAllowedUser");
		if (idOfAllowedUser == null || idOfAllowedUser.trim().isEmpty()) {
			throw new RuntimeException("Missing config IdOfAllowedUser!");
		} else if (!idOfAllowedUser.contains("#")) {
			throw new RuntimeException("IdOfAllowedUser has wrong format!");
		}
		final String w[] = idOfAllowedUser.split("#"); // Service#UserId
		auth = new SchildAuthPlugin(new RememberMeInMongoDB()) {
			@Override
			protected de.mwvb.maja.auth.Authorization getAuthorization() {
				return new OneUserAuthorization(w[1].trim(), w[0].trim());
			}
		};
	}
	
	private void initApp() {
		if (config.isDevelopment()) {
    		docker = new WindowsDocker();
    	} else {
    		docker = new UnixDocker();
    	}
		OGImage.standardImageAuthor = config.get("standardImageAuthor");
	}

	@Override
	protected void initAction(Request req, Action action) {
		action.put("user", AuthPlugin.getUser(req.session()));
	}
	
	public static String getUserId(Request req) {
		String userId = AuthPlugin.getUserId(req.session());
		if (userId == null || userId.isEmpty()) {
			throw new RuntimeException("User Id ist leer!"); // Programmschutz
		}
		return userId;
	}
}
