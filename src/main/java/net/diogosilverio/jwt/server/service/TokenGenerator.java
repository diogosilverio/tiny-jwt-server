package net.diogosilverio.jwt.server.service;

import net.diogosilverio.jwt.server.exception.InvalidAudienceException;
import net.diogosilverio.jwt.server.model.AuthorizedUser;
import net.diogosilverio.jwt.server.model.Token;

public interface TokenGenerator {

	void check(AuthorizedUser authorizedUser) throws InvalidAudienceException;
	Token generate(AuthorizedUser authorizedUser);
	void validate(String token);
	
}
