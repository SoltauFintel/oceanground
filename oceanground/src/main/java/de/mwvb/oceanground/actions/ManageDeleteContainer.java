package de.mwvb.oceanground.actions;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.web.ActionBase;
import de.mwvb.oceanground.OceanGroundApp;
import de.mwvb.oceanground.model.Container;
import de.mwvb.oceanground.model.ContainerDAO;

public class ManageDeleteContainer extends ActionBase {

	@Override
	public String run() {
		String container = req.params("container");
		SaveContainer.validateContainer(container);
		
		ContainerDAO dao = new ContainerDAO(OceanGroundApp.database);
		Container c = dao.findByContainer(container);
		if (c == null) {
			throw new RuntimeException("Container does not exist!");
		}
		dao.delete(c);

		Logger.info("Container removed from database: " + container);
		
		res.redirect("/");
		return "";
	}
}
