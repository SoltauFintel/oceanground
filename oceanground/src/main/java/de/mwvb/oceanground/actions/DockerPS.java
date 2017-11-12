package de.mwvb.oceanground.actions;

import java.util.ArrayList;
import java.util.List;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.web.Action;
import de.mwvb.oceanground.OceanGroundApp;
import de.mwvb.oceanground.docker.OGContainer;

public class DockerPS extends Action {
	
	@Override
	protected void execute() {
		List<OGContainer> cl = new ArrayList<>();
		try {
			cl = OceanGroundApp.docker.ps();
		} catch (Exception e) { // Fehler schlucken und dann leere Seite anzeigen
			Logger.error(e);
		}
		put("list", cl);
	}
}
