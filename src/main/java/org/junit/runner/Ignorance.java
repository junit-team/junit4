package org.junit.runner;

// TODO: (Dec 12, 2007 2:39:57 PM) does this belong here?

public class Ignorance {

	private final String fReason;

	public Ignorance(Description description, String reason) {
		fReason= reason;
		// TODO: (Dec 13, 2007 12:57:49 AM) Do I use description?  Do I in failure?

		// TODO Auto-generated constructor stub
	}

	public String getReason() {
		return fReason;
	}

}
