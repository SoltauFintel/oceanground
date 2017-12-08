package de.mwvb.oceanground.actions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.mwvb.maja.web.ActionBase;
import de.mwvb.oceanground.model.Container;
import de.mwvb.oceanground.model.ContainerDAO;
import de.mwvb.oceanground.model.UpdateDockerContainer;

public class UpdateContainer extends ActionBase {

	@Override
	public String run() {
		try {
			String container = run2();
			return makeHtmlMsg("Der Container <a href='" + ContainerDetails.makeLink(container) + "' class='alert-link'>" + container
					+ "</a> wurde aktualisiert!", true);
		} catch (Exception e) {
			return makeHtmlMsg("<b>Aktualisierung war nicht erfolgreich!</b>"
					+ (e.getMessage() == null ? "" : "<br/>Details: " + e.getMessage()), false);
		}
	}

	private String run2() {
		String container = req.params("container");
		SaveContainer.validateContainer(container);
		ContainerDAO dao = new ContainerDAO();
		Container c = dao.findByContainer(container);
		if (c == null) {
			throw new RuntimeException("Container does not exist!");
		}
		new UpdateDockerContainer().update(c);
		return container;
	}
	
	private String makeHtmlMsg(String msg, boolean success) {
		String zeit = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(LocalDateTime.now());
		msg += " (" + zeit + ")";
		String ret = "<div class='alert alert-" + (success ? "success" : "danger") + " alert-dismissable'>"
				+ "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>" + (char) 215 + "</button>"
				+ msg
				+ "</div>";
		return ret.replace("'", "\"");
	}
}
