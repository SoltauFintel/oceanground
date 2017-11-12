package de.mwvb.oceanground.actions;

import de.mwvb.maja.web.Action;
import de.mwvb.oceanground.OceanGroundApp;
import de.mwvb.oceanground.model.Container;
import de.mwvb.oceanground.model.ContainerDAO;

public class EditContainer extends Action {

	@Override
	protected void execute() {
		String container = req.params("container");
		SaveContainer.validateContainer(container);
		ContainerDAO dao = new ContainerDAO(OceanGroundApp.database);
		Container c = dao.findByContainer(container);
		put("c", c);
	}
}
