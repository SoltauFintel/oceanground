package de.mwvb.oceanground.model;

import org.pmw.tinylog.Logger;

import de.mwvb.oceanground.OceanGroundApp;

/**
 * Container anhand einer Containerdefinition aktualisieren
 */
// Fachlogik
public class UpdateDockerContainer {

	public void update(Container c) {
		Logger.info("Update container: " + c.getContainer());
		
		Logger.debug("   docker pull " + c.getImage());
		OceanGroundApp.docker.pull(c.getImage());
		
		Logger.debug("   docker rm   " + c.getContainer());
		OceanGroundApp.docker.rm_force(c.getContainer());
		
		Logger.debug("   docker run  " + c.getContainer());
		OceanGroundApp.docker.run(c.getContainer(), c.getImage(), c.getPortContainer(), c.getPortHost(), c.getEnv(),
				c.getBinds(), new MaxMemory(c.getMaxMemory()));
		
//		try {
//			Thread.sleep(1500);
//		} catch (InterruptedException e) {
//		}
		Logger.info("Update container: " + c.getContainer() + " => complete");
	}
}
