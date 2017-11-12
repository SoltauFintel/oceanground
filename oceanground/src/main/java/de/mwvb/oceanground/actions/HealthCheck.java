package de.mwvb.oceanground.actions;

import de.mwvb.maja.web.ActionBase;
import de.mwvb.oceanground.OceanGroundApp;
import de.mwvb.oceanground.model.ContainerDAO;

public class HealthCheck extends ActionBase {

	@Override
	public String run() {
		if (OceanGroundApp.docker.version().isEmpty()) {
			return "ERROR: Docker access may not be possible";
		}
		try {
			new ContainerDAO(OceanGroundApp.database).size();
		} catch (Exception e) {
			return "ERROR: Database access is not possible";
		}
		return "ok";
	}
}
