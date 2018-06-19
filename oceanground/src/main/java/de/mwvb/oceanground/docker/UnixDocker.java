package de.mwvb.oceanground.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

import de.mwvb.maja.web.AppConfig;

public class UnixDocker extends AbstractDocker {
	
	// TODO Per AppConfig konfigurierbar machen
	
	@Override
	protected DockerClient createClient() {
		System.out.println("UnixDocker: unix:///var/run/docker.sock, API 1.26");
		DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
				.withDockerHost("unix:///var/run/docker.sock")
				.withApiVersion(getApiVersion())
				.withRegistryUrl("https://index.docker.io/v1/")
				.build();
		return DockerClientBuilder.getInstance(config).build();
	}
	
	public static String getApiVersion() {
		AppConfig cfg = new AppConfig();
		return cfg.get("docker.version", "1.26");
	}
}
