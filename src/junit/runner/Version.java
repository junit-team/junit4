package junit.runner;

/**
 * This class defines the current version of JUnit
 */
public class Version {
	private Version() {
		// don't instantiate
	}

	public static String id() {
		return "4.4-snapshot-20070712-1321";
	}
	
	public static void main(String[] args) {
		System.out.println(id());
	}
}
