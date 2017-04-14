package net.diogosilverio.jwt.server.exception;

public class InvalidAudienceException extends Exception {

	private static final long serialVersionUID = -8949292315688316349L;

	public InvalidAudienceException(String audience, String audienceExpected) {
		super(String.format("Invalid audience '%s', expected '%s'.", audience, audienceExpected));
	}

}
