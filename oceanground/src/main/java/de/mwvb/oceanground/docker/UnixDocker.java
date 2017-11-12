package de.mwvb.oceanground.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

public class UnixDocker extends AbstractDocker {
	
	// TODO Per AppConfig konfigurierbar machen
	
	@Override
	protected DockerClient createClient() {
		System.out.println("UnixDocker: unix:///var/run/docker.sock, API 1.26");
		DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
				.withDockerHost("unix:///var/run/docker.sock")
				.withApiVersion("1.26")
				.withRegistryUrl("https://index.docker.io/v1/")
				.build();
		return DockerClientBuilder.getInstance(config).build();
	}
}
