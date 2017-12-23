package de.mwvb.oceanground.model;

public class MaxMemory {
	private final String input;
	private final Long bytes;

	/**
	 * @param pInput something like "128m", "7000k", "1024000" - or null or empty
	 */
	public MaxMemory(String pInput) {
		if (pInput == null) {
			bytes = null;
			this.input = "";
		} else {
			this.input = pInput.trim();
			if (this.input.matches("[0-9]+m")) {
				bytes = Long.parseLong(this.input.substring(0, this.input.length() - 1)) * 1024 * 1024;
			} else if (this.input.matches("[0-9]+k")) {
				bytes = Long.parseLong(this.input.substring(0, this.input.length() - 1)) * 1024;
			} else if (this.input.matches("[0-9]+")) {
				bytes = Long.parseLong(this.input);
			} else {
				throw new RuntimeException("Input '" + this.input + "' not valid!");
			}
		}
	}

	public Long getBytes() {
		return bytes;
	}

	public String getInput() {
		return input;
	}
}
