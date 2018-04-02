package de.mwvb.oceanground.docker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.pmw.tinylog.Logger;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.LogConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports.Binding;
import com.github.dockerjava.api.model.RestartPolicy;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.api.model.Version;
import com.github.dockerjava.core.command.PullImageResultCallback;

import de.mwvb.maja.web.AppConfig;
import de.mwvb.oceanground.model.MaxMemory;

public abstract class AbstractDocker {
	private final DockerClient docker;
	private final Map<String, InspectImageResponse> imageCache = new HashMap<>();

	public AbstractDocker() {
		docker = createClient();
	}
	
	protected abstract DockerClient createClient();
	
	public List<OGContainer> ps() {
		List<Container> list = docker.listContainersCmd().withShowAll(false).exec();
		return verpackeContainer(list);
	}

	public List<OGContainer> psAll() {
		List<Container> list = docker.listContainersCmd().withShowAll(true).exec();
		return verpackeContainer(list);
	}

	private List<OGContainer> verpackeContainer(List<Container> list) {
		List<OGContainer> ret = new ArrayList<>();
		for (Container c : list) {
			ret.add(new OGContainer(c));
		}
		return ret;
	}

	public List<Image> images() {
		return docker.listImagesCmd().exec();
	}
	
	public InspectImageResponse inspectImage(String imageId) {
		InspectImageResponse r = imageCache.get(imageId);
		if (r == null) {
			r = docker.inspectImageCmd(imageId).exec();
			imageCache.put(imageId, r);
		}
		return r;
	}

	public void run(String container, String image, int port, String env, List<Bind> binds, MaxMemory maxMemory) {
		run(container, image, port, port, env, binds, maxMemory);
	}
	
	public void run(String container, String image, int portContainer, int portHost, String env, List<Bind> binds, MaxMemory maxMemory) {
		PortBinding ports = new PortBinding(Binding.bindPort(portHost), ExposedPort.tcp(portContainer));
		CreateContainerCmd cmd = docker.createContainerCmd(image)
				.withName(container)
				.withPortBindings(ports)
				.withEnv(env.split(","))
				.withBinds(binds)
				.withRestartPolicy(RestartPolicy.alwaysRestart()); // TODO evtl. in Containerdefinition konfigurierbar machen
		if (!"false".equals(new AppConfig().get("with-log-opts"))) {
			cmd = cmd.withLogConfig(getLogConfig()); // TO-DO evtl. in Containerdefinition konfigurierbar machen
		}
		if (maxMemory != null) {
			cmd = cmd.withMemory(maxMemory.getBytes());
		}
		CreateContainerResponse k = cmd.exec();
		if (k != null && k.getWarnings() != null && k.getWarnings().length > 0) {
			Logger.warn("DOCKER RUN \"" + container + "\" produced " + k.getWarnings().length + " warning(s):");
			for (String warning : k.getWarnings()) {
				Logger.warn(warning);
			}
		}
		docker.startContainerCmd(container).exec();
	}
	
	public void pull(String image) {
		docker.pullImageCmd(image).exec(new PullImageResultCallback()).awaitSuccess();
	}

	public void rm_force(String container) {
		try {
			docker.removeContainerCmd(container).withForce(true).exec();
		} catch (NotFoundException e) {
		}
	}
	
	public InspectContainerResponse inspectContainer(String id) {
		return docker.inspectContainerCmd(id).exec();
	}
	
	public String logs(String container, boolean error) {
		try {
			LogContainerCallback log = new LogContainerCallback();
			docker.logContainerCmd(container).withStdOut(!error).withStdErr(error)
				.withTail(300) // max. die jüngsten 300 Zeilen
				.exec(log);
			if (log.awaitCompletion(10, TimeUnit.SECONDS)) {
				String logtext = log.toString();
				/*int size = logtext.length();
				int zeilen = size == 0 ? 0 : logtext.split("\n").length;
				String p = " lines";
				if (zeilen == 1) {
					p = " line";
				}
				System.out.println("- " + Strings.padEnd(container, 25, ' ') + " | " + (error ? "err" : "out") + " | "
						+ Strings.padStart("" + size, 5, ' ') + " characters" + " | "
						+ Strings.padStart("" + zeilen, 3, ' ') + p + " of max. 300");*/
				return logtext.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
			} else {
				//System.out.println("- " + container + " | " + (error ? "err" : "out") + " | ERROR: Timeout nach 10 Sekunden");
				return "ERROR: Timeout getting log";
			}
		} catch (InterruptedException e) {
			Logger.error(e);
			return "ERROR (see log)";
		}
	}
	
	public String version() {
		try {
			Version v = docker.versionCmd().exec();
			return v.getVersion() + "; " + v.getApiVersion() + "; " + v.getOperatingSystem() + "; " + v.getKernelVersion();
		} catch (Exception e) {
			Logger.error(e);
			return "";
		}
	}
	
	protected LogConfig getLogConfig() {
		Map<String, String> config = new HashMap<>();
		config.put("max-size", "1m");
		config.put("max-file", "3");
		return new LogConfig(LogConfig.LoggingType.DEFAULT, config);
	}
	
	public int getImageCacheSize() {
		return imageCache.size();
	}
	
	public List<SearchItem> searchImage(String image) {
		try {
			return docker.searchImagesCmd(image).exec();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public void deleteImage(String id) {
		docker.removeImageCmd(id).exec();
	}
	
	public void deleteContainer(String id, Boolean force) {
		docker.removeContainerCmd(id).withForce(force).exec();
	}

	public void startContainer(String id) {
		docker.startContainerCmd(id).exec();
	}

	public void restartContainer(String id) {
		docker.restartContainerCmd(id).exec();
	}

	public void stopContainer(String id) {
		docker.stopContainerCmd(id).exec();
	}

	public String stats(String id) {
		StatsCmd c = docker.statsCmd(id);
		StatsCallback cb = new StatsCallback();
		c.exec(cb);
		int n = 0;
		while ("?".equals(cb.usage)) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				return "???";
			}
			if (++n > 50) return "??";
		}
		return cb.usage;
	}
}
