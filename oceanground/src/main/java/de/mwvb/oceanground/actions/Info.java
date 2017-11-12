package de.mwvb.oceanground.actions;

import java.time.format.DateTimeFormatter;

import de.mwvb.maja.web.ActionBase;
import de.mwvb.oceanground.OceanGroundApp;

public class Info extends ActionBase {

	@Override
	public String run() {
		// OceanGround v0.3.0, boot time: 02.04.2017 17:28:27, Docker: 1.13.1; 1.26; linux; 4.4.27-x86_64-jb1
		String dockerVersion = OceanGroundApp.docker.version();
		return "OceanGround v" + OceanGroundApp.VERSION
				+ ", Boot time: " + DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(OceanGroundApp.getBoottime())
				+ ", Docker: " + (dockerVersion.isEmpty() ? "fault" : "\"" + dockerVersion + "\"")
				+ ", Image cache size: " + OceanGroundApp.docker.getImageCacheSize();
	}
}
