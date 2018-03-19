package de.mwvb.auth.schild;

import static spark.Spark.get;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.auth.AuthFeature;
import de.mwvb.maja.auth.AuthPlugin;
import de.mwvb.maja.web.AppConfig;

public class SchildAuthFeature implements AuthFeature {
	private AuthPlugin owner;
	
	@Override
	public void init(AuthPlugin owner) {
		this.owner = owner;
	}

	@Override
	public void routes() {
		AppConfig config = new AppConfig();
		String callback = "/login/schild/callback";
		String callbackUrl = config.get("host") + callback;
		String loginUrl = config.get("schild.url") + "/" + config.get("schild.key") + "?c=" + urlEncode(callbackUrl, callbackUrl);
		
		owner.addNotProtected("/login");

		get("/login/schild/cu", (req, res) -> { res.redirect(loginUrl + "&mode=cu"); return ""; });
		get("/login/schild", (req, res) -> { res.redirect(loginUrl); return ""; });
		get(callback, (req, res) -> owner.login(req, res, req.queryParams("name"), req.queryParams("id"), "Schild", true));
	}
	
	public static String urlEncode(String text, String fallback) {
		try {
			return URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Logger.error(e);
			return fallback;
		}
	}
}
