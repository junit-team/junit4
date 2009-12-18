package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

public abstract class Plan {
	public abstract Description getDescription();
	public abstract List<Plan> getChildren();

	public static Plan fromDescription(final Description description) {
		return new Plan() {
			@Override
			public Description getDescription() {
				return description;
			}
			
			@Override
			public List<Plan> getChildren() {
				List<Description> childrenDescs= description.getChildrenInternal();
				ArrayList<Plan> children= new ArrayList<Plan>();
				for (Description each : childrenDescs)
					children.add(Plan.fromDescription(each));
				return children;
			}
		};
	}
	
	public boolean isSuite() {
		return getChildren().size() > 0;
	}
	
	public boolean isTest() {
		return !isSuite();
	}
}
