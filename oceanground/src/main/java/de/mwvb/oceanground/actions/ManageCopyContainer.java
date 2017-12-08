package de.mwvb.oceanground.actions;

import de.mwvb.maja.mongo.AbstractDAO;
import de.mwvb.maja.web.ActionBase;
import de.mwvb.oceanground.model.Container;
import de.mwvb.oceanground.model.ContainerDAO;

public class ManageCopyContainer extends ActionBase {

	@Override
	public String run() {
		String container = req.params("container");
		SaveContainer.validateContainer(container);
		
		ContainerDAO dao = new ContainerDAO();
		Container c = dao.findByContainer(container);
		if (c == null) {
			throw new RuntimeException("Container does not exist!");
		}
		c.setId(AbstractDAO.code6(AbstractDAO.genId()));
		c.setContainer(c.getContainer() + "_copy");
		dao.save(c);
		
		res.redirect("/manage/container/" + c.getContainer());
		return "";
	}

}
