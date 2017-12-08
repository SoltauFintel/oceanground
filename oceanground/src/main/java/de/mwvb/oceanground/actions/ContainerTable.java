package de.mwvb.oceanground.actions;

import java.util.List;

import de.mwvb.maja.web.Action;
import de.mwvb.oceanground.model.Container;
import de.mwvb.oceanground.model.ContainerDAO;

public class ContainerTable extends Action {

	@Override
	protected void execute() {
		ContainerDAO dao = new ContainerDAO();
		List<Container> containers = dao.list();
		put("compact", false);
		if ("port".equals(req.queryParams("sort"))) {
			containers.sort((a,b) -> a.getPortHost() - b.getPortHost());
			put("compact", true);
		}
		put("containers", containers);
	}
}
