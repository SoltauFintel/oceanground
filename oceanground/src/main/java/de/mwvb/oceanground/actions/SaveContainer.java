package de.mwvb.oceanground.actions;

import java.util.ArrayList;
import java.util.List;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.web.ActionBase;
import de.mwvb.oceanground.model.Container;
import de.mwvb.oceanground.model.ContainerDAO;
import de.mwvb.oceanground.model.PathMapping;

public class SaveContainer extends ActionBase {

	@Override
	public String run() {
		String containerForLoad = req.params("container");
		validateContainer(containerForLoad);
		
		String container = req.queryParams("container").trim();
		String image = req.queryParams("image").trim();
		String portHost = req.queryParams("portHost").trim();
		String portContainer = req.queryParams("portContainer").trim();
		String env = req.queryParams("env").trim(); // $unsafe
		String pathMappingsText = req.queryParams("pathMappingsText").trim(); // $unsafe

		validateContainer(container);
		image = validateImage(image, container);
		if (portHost.isEmpty() || portContainer.isEmpty()) {
			throw new RuntimeException("Please enter port!");
		}
		Integer n_portContainer = Integer.valueOf(portContainer);
		Integer n_portHost = Integer.valueOf(portHost);
		if (n_portContainer < 0 || n_portContainer > 65535) {
			throw new RuntimeException("Container port must be in range 0 .. 65535!");
		}
		if (n_portHost < 0 || n_portHost > 65535) {
			throw new RuntimeException("Host port must be in range 0 .. 65535!");
		}
		validateEnv(env);
		
		ContainerDAO dao = new ContainerDAO();
		Container c = dao.findByContainer(containerForLoad);
		if (c == null) {
			throw new RuntimeException("Container does not exist!");
		}
		c.setContainer(container);
		c.setImage(image);
		c.setPortContainer(n_portContainer);
		c.setPortHost(n_portHost);
		c.setEnv(env);
		c.setPathMappings(getPathMappings(pathMappingsText));
		dao.save(c);
		Logger.info("Saved container (#" + c.getId() + ") " + c.getContainer() + " -> " + c.getImage());
		
		res.redirect("/");
		return "";
	}

	public static void validateContainer(String container) {
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
			throw new RuntimeException("Please enter image!");
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

	public static void validateEnv(String env) {
		for (String line : env.split(",")) {
			int o = line.indexOf("=");
			if (o < 0) {
				throw new RuntimeException("Missing = in env!");
			}
			String left = line.substring(0, o).trim();
			if (left.isEmpty()) {
				throw new RuntimeException("Empty env var name is not okay!");
			}
		}
	}

	public static List<PathMapping> getPathMappings(String pathMappingsText) {
		List<PathMapping> pathMappings = new ArrayList<>();
		for (String line : pathMappingsText.replace("\r", "").split("\n")) {
			if (!line.trim().isEmpty()) {
				boolean readOnly = true;
				if (line.startsWith("rw:")) {
					readOnly = false;
				} else if (line.startsWith("ro:")) {
					readOnly = true;
				} else {
					throw new RuntimeException("Path Mapping muss mit 'rw:' oder 'ro:' anfangen!");
				}
				line = line.substring("rw:".length());
				String hostPath = line;
				String containerPath = "";
				int o = line.indexOf("=");
				if (o >= 0) {
					hostPath = line.substring(0, o).trim();
					containerPath = line.substring(o + 1).trim();
				}
				PathMapping pathMapping = new PathMapping();
				pathMapping.setHostPath(hostPath);
				pathMapping.setContainerPath(containerPath);
				pathMapping.setReadOnly(readOnly);
				pathMappings.add(pathMapping);
			}
		}
		return pathMappings;
	}
}
