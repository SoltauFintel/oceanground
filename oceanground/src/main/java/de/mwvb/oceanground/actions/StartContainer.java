package de.mwvb.oceanground.actions;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.web.Action;
import de.mwvb.oceanground.OceanGroundApp;

public class StartContainer extends Action {

	@Override
	protected void execute() {
		String containerId = req.params("id");

		OceanGroundApp.docker.startContainer(containerId);
		
		String msg = "Container wurde gestartet.";
		put("msg", msg);
		Logger.info(msg);
		put("title", "Container starten - " + OceanGroundApp.TITLE);
	}
}
