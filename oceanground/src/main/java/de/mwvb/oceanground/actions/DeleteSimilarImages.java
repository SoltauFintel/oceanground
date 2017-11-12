package de.mwvb.oceanground.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.pmw.tinylog.Logger;

import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Image;

import de.mwvb.maja.web.Action;
import de.mwvb.oceanground.OceanGroundApp;
import de.mwvb.oceanground.docker.OGImage;

public class DeleteSimilarImages extends Action {
	private static ExecutorService threadPool = Executors.newFixedThreadPool(1);

	@Override
	protected void execute() {
		String imageId = req.params("imageid");
		try {
			InspectImageResponse ii = OceanGroundApp.docker.inspectImage(imageId);
			OGImage i = new OGImage(ii);
			put("title", i.getShortName() + " - " + OceanGroundApp.TITLE);
			put("id", imageId);
			String msg = "Auftrag wurde nicht angenommen.";
			if (i.getCommand() != null) {
				int n = similar(ii.getId(), i.getCommand());
				if (n == 1) {
					msg = "Auftrag zur Löschung des Images wurde angenommen.";
				} else {
					msg = "Auftrag zur Löschung der " + n + " Images wurde angenommen.";
				}
			}
			Logger.info(msg);
			put("msg", msg);
		} catch (NotFoundException e) {
			throw new RuntimeException("Image \"" + imageId + "\" nicht vorhanden!");
		}
	}

	private int similar(String refId, String refCommand) {
		List<Image> images = OceanGroundApp.docker.images();
		List<String> kill = new ArrayList<>();
		for (Image image : images) {
			String id = image.getId();
			if (refId.equals(id)) continue;
			InspectImageResponse ii = OceanGroundApp.docker.inspectImage(id);
			OGImage i = new OGImage(ii);
			if (refCommand.equals(i.getCommand())) {
				if (i.getName().isEmpty()) {
					kill.add(id);
				}
			}
		}
		kill(kill);
		return kill.size();
	}
	
	private void kill(List<String> kill) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				kill.forEach(id -> {
					try {
						OceanGroundApp.docker.deleteImage(id);
					} catch (Exception e) {
						Logger.warn("Image " + id + " can not be deleted: [" + e.getClass().getName() + "] " + e.getMessage());
					}
				});
			}
		});
	}
}
