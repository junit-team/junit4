package org.junit.experimental.theories;

public abstract class PotentialAssignment {
	public static class CouldNotGenerateValueException extends Exception {
		private static final long serialVersionUID= 1L;
	}
	
	public static PotentialAssignment forValue(final String name, final Object value) {
		return new PotentialAssignment() {		
			@Override
			public Object getValue() throws CouldNotGenerateValueException {
				return value;
			}
			
			@Override
			public String toString() {
				return String.format("[%s]", value);
			}

			@Override
			public String getDescription()
					throws CouldNotGenerateValueException {
				return name;
			}
		};
	}
	
	public abstract Object getValue() throws CouldNotGenerateValueException;
	
	public abstract String getDescription() throws CouldNotGenerateValueException;
}
