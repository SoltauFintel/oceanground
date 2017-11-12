package de.mwvb.oceanground.docker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.SearchItem;

import de.mwvb.oceanground.OceanGroundApp;

public class OGImage {
	public static String standardImageAuthor;
	private final InspectImageResponse ii;

	public OGImage(InspectImageResponse inspectImage) {
		this.ii = inspectImage;
	}

	public String getId() {
		String id = ii.getId();
		if (id == null) {
			return "";
		} else if (id.startsWith("sha256:")) {
			id = id.substring("sha256:".length());
			id = id.substring(0, 12);
		}
		return id;
	}

	public String getLongId() {
		String id = ii.getId();
		if (id == null) {
			return "";
		} else if (id.startsWith("sha256:")) {
			id = id.substring("sha256:".length());
		}
		return id;
	}

	public String getName() {
		if (ii.getRepoTags() == null || ii.getRepoTags().isEmpty()) {
			return "";
		}
		return ii.getRepoTags().get(0).replace("<", "&lt;").replace(">", "&gt;");
	}

	public String getCreated() {
		return OGContainer.transformDate(ii.getCreated());
	}

	public String getSize() {
		long size = ii.getSize() / 1024 / 1024;
		String ret = size + " MB";
		String vs = ii.getVirtualSize() / 1024 / 1024 + " MB";
		if (!ret.equals(vs)) {
			ret += " / virtuell: " + vs;
		}
		return ret;
	}

	public String getAuthor() {
		if (ii == null) {
			return "";
		}
		String author = ii.getAuthor().trim();
		String w[] = author.split(" ");
		if (w.length > 2) {
			return w[0] + " " + w[1] + " ...";
		} else if (w.length == 2) {
			return w[0] + " " + w[1];
		} else {
			return w[0];
		}
	}

	public String getEntryPoint() {
		String ret = "";
		if (ii != null && ii.getConfig() != null && ii.getConfig().getEntrypoint() != null) {
			for (String ep : ii.getConfig().getEntrypoint()) {
				ret += ep;
			}
		}
		return ret;
	}

	public String getCmd() {
		String ret = "";
		if (ii != null && ii.getConfig() != null && ii.getConfig().getCmd() != null) {
			for (String ep : ii.getConfig().getCmd()) {
				if (!ret.isEmpty()) {
					ret += " ";
				}
				ret += ep;
			}
		}
		return ret;
	}

	public String getCommand() {
		String cmd = getCmd();
		String ep = getEntryPoint();
		if (cmd.isEmpty()) {
			cmd = ep;
		} else if (ep.isEmpty()) {
			cmd = "CMD " + cmd;
		} else if (ep.isEmpty()) {
			cmd = "CMD " + cmd + "<br/>ENTRYPOINT " + ep;
		}
		return cmd;
	}
	
	public String getVolumes() {
		String ret = "";
		if (ii != null && ii.getConfig() != null && ii.getConfig().getVolumes() != null) {
			ret = ii.getConfig().getVolumes().keySet().stream().collect(Collectors.joining(", "));
		}
		return ret;
	}
	
	public String getServer() {
		String name = getName();
		int o = name.indexOf("/");
		return o >= 0 ? name.substring(0, o) : (getDockerHubUrl().isEmpty() ? "" : "Docker Hub");
	}

	private static Map<String, List<SearchItem>> cache = new HashMap<>();

	private static List<SearchItem> search(String name) {
		List<SearchItem> erg = cache.get(name);
		if (erg == null) {
			erg = new ArrayList<>();
			erg = search2(name);
			cache.put(name, erg);
		}
		return erg;
	}

	static List<SearchItem> search2(String name) {
		return OceanGroundApp.docker.searchImage(name); // expensive
	}

	public String getDockerHubUrl() {
		String sb = getName();
		int o = sb.indexOf("/");
		String sx = "";
		if (o > 0) {
			sx = sb.substring(0, o);
			sb = sb.substring(o + 1);
		}
		o = sb.lastIndexOf(":");
		if (o >= 0) {
			sb = sb.substring(0, o);
		}
		boolean gef = false;
		if (!(sx.contains(".") || sx.contains(":"))) {
			List<SearchItem> erg = search(sb);
			for (SearchItem searchItem : erg) {
				if (searchItem.getName().equals(sb)) {
					gef = true;
					break;
				}
			}
		}
		if (!gef) {
			return "";
		}
		
		String im = getName();
		o = im.indexOf("/");
		if (o > 0) {
			String server = im.substring(0, o);
			if (!server.contains(".") && !server.contains(":")) {
				return "https://hub.docker.com/r/" + getServer() + "/" + getShortName() + "/";
			}
		} else {
			return "https://hub.docker.com/_/" + getShortName() + "/";
		}
		return "";
	}
	
	public String getShortName() {
		String name = getName();
		int o = name.indexOf("/");
		if (o >= 0) {
			name = name.substring(o + 1);
		}
		o = name.lastIndexOf(":");
		if (o >= 0) {
			name = name.substring(0, o);
		}
		if (name.isEmpty()) {
			return ii.getId();
		}
		return name;
	}

	public String getVersion() {
		String name = getName();
		int o = name.indexOf("/");
		if (o >= 0) {
			name = name.substring(o + 1);
		}
		o = name.lastIndexOf(":");
		if (o >= 0) {
			name = name.substring(o + 1);
		} else {
			name = "";
		}
		return name;
	}

	public String getSort() {
		String a;
		if (standardImageAuthor != null && !standardImageAuthor.trim().isEmpty()
				&& getAuthor().toLowerCase().contains(standardImageAuthor.toLowerCase())) {
			a = "1";
		} else if ("Docker Hub".equalsIgnoreCase(getServer())) {
			a = "2";
		} else {
			a = "3";
		}
		return a + getShortName() + getCommand();
	}
}
