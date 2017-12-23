package de.mwvb.oceanground.docker;

import java.io.IOException;

import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.async.ResultCallbackTemplate;

public class StatsCallback extends ResultCallbackTemplate<StatsCallback, Statistics> {
	public String usage = "?";
	
	@Override
	public void onNext(Statistics s) {
		int mem = ((Integer) s.getMemoryStats().get("usage")) / 1024 / 1024;
		try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		usage = mem + " MB";
	}
}
