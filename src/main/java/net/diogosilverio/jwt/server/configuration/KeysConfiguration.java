package net.diogosilverio.jwt.server.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeysConfiguration {
	
	@Value("${net.diogosilverio.jwt.key}")
	private String keyPath;
	
	@Bean("privateKey")
	public Key generatePrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
		
		final byte[] privateKeyBytes = Files.readAllBytes(Paths.get(this.keyPath));
		
		final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
		final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		final PrivateKey privateKey = keyFactory.generatePrivate(spec);
		
		return privateKey;
	}

}
