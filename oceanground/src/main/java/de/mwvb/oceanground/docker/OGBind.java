package de.mwvb.oceanground.docker;

import com.github.dockerjava.api.model.Bind;

public class OGBind {
	private final String path;
	private final String volumePath;

	public OGBind(Bind bind) {
		path = bind.getPath();
		volumePath = bind.getVolume().getPath() + ":" + bind.getAccessMode().name();
	}
	
	public String getPath() {
		return path;
	}

	public String getVolumePath() {
		return volumePath;
	}
}
