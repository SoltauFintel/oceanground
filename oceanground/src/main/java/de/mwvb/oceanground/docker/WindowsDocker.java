package de.mwvb.oceanground.docker;

import java.io.File;
import java.io.IOException;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

// Nur für lokale Entwicklung auf Windows 10 PC.
public class WindowsDocker extends AbstractDocker {

	@Override
	protected DockerClient createClient() {
		try {
			System.out.println("WindowsDocker: tcp://192.168.99.100:2376, API 1.24");
			DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
					.withDockerHost("tcp://192.168.99.100:2376")
					.withDockerTlsVerify(true)
					.withDockerCertPath(new File(System.getProperty("user.home"), ".docker/machine/machines/default").getCanonicalPath())
					.withApiVersion("1.24")
					.withRegistryUrl("https://index.docker.io/v1/")
					.build();
			return DockerClientBuilder.getInstance(config).build();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
