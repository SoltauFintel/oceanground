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
		put("totalMemory", "");
		List<OGContainer> cl = new ArrayList<>();
		try {
			cl = OceanGroundApp.docker.ps();
			
			if ("1".equals(req.queryParams("mem"))) {
				Logger.debug("getting used memory for all containers");
				cl.forEach(c -> c.setMemoryUsage(OceanGroundApp.docker.stats(c.getId())));
				int totalMemory = cl.stream().mapToInt(c -> c.getMemoryUsage().contains("?") ? 0 :
					Integer.parseInt(c.getMemoryUsage().replace(" MB", "")))
						.sum();
				put("totalMemory", "total memory: " + totalMemory + " MB");
			}
		} catch (Exception e) { // Fehler schlucken und dann leere Seite anzeigen
			Logger.error(e);
		}
		put("list", cl);
	}
}
