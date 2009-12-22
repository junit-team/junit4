package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

public class Plan {
	private final Description fDescription;

	public Plan(Description description) {
		fDescription= description;
	}

	public Description getDescription() {
		return fDescription;
	}

	public List<Plan> getChildren() {
		ArrayList<Plan> results= new ArrayList<Plan>();
		ArrayList<Description> children= fDescription.getChildren();
		for (Description each : children)
			results.add(Plan.fromDescription(each));
		return results;
	}

	public static Plan fromDescription(Description description) {
		return new Plan(description);
	}

}
