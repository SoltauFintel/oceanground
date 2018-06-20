package de.mwvb.oceanground.actions;

import com.github.dockerjava.api.exception.NotFoundException;

import de.mwvb.maja.web.Action;
import de.mwvb.oceanground.OceanGroundApp;

public class ContainerConsole extends Action {
	
	@Override
	protected void execute() {
		String container = req.params("container");
		try {
			put("container", container);
			put("title", container + " (stdout) - " + OceanGroundApp.TITLE);
			
//			InspectContainerResponse c_ = OceanGroundApp.docker.inspectContainer(container);
//			InspectImageResponse image = OceanGroundApp.docker.inspectImage(c_.getImageId());
//			OGContainer c = new OGContainer(c_, image);
//			put("c", c);
			
			put("output", OceanGroundApp.docker.logs(container, false, true));
			
		} catch (NotFoundException e) {
			throw new RuntimeException("Container \"" + container + "\" nicht vorhanden!");
		}
	}
}
