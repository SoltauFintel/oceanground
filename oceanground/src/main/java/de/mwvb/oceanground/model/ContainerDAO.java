package de.mwvb.oceanground.model;

import java.util.List;

import de.mwvb.maja.mongo.AbstractDAO;

public class ContainerDAO extends AbstractDAO<Container> {

	@Override
	protected Class<Container> getEntityClass() {
		return Container.class;
	}

	@Override
	public List<Container> list() {
		List<Container> list = createQuery().asList();
		list.sort((a,b) -> a.getContainer().toLowerCase().compareTo(b.getContainer().toLowerCase()));
		return list;
	}

	public Container findByContainer(String container) {
		return createQuery().field("container").equal(container).get();
	}
}
