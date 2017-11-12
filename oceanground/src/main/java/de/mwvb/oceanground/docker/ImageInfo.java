package de.mwvb.oceanground.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectImageResponse;

public class ImageInfo {
	private String imageId;
	private String dockerHubUrl;
	private String image;
	private String size;
	
	public ImageInfo(InspectContainerResponse c, InspectImageResponse image) {
		imageId = c.getImageId();
		if (imageId.startsWith("sha256:")) {
			imageId = imageId.substring("sha256:".length());
		}
		if (image.getRepoTags() != null && image.getRepoTags().size() > 0) {
			this.image = image.getRepoTags().get(0);
			if (image.getRepoTags().size() >= 2) {
				this.image += ", ...";
			}
			size = image.getSize() / 1024 / 1024 + " MB";
			String v = image.getVirtualSize() / 1024 / 1024 + " MB";
			if (!v.equals(size)) {
				size += " / " + v;
			}
			if (image.getRepoTags().size() >= 1) {
				String im = image.getRepoTags().get(0);
				int o = im.indexOf("/");
				if (o > 0) {
					String server = im.substring(0, o);
					if (!server.contains(".") && !server.contains(":")) {
						dockerHubUrl = "https://hub.docker.com/r/" + withoutVersion(image.getRepoTags().get(0)) + "/";
					}
				} else {
					dockerHubUrl = "https://hub.docker.com/_/" + withoutVersion(image.getRepoTags().get(0)) + "/";
				}
			}
		}
	}
	
	private String withoutVersion(String name) {
		int o = name.lastIndexOf(":");
		if (o >= 0) {
			return name.substring(0, o);
		}
		return name;
	}

	public String getImageId() {
		return imageId;
	}

	public String getDockerHubUrl() {
		return dockerHubUrl;
	}

	public String getImage() {
		return image;
	}

	public String getSize() {
		return size;
	}
}
