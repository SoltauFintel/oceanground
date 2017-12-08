package de.mwvb.oceanground.actions;

import java.util.List;

import de.mwvb.maja.web.Action;
import de.mwvb.oceanground.OceanGroundApp;
import de.mwvb.oceanground.model.Container;
import de.mwvb.oceanground.model.ContainerDAO;

public class Index extends Action {

	@Override
	protected void execute() {
		ContainerDAO dao = new ContainerDAO();
		List<Container> containers = dao.list();
		put("containers", containers);
		put("displayEditContainerTitle", !containers.isEmpty());
		put("version", OceanGroundApp.VERSION);
	}
}
