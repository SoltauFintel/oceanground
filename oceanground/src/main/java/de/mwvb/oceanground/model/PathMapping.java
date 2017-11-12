package de.mwvb.oceanground.model;

/**
 * Path Mapping zu einer Containerdefinition
 */
public class PathMapping {
	private String hostPath;
	private String containerPath; // volume
	private boolean readOnly = true;

	public String getHostPath() {
		return hostPath;
	}

	public void setHostPath(String hostPath) {
		this.hostPath = hostPath;
	}

	public String getContainerPath() {
		return containerPath;
	}

	public void setContainerPath(String containerPath) {
		this.containerPath = containerPath;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public String toString() {
		return (isReadOnly() ? "ro:" : "rw:") + getHostPath() + "=" + getContainerPath();
	}
}
