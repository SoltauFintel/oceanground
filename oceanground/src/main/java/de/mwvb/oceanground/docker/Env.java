package de.mwvb.oceanground.docker;

public class Env {
	private final String name;
	private final String value;

	public Env(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}
