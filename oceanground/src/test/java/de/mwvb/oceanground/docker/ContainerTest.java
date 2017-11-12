package de.mwvb.oceanground.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.VolumeBind;

public class ContainerTest {

//	@Test
	public void containerInfos() {
		AbstractDocker docker = new WindowsDocker();
		InspectContainerResponse c = docker.inspectContainer("gettvprogramm");
		
		System.out.println("Container: " + c.getName());
		System.out.println("Container ID: " + c.getId());
		
		System.out.println("läuft? " + c.getState().getStatus());
		System.out.println("started at: " + c.getState().getStartedAt());
		// up 3 hours <== schwer
		
		System.out.println("created: " + c.getCreated());
		
		for (String arg : c.getArgs()) {
			System.out.println("arg: " + arg);
		}
		
		System.out.println("\nExposed ports:");
		for (ExposedPort ep : c.getConfig().getExposedPorts()) {
			System.out.println("- port: " + ep.getPort() + " | scheme: " + ep.getProtocol().name());
		}
		
		// ports
		if (c.getConfig().getPortSpecs() != null) {
			System.out.println();
			for (String p : c.getConfig().getPortSpecs()) {
				System.out.println("port spec: " + p);
			}
		}

		if (c.getHostConfig().getBinds().length > 0) {
			System.out.println();
			System.out.println("Binds:");
			for (Bind bind : c.getHostConfig().getBinds()) {
				System.out.println("Bind: path: " + bind.getPath() + " | volume path: " + bind.getVolume().getPath());
			}
		}
		
		// image
		System.out.println();
		System.out.println("Image ID: " + c.getImageId());
		InspectImageResponse image = docker.inspectImage(c.getImageId());
		for (String name : image.getRepoTags()) {
			System.out.println("- Image: " + name);
		}
		
		//  -> image infos
		if (c.getConfig().getCmd() != null)
			for (String cmd : c.getConfig().getCmd()) {
				System.out.println("- CMD: " + cmd);
			}
		if (c.getConfig().getEntrypoint() != null)
			for (String ep : c.getConfig().getEntrypoint()) {
				System.out.println("- EntryPoint: " + ep);
			}
		System.out.println("- created: " + c.getCreated());
		
		// volumes
		if (c.getVolumes() != null) {
			System.out.println();
			System.out.println("Volumes:");
			for (VolumeBind volumeBind : c.getVolumes()) {
				System.out.println("Volume bind (H->C): " + volumeBind.getHostPath() + " -> " + volumeBind.getContainerPath());
			}
		}
		
		System.out.println();
		System.out.println("Restart policy: " + c.getHostConfig().getRestartPolicy().getName());
		System.out.println("Restart policy -> getMaximumRetryCount: " + c.getHostConfig().getRestartPolicy().getMaximumRetryCount());
		
		System.out.println();
		for (String env : c.getConfig().getEnv()) {
			System.out.println("ENV: " + env);
		}
		
		new OGContainer(c, image);
	}
}
