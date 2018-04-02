package de.mwvb.oceanground.actions;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.web.Action;
import de.mwvb.oceanground.OceanGroundApp;

public class StopContainer extends Action {

	@Override
	protected void execute() {
		String containerId = req.params("id");

		OceanGroundApp.docker.stopContainer(containerId);
		
		String msg = "Container wurde gestoppt.";
		put("msg", msg);
		Logger.info(msg);
		put("title", "Container stoppen - " + OceanGroundApp.TITLE);
	}
}
