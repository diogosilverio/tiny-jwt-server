package net.diogosilverio.jwt.server.api;

import org.springframework.http.ResponseEntity;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.diogosilverio.jwt.server.model.AuthorizedUser;
import net.diogosilverio.jwt.server.model.CheckToken;
import net.diogosilverio.jwt.server.model.Token;

public interface TokenAPI {

	@ApiOperation(
			value="Issues a new token for a properly authorized user.", 
			notes="Generates a new token for a user already authenticated."
		  		+ "The source system must inform the expected audience according to the specifications set by this server.")
	@ApiResponses(value={
		@ApiResponse(code=200, message="Token generated with success", response=Token.class),
		@ApiResponse(code=400, message="Request is invalid", response=Token.class),
		@ApiResponse(code=409, message="Invalid claims/payload provided", response=Token.class),
		@ApiResponse(code=500, message="Something went wrong with the server", response=Token.class)
	}	
	)
	ResponseEntity<Token> issueToken(AuthorizedUser authorizedUser);

	@ApiOperation(
			value="Checks if a given token is valid", 
			notes="Checks token, signature, audience and issuer against server configs.")
	@ApiResponses(value={
			@ApiResponse(code=200, message="Token is valid", response=CheckToken.class),
			@ApiResponse(code=400, message="Token is invalid: Bad signature, invalid claims or expired", response=CheckToken.class),
			@ApiResponse(code=500, message="Something went wrong with the server", response=CheckToken.class),
			@ApiResponse(code=503, message="Something went wrong with the server", response=CheckToken.class)

	}	
	)
	ResponseEntity<CheckToken> checkToken(String token);

}