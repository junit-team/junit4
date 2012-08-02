package junit.framework;

/** Thrown when a test is blocked */
public class BlockedException extends RuntimeException {
	private static final long serialVersionUID= 1L;

	public BlockedException() {
	}

	public BlockedException(String reason) {
		super(defaultString(reason));
	}

	private static String defaultString(String message) {
		return message == null ? "" : message;
	}
}
