package net.diogosilverio.jwt.server.model;

public enum Status {
	AUTHENTICATED,
	EXPIRED,
	CORRUPTED,
	SERVER_ERROR;
}