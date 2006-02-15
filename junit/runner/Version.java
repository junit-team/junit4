package junit.runner;

/**
 * This class defines the current version of JUnit
 */
public class Version {
	private Version() {
		// don't instantiate
	}

	public static String id() {
		// TODO: auto-replace this
		return "4.0";
	}
	
	public static void main(String[] args) {
		System.out.println(id());
	}
}
