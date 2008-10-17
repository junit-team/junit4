package org.junit.tests.experimental.max;

import org.junit.runner.Description;

public class Odds {
	private final double fCertainty;
	private final Description fDescription;

	public Odds(Description description, double certainty) {
		fDescription= description;
		fCertainty= certainty;
	}

	public double getCertainty() {
		return fCertainty;
	}

	public Object getDescription() {
		return fDescription;
	}
}