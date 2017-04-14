package net.diogosilverio.jwt.server.configuration;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class TokenConfiguration {

	@Value("${net.diogosilverio.jwt.expiration}")
	private Long expiration;
	
	@Profile({"dev", "prd"})
	@Bean("expirationDate")
	public Date expirationDate() {
		return generateExpirationDate(Boolean.TRUE, expiration);
	}
	
	@Profile({"tes"})
	@Bean("expirationDate")
	public Date expirationDateTestEnv() {
		return generateExpirationDate(Boolean.FALSE, expiration);
	}
	
	private Date generateExpirationDate(Boolean minutes, Long time){
		return Date.from(LocalDateTime.now().plus(time, (minutes ? ChronoUnit.MINUTES : ChronoUnit.SECONDS)).atZone(ZoneId.systemDefault()).toInstant());
	}
	
}
