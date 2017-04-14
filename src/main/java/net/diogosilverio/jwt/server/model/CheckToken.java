package net.diogosilverio.jwt.server.model;

public class CheckToken{

	private Status status;
	private String message;
	
	public CheckToken(Status status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "CheckToken [status=" + status + ", message=" + message + "]";
	}
	
}