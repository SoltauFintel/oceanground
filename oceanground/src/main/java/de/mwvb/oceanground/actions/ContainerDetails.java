package de.mwvb.oceanground.actions;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.NotFoundException;

import de.mwvb.maja.web.Action;
import de.mwvb.maja.web.AppConfig;
import de.mwvb.oceanground.OceanGroundApp;
import de.mwvb.oceanground.docker.OGContainer;

public class ContainerDetails extends Action {
	private final String webhost;
	private String restFolder;
	private String restFolderAlternative;
	
	public ContainerDetails() {
		AppConfig config = new AppConfig();
		this.webhost = config.get("webhost");
		this.restFolder = config.get("restfolder", "/rest/");
		this.restFolderAlternative = config.get("restfolder2", "/rest/_");
	}
	
	@Override
	protected void execute() {
		String container = req.params("container");
		try {
			put("container", container);
			put("title", container + " - " + OceanGroundApp.TITLE);
			
			InspectContainerResponse c_ = OceanGroundApp.docker.inspectContainer(container);
			InspectImageResponse image = OceanGroundApp.docker.inspectImage(c_.getImageId());
			put("memoryUsage", OceanGroundApp.docker.stats(container));
			OGContainer c = new OGContainer(c_, image);
			put("c", c);
			
			put("output", OceanGroundApp.docker.logs(container, false));
			put("erroroutput", OceanGroundApp.docker.logs(container, true));
			
			put("ping", getPing(c.getPublicPort()));
			put("info", getInfo(c.getPublicPort()));
		} catch (NotFoundException e) {
			throw new RuntimeException("Container \"" + container + "\" nicht vorhanden!");
		}
	}

	private String getPing(Integer port) {
		return loadPage(port, "ping", false);
	}

	private String getInfo(Integer port) {
		return loadPage(port, "info", true);
	}

	private String loadPage(Integer port, String part, boolean prependUrl) {
		if (port == null) {
			return null;
		}
		String host = "http://" + webhost + ":";
		String url = host + port + restFolder + part;
		String result = loadWebPage(url);
		if (isError(result)) {
			url = host + port + restFolderAlternative + part;
			result = loadWebPage(url);
			if (isError(result)) {
				return null;
			}
		}
		if (result == null) {
			return result;
		}
		int o = result.indexOf("<body>");
		if (o >= 0) {
			o += "<body>".length();
		}
		if (o >= 0) {
			result = result.substring(o);
		}
		result = removeHtml(result);
		return prependUrl ? url + "\n" + result : result;
	}

	private String loadWebPage(String pUrl) {
		try {
			URL url = new URL(pUrl);
			URLConnection con = url.openConnection();
			Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
			Matcher m = p.matcher(con.getContentType());
			String charset = m.matches() ? m.group(1) : "ISO-8859-1";
			Reader r = new InputStreamReader(con.getInputStream(), charset);
			StringBuilder buf = new StringBuilder();
			while (true) {
				int ch = r.read();
				if (ch < 0) break;
				buf.append((char) ch);
			}
			return buf.toString();
		} catch (FileNotFoundException e) {
			// There's nothing, return nothing.
			return null;
		} catch (Exception e) {
			return e.getClass().getName() + ": " + e.getMessage();
		}
	}
	
	private String removeHtml(String text) {
		return text.replace("<br>", "\n").replace("<br/>", "\n").replace("<br />", "\n")
				.replace("<h2>", "**** ")
				.replace("</h2>", " ****")
				.replace("<p>", "")
				.replace("</p>", "")
				.replace("<b>", "")
				.replace("</b>", "")
				.replace("</body>", "")
				.replace("<span>", "")
				.replace("</span>", "")
				.replace("<", "&lt;").replace(">", "&gt;");
	}

	private boolean isError(String result) {
		return result == null || result.isEmpty() || result.contains("404") || result.contains("401")
				|| result.contains("HTTP response code")
				|| result.contains("FileNotFound");
	}
	
	public static String makeLink(String container) {
		return "/container/" + container;
	}
}
