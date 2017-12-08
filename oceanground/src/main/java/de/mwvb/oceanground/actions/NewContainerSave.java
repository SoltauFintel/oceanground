package de.mwvb.oceanground.actions;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.mongo.AbstractDAO;
import de.mwvb.maja.web.ActionBase;
import de.mwvb.oceanground.model.Container;
import de.mwvb.oceanground.model.ContainerDAO;

public class NewContainerSave extends ActionBase {
	private final String imagePrefix;

	public NewContainerSave(String imagePrefix) {
		this.imagePrefix = imagePrefix;
	}

	@Override
	public String run() {
		String container = req.queryParams("container").trim();
		String image = req.queryParams("image").trim();
		String portHost = req.queryParams("portHost").trim();
		String portContainer = req.queryParams("portContainer").trim();
		String env = req.queryParams("env").trim(); // $unsafe
		String pathMappingsText = req.queryParams("pathMappingsText").trim(); // $unsafe

		validateContainer(container);
		image = validateImage(image, container);
		if (portHost.isEmpty() && portContainer.isEmpty()) {
			throw new RuntimeException("Please enter port!");
		}
		if (portHost.isEmpty() && !portContainer.isEmpty()) {
			portHost = portContainer;
		}
		if (!portHost.isEmpty() && portContainer.isEmpty()) {
			portContainer = portHost;
		}
		Integer n_portContainer = Integer.valueOf(portContainer);
		Integer n_portHost = Integer.valueOf(portHost);
		if (n_portContainer < 0 || n_portContainer > 65535) {
			throw new RuntimeException("Container port must be in range 0 .. 65535!");
		}
		if (n_portHost < 0 || n_portHost > 65535) {
			throw new RuntimeException("Host port must be in range 0 .. 65535!");
		}
		SaveContainer.validateEnv(env);
		
		ContainerDAO dao = new ContainerDAO();
		Container c = new Container();
		c.setId(AbstractDAO.code6(AbstractDAO.genId()));
		c.setContainer(container);
		c.setImage(image);
		c.setPortContainer(n_portContainer);
		c.setPortHost(n_portHost);
		c.setEnv(env);
		c.setPathMappings(SaveContainer.getPathMappings(pathMappingsText));
		dao.save(c);
		Logger.info("Saved new container (#" + c.getId() + ") " + c.getContainer() + " -> " + c.getImage());
		
		res.redirect("/");
		return "";
	}

	private void validateContainer(String container) {
		if (container.isEmpty()) {
			throw new RuntimeException("Please enter container!");
		}
		for (int i = 0; i < container.length(); i++) {
			char c = container.charAt(i);
			if (!(
					(c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
						|| (c == '_' && i > 0) || (c == '-' && i > 0) || (c == '.' && i > 0)
					)) {
				throw new RuntimeException("Container name contains illegal character!");
			}
		}
	}

	private String validateImage(String image, String container) {
		if (image.isEmpty()) {
			image = imagePrefix + container;
		}
		for (int i = 0; i < image.length(); i++) {
			char c = image.charAt(i);
			if (!(
					(c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
						|| (c == '_' && i > 0) || (c == '-' && i > 0) || (c == '.' && i > 0)
						|| (c == ':' && i > 0) || (c == '/' && i > 0)
					)) {
				throw new RuntimeException("Image name contains illegal character!");
			}
		}
		return image;
	}
}
