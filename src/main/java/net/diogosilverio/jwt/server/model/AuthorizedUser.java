package net.diogosilverio.jwt.server.model;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AuthorizedUser {
	
	@NotNull
	@Digits(integer=64, fraction=0)
	private Long userId;
	
	@NotNull
	@Size(min=1)
	private String userName;
	
	@NotNull
	@Size(min=1)
	private String audience;
	
	private Map<String, Object> claims = new HashMap<>();
	
	public AuthorizedUser() {
		super();
	}

	public AuthorizedUser(Long userId, String userName, String audience, Map<String, Object> claims) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.audience = audience;
		this.claims = claims;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Map<String, Object> getClaims() {
		return claims;
	}

	public void setClaims(Map<String, Object> claims) {
		this.claims = claims;
	}
	
	public void addClaim(String key, Object value){
		this.claims.put(key, value);
	}

	public String getAudience() {
		return audience;
	}

	public void setAudience(String audience) {
		this.audience = audience;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((audience == null) ? 0 : audience.hashCode());
		result = prime * result + ((claims == null) ? 0 : claims.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthorizedUser other = (AuthorizedUser) obj;
		if (audience == null) {
			if (other.audience != null)
				return false;
		} else if (!audience.equals(other.audience))
			return false;
		if (claims == null) {
			if (other.claims != null)
				return false;
		} else if (!claims.equals(other.claims))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AuthorizedUser [userId=" + userId + ", userName=" + userName + ", audience=" + audience + ", claims="
				+ claims + "]";
	}
	
}