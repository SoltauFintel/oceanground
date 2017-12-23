package de.mwvb.oceanground.docker;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.VolumeBind;

public class OGContainer {
	private String id;
	private String image;
	private String command;
	private String created;
	private String status;
	private String ports = "";
	private String name;
	
	// Details
	private String entryPoint;
	private String cmd;
	private String running;
	private String backcolor = "cfc";
	private String startedAt;
	private String imageId;
	private InspectContainerResponse cr;
	private String restartPolicy;
	private Integer restartPolicyMaxRetry;
	private final List<Env> env = new ArrayList<>();
	private final List<OGBind> binds = new ArrayList<>();
	private String size;
	private String dockerHubUrl;
	public Integer publicPort;
	private String memoryUsage = "";
	private String maxMemory;
	private Integer restartCount;
	
	public OGContainer() {
	}
	
	/** 'docker ps' Constructor */
	public OGContainer(Container c) {
		this.id = c.getId();
		this.image = c.getImage();
		if (this.image == null) {
			this.image = "";
		}
		if (this.image.startsWith("sha256:")) {
			this.image = this.image.substring("sha256:".length());
			this.image = this.image.substring(0, 10);
		}
		if (this.image.length() > 80) {
			this.image = this.image.substring(0, 80);
		}
		this.command = c.getCommand();
		this.created = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(LocalDateTime.ofInstant(
				Instant.ofEpochSecond(c.getCreated()), ZoneId.systemDefault()));
		this.status = c.getStatus();
		this.backcolor = this.status.toLowerCase().contains("up") ? "cfc" : "fdd";
		this.ports = PortsBuilder.buildPortString(c);
		this.name = c.getNames()[0].substring(1);
	}

	/** Docker Details Page Constructor */
	public OGContainer(InspectContainerResponse c, InspectImageResponse image) {
		cr = c;
		id = c.getId();
		name = c.getName();
		if (name.startsWith("/")) {
			name = name.substring(1);
		}
		created = transformDate(c.getCreated());
		startedAt = transformDate(c.getState().getStartedAt());
		running = c.getState().getStatus();
		restartPolicy = c.getHostConfig().getRestartPolicy().getName();
		restartPolicyMaxRetry = c.getHostConfig().getRestartPolicy().getMaximumRetryCount();
		
		PortsBuilder portsBuilder = new PortsBuilder(c);
		ports = portsBuilder.getPorts();
		publicPort = portsBuilder.getPublicPort();

		ImageInfo j = new ImageInfo(c, image);
		imageId = j.getImageId();
		this.image = j.getImage();
		size = j.getSize();
		dockerHubUrl = j.getDockerHubUrl();

		if (c.getConfig().getEntrypoint() != null && c.getConfig().getEntrypoint().length > 0) {
			entryPoint = c.getConfig().getEntrypoint()[0];
		}
		if (c.getConfig().getCmd() != null && c.getConfig().getCmd().length > 0) {
			cmd = c.getConfig().getCmd()[0];
		}
		
		for (String e : c.getConfig().getEnv()) {
			int o = e.indexOf("=");
			env.add(new Env(e.substring(0, o), e.substring(o + 1)));
		}
		
		if (c.getHostConfig().getBinds().length > 0) {
			for (Bind bind : c.getHostConfig().getBinds()) {
				binds.add(new OGBind(bind));
			}
		}
		
		if (c.getHostConfig().getMemory() < 1) {
			this.maxMemory = "-";
		} else if (c.getHostConfig().getMemory() >= (1024l * 1024l)) {
			long memory = c.getHostConfig().getMemory() / (1024l * 1024l);
			this.maxMemory = memory + " MB";
		} else {
			this.maxMemory = c.getHostConfig().getMemory() + " Bytes";
		}
		restartCount = c.getRestartCount();
	}
	
	public static String transformDate(String d) {
		if (d != null && d.length() >= "2017-04-30T20:08:22".length()) {
			//2017-04-30T20:08:22.438402855Z
			//012345678901234567890123456789
			d = d.substring(8, 10) + "." + d.substring(5, 7) + "." + d.substring(0, 4) + " " + d.substring(11, 19);
		}
		return d;
	}

	public void shorty() {
		id = pad("CONTAINER ID".equals(id) ? id : id.substring(0, 10), 30);
		name = pad(name, 30);
		image = pad(image, 33);
		command = pad(command, 40);
		status = pad(status, 20);
		ports = pad(ports, 20);
	}

	private String pad(String text, int len) {
		if (text.length() > len) {
			return text.substring(0, len);
		}
		while (text.length() < len) {
			text += " ";
		}
		return text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPorts() {
		return ports;
	}

	public void setPorts(String ports) {
		this.ports = ports;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEntryPoint() {
		return entryPoint;
	}

	public String getCmd() {
		return cmd;
	}

	public String getRunning() {
		return running;
	}

	public String getStartedAt() {
		return startedAt;
	}

	public String getImageId() {
		return imageId;
	}
	
	public VolumeBind[] getVolumes() {
		return cr.getVolumes();
	}
	
	public String getRestartPolicy() {
		return restartPolicy;
	}

	public String getRestartPolicyMaxRetry() {
		return restartPolicyMaxRetry == null ? "" : "" + restartPolicyMaxRetry;
	}

	public List<Env> getEnv() {
		return env;
	}

	public List<OGBind> getBinds() {
		return binds;
	}

	public String getSize() {
		return size;
	}
	
	public String getBackcolor() {
		return backcolor;
	}

	public String getDockerHubUrl() {
		return dockerHubUrl;
	}

	public Integer getPublicPort() {
		return publicPort;
	}

	public String getMemoryUsage() {
		return memoryUsage;
	}

	public void setMemoryUsage(String memoryUsage) {
		this.memoryUsage = memoryUsage;
	}

	public String getMaxMemory() {
		return maxMemory;
	}

	public String getRestartCount() {
		return restartCount == null ? "?" : "" + restartCount;
	}
}
