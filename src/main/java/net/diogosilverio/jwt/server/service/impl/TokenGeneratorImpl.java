package net.diogosilverio.jwt.server.service.impl;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.diogosilverio.jwt.server.exception.InvalidAudienceException;
import net.diogosilverio.jwt.server.model.AuthorizedUser;
import net.diogosilverio.jwt.server.model.Token;
import net.diogosilverio.jwt.server.service.TokenGenerator;

@Service
public class TokenGeneratorImpl implements TokenGenerator {

	@Autowired
	@Qualifier("privateKey")
	private Key privateKey;
	
	@Autowired
	@Qualifier("expirationDate")
	private Date expirationDate;
	
	@Value("${net.diogosilverio.jwt.issuer}")
	private String issuer;
	
	@Value("${net.diogosilverio.jwt.audience}")
	private String audience;

	/**
	 * Checks user's intended audience against server's audience.
	 * 
	 * @param authorizedUser {@link AuthorizedUser} to check.
	 */
	@Override
	public void check(AuthorizedUser authorizedUser) throws InvalidAudienceException {
		if(!audience.equals(authorizedUser.getAudience())){
			throw new InvalidAudienceException(authorizedUser.getAudience(), audience);
		}
	}
	
	/**
	 * Generates a new token, based on a user previously authenticated.
	 * 
	 * @param authorizedUser {@link AuthorizedUser} to generate a token.
	 */
	@Override
	public Token generate(AuthorizedUser authorizedUser){
		return new Token(this.issuer, tokenString(authorizedUser)).ok();
	}
	
	/**
	 * Validates a token.
	 * 
	 * @param token JWT token.
	 */
	@Override
	public void validate(String token) {
		
		Jwts
		.parser()
			.setSigningKey(privateKey)
			.requireAudience(audience)
			.requireIssuer(issuer)
			.parseClaimsJws(token)
			.getBody();
	
	}
	
	private String tokenString(AuthorizedUser authorizedUser){
		return Jwts
				.builder()
					.setClaims(authorizedUser.getClaims())
					.setSubject(authorizedUser.getUserName())
					.setAudience(authorizedUser.getAudience())
					.setIssuer(issuer)
					.setId(authorizedUser.getUserId().toString())
					.setExpiration(expirationDate)
					.setIssuedAt(new Date())
					.signWith(SignatureAlgorithm.RS256, privateKey)
				.compact();
	}

}
