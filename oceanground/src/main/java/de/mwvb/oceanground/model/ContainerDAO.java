package de.mwvb.oceanground.model;

import java.util.List;

import de.mwvb.maja.mongo.AbstractDAO;
import de.mwvb.maja.mongo.Database;

public class ContainerDAO extends AbstractDAO<Container> {

	public ContainerDAO(Database database) {
		super(database, Container.class);
	}

	public List<Container> list() {
		List<Container> list = ds.createQuery(Container.class).asList();
		list.sort((a,b) -> a.getContainer().toLowerCase().compareTo(b.getContainer().toLowerCase()));
		return list;
	}

	public Container findByContainer(String container) {
		return ds.createQuery(Container.class).field("container").equal(container).get();
	}
}
