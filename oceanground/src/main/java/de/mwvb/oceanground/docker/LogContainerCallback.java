package de.mwvb.oceanground.docker;

import java.util.ArrayList;
import java.util.List;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;

class LogContainerCallback extends LogContainerResultCallback {
	protected final StringBuffer log = new StringBuffer();
	private List<Frame> collectedFrames = new ArrayList<Frame>();
	private boolean collectFrames = false;

	LogContainerCallback() {
		this(false);
	}

	LogContainerCallback(boolean collectFrames) {
		this.collectFrames = collectFrames;
	}

	@Override
	public void onNext(Frame frame) {
		if (collectFrames) {
			collectedFrames.add(frame);
		}
		log.append(new String(frame.getPayload()));
	}

	@Override
	public String toString() {
		return log.toString();
	}

	public List<Frame> getCollectedFrames() {
		return collectedFrames;
	}
}
