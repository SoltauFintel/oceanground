package de.mwvb.oceanground.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;

import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;

/**
 * Containerdefinition
 */
@Entity
@Indexes({ @Index(fields = {@Field("container")}, options = @IndexOptions(unique = true)) })
public class Container {
	@Id
	private String id;
	/** Containername (Fachlicher Schlüssel) */
	private String container;
	private String image;
	private int portContainer;
	private int portHost;
	private String env;
	private List<PathMapping> pathMappings = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getPortContainer() {
		return portContainer;
	}

	public void setPortContainer(int portContainer) {
		this.portContainer = portContainer;
	}

	public int getPortHost() {
		return portHost;
	}

	public void setPortHost(int portHost) {
		this.portHost = portHost;
	}

	public String getEnv() {
		return env;
	}

	public String getEnvHtml() {
		if (env == null) {
			return "";
		}
		return env.replace(",", "<br/>");
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public List<PathMapping> getPathMappings() {
		return pathMappings;
	}

	public String getPathMappingsText() {
		return pathMappings.stream().map(m -> m.toString()).collect(Collectors.joining("\n"));
	}
	
	public String getPathMappingsHtml() {
		return pathMappings.stream().map(m -> m.toString()).collect(Collectors.joining("<br/>"));
	}

	public List<Bind> getBinds() {
		List<Bind> binds = new ArrayList<>();
		for (PathMapping pathMapping : getPathMappings()) {
			String hostPath = pathMapping.getHostPath();
			Volume volume = new Volume(pathMapping.getContainerPath());
			AccessMode accessmode = pathMapping.isReadOnly() ? AccessMode.ro : AccessMode.rw;
			binds.add(new Bind(hostPath, volume, accessmode));
		}
		return binds;
	}

	public void setPathMappings(List<PathMapping> pathMappings) {
		if (pathMappings == null) {
			throw new NullPointerException("Arg pathMappings must not be null!");
		}
		this.pathMappings = pathMappings;
	}
}
