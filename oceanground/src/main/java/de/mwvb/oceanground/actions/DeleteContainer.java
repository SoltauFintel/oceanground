package de.mwvb.oceanground.actions;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.web.Action;
import de.mwvb.oceanground.OceanGroundApp;

public class DeleteContainer extends Action {

	@Override
	protected void execute() {
		String containerId = req.params("id");
		boolean force = "1".equals(req.queryParams("force"));
		boolean removeImage = "1".equals(req.queryParams("image"));
		boolean restart = "1".equals(req.queryParams("restart"));

		String imageId = null;
		String msg;
		if (restart) {
			OceanGroundApp.docker.restartContainer(containerId);
			msg = "Container wurde durchgestartet.";
		} else {
			if (removeImage) {
				imageId = OceanGroundApp.docker.inspectContainer(containerId).getImageId();
			}
			OceanGroundApp.docker.deleteContainer(containerId, force);
			msg = "Container wurde gelöscht.";
			if (removeImage) {
				OceanGroundApp.docker.deleteImage(imageId);
				msg += " Image wurde gelöscht.";
			}
		}
		
		put("title", "Container löschen - " + OceanGroundApp.TITLE);
		put("msg", msg);
		Logger.info(msg);
	}
}
