package net.diogosilverio.jwt.server.model;

public class Token {
	
	private Status status;
	private String message;
	private String issuer;
	private String token;
	
	public Token(String issuer, String token) {
		super();
		this.issuer = issuer;
		this.token = token;
	}
	
	public String getIssuer() {
		return issuer;
	}
	
	public String getToken() {
		return token;
	}

	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
	
	public Token ok(){
		status = Status.AUTHENTICATED;
		message = "Ok";
		
		return this;
	}
	
	public Token notOk(Status s, String m){
		status = s;
		message = m;
		
		return this;
	}

	@Override
	public String toString() {
		return "Token [issuer=" + issuer + ", token=" + token + "]";
	}
	
}