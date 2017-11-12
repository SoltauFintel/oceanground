package de.mwvb.oceanground.actions;

import java.util.ArrayList;
import java.util.List;

import org.pmw.tinylog.Logger;

import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.Image;

import de.mwvb.maja.web.Action;
import de.mwvb.oceanground.OceanGroundApp;
import de.mwvb.oceanground.docker.OGImage;

public class Images extends Action {
	
	@Override
	protected void execute() {
		List<Image> cl = new ArrayList<>();
		try {
			cl = OceanGroundApp.docker.images();
		} catch (Exception e) { // Fehler schlucken und dann leere Seite anzeigen
			Logger.error(e);
		}
		List<OGImage> ret = new ArrayList<>();
		cl.forEach(i -> {
			InspectImageResponse inspectImage = null;
			try {
				inspectImage = OceanGroundApp.docker.inspectImage(i.getId());
			} catch (Exception e) {
			}
			OGImage image = new OGImage(inspectImage);
			boolean insert = !image.getName().isEmpty() && !image.getName().equals("&lt;none&gt;:&lt;none&gt;");
			if (!insert) {
				insert = true;
				for (int j = 0; j < ret.size(); j++) {
					OGImage ogImage = ret.get(j);
					if (ogImage.getCommand().equals(image.getCommand())) {
						insert = false; // Ist schon enthalten
						break;
					}
				}
			}
			if (insert && image.getShortName().endsWith("base")) {
				insert = false;
			}
			if (insert) {
				ret.add(image);
			}
		});
		ret.sort((a,b) -> a.getSort().compareTo((b.getSort())));
		put("list", ret);
		put("size", ret.size());
		put("clsize", cl.size());
		put("title", "Images - " + OceanGroundApp.TITLE);
	}
}
