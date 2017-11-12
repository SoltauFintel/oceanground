package de.mwvb.oceanground.actions;

import java.util.ArrayList;
import java.util.List;

import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Image;

import de.mwvb.maja.web.Action;
import de.mwvb.oceanground.OceanGroundApp;
import de.mwvb.oceanground.docker.OGImage;

public class ImageDetails extends Action {

	@Override
	protected void execute() {
		String imageId = req.params("imageid");
		try {
			InspectImageResponse ii = OceanGroundApp.docker.inspectImage(imageId);
			OGImage i = new OGImage(ii);
			put("title", i.getShortName() + " - " + OceanGroundApp.TITLE);
			put("i", i);
			if (i.getCommand() != null) {
				similar(ii.getId(), i.getCommand());
			}
		} catch (NotFoundException e) {
			throw new RuntimeException("Image \"" + imageId + "\" nicht vorhanden!");
		}
	}

	private void similar(String refId, String refCommand) {
		List<Image> images = OceanGroundApp.docker.images();
		List<OGImage> ogImages = new ArrayList<>();
		List<OGImage> ogImagesWithNames = new ArrayList<>();
		for (Image image : images) {
			if (refId.equals(image.getId())) continue;
			InspectImageResponse ii = OceanGroundApp.docker.inspectImage(image.getId());
			OGImage i = new OGImage(ii);
			if (refCommand.equals(i.getCommand())) {
				if (i.getName().isEmpty()) {
					ogImages.add(i);
				} else {
					ogImagesWithNames.add(i);
				}
			}
		}
		put("similar", ogImages.isEmpty() ? null : ogImages);
		put("nSimilar", ogImages.size());
		put("deleteSimilarBtnText", ogImages.size() == 1 ? "Dieses Image l&ouml;schen..." : "Diese " + ogImages.size() + " Images l&ouml;schen...");
		put("similarWithNames", ogImagesWithNames.isEmpty() ? null : ogImagesWithNames);
		put("nSimilarWithNames", ogImagesWithNames.size());
	}
}
