package net.diogosilverio.jwt.server.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.SignatureException;
import net.diogosilverio.jwt.server.api.TokenAPI;
import net.diogosilverio.jwt.server.exception.InvalidAudienceException;
import net.diogosilverio.jwt.server.model.AuthorizedUser;
import net.diogosilverio.jwt.server.model.CheckToken;
import net.diogosilverio.jwt.server.model.Status;
import net.diogosilverio.jwt.server.model.Token;
import net.diogosilverio.jwt.server.service.TokenGenerator;

@RestController
@RequestMapping("/token")
public class TokenController implements TokenAPI {

	@Autowired
	private TokenGenerator tokenGenerator;
	
	@Override
	@PostMapping(path="/new", produces=MediaType.APPLICATION_JSON_UTF8_VALUE, consumes=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Token> issueToken(@RequestBody @Valid AuthorizedUser authorizedUser){
		
		ResponseEntity<Token> response = null;
		
		try {
			tokenGenerator.check(authorizedUser);
			
			final Token generatedToken = tokenGenerator.generate(authorizedUser);
			response = ResponseEntity.ok(generatedToken);
		} catch (InvalidAudienceException e) {
			response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Token("", "").notOk(Status.CORRUPTED, e.getMessage()));
		} catch (IllegalStateException e) {
			response = ResponseEntity.status(HttpStatus.CONFLICT).body(new Token("", "").notOk(Status.CORRUPTED, e.getMessage()));
		} catch (Exception e) {
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Token("", "").notOk(Status.SERVER_ERROR, e.getMessage()));
		} 
		
		return response;
	}

	@Override
	@PostMapping(path="/check", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<CheckToken> checkToken(@RequestBody(required=false) String token){
		
		ResponseEntity<CheckToken> response = null;
		
		try {
			tokenGenerator.validate(token);
			response = ResponseEntity.ok(new CheckToken(Status.AUTHENTICATED, "Ok"));
		} catch(SignatureException | IncorrectClaimException e) {
			response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CheckToken(Status.CORRUPTED, e.getMessage()));
		} catch(ExpiredJwtException e){
			response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CheckToken(Status.EXPIRED, e.getMessage()));
		} catch(IllegalArgumentException e) {
			if(e.getMessage().contains("JWT String")){
				response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CheckToken(Status.CORRUPTED, e.getMessage()));
			} else {
				response = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new CheckToken(Status.SERVER_ERROR, e.getMessage()));
			}
		} catch (Exception e) {
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CheckToken(Status.SERVER_ERROR, e.getMessage()));
		} 
		
		return response;
	}
	
}