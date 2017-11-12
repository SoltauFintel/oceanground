package de.mwvb.oceanground.actions;

import de.mwvb.maja.web.Action;

public class ManageDeleteContainerQuery extends Action {

	@Override
	protected void execute() {
		String container = req.params("container");
		SaveContainer.validateContainer(container);
		
		put("container", container);
	}
}
