package net.diogosilverio.jwt.server.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.google.gson.Gson;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.diogosilverio.jwt.server.exception.InvalidAudienceException;
import net.diogosilverio.jwt.server.model.AuthorizedUser;
import net.diogosilverio.jwt.server.model.CheckToken;
import net.diogosilverio.jwt.server.model.Status;
import net.diogosilverio.jwt.server.model.Token;
import net.diogosilverio.jwt.server.service.TokenGenerator;

@ActiveProfiles("tes")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
@AutoConfigureMockMvc
@FixMethodOrder()
public class TokenControllerTests {

	@Autowired
	private MockMvc mock;
	
	@Autowired
	@Qualifier("privateKey")
	private Key privateKey;
		
	@Mock
	private TokenGenerator tokenGenerator;
	
	@Value("${net.diogosilverio.jwt.audience}")
	private String audience;
	
	@Value("${net.diogosilverio.jwt.issuer}")
	private String issuer;
	
	private Gson gson = new Gson();
	
	@Test
	public void testValidRequest() throws Exception{
		
		mock.perform(
				post("/token/new")
					.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
					.content(userJson())
				)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
		
	}
	
	@Test
	public void testInvalidAudienceRequest() throws Exception{
		
		doThrow(new InvalidAudienceException("spring-etc", audience)).when(tokenGenerator).check(user());
		
		mock.perform(
				post("/token/new")
					.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
					.content(userInvalidAudJson())
				)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Invalid audience")))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
		
	}
	
	@Test
	public void testEmptyRequest() throws Exception {
		
		mock.perform(
				post("/token/new")
					.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
					.content(emptyUserJson())
				)
			.andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void testValidTokenCheck() throws Exception {
		
		final Token token = retrieveToken();
		
		assertEquals(Status.AUTHENTICATED, token.getStatus());
		assertNotNull("Token should not be null", token.getToken());
		
		MvcResult checkResult = mock.perform(
								post("/token/check")
									.content(fakeToken(audience, issuer).getToken())
								)
							.andExpect(status().isOk())
							.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
							.andReturn();
		
		final CheckToken checkToken = buildCheckToken(checkResult.getResponse().getContentAsString());
		
		assertEquals(Status.AUTHENTICATED, checkToken.getStatus());
		assertEquals("Ok", checkToken.getMessage());
		
	}
	
	@Test
	public void testExpiredTokenCheck() throws Exception {
		
		final Token token = retrieveToken();
		
		assertEquals(Status.AUTHENTICATED, token.getStatus());
		assertNotNull("Token should not be null", token.getToken());
		
		Thread.sleep(5000);
		
		MvcResult checkResult = mock.perform(
								post("/token/check")
									.content(token.getToken())
								)
							.andExpect(status().isBadRequest())
							.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
							.andReturn();
		
		final CheckToken checkToken = buildCheckToken(checkResult.getResponse().getContentAsString());
		
		assertEquals(Status.EXPIRED, checkToken.getStatus());
		
	}

	@Test
	public void testScrewedUpToken() throws Exception{
		final String screwedUpToken = retrieveToken().getToken().replace("a", "b");
		
		MvcResult checkResult = mock.perform(
				post("/token/check")
					.content(screwedUpToken)
				)
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
			.andReturn();

		final CheckToken checkToken = buildCheckToken(checkResult.getResponse().getContentAsString());
		
		assertEquals(Status.CORRUPTED, checkToken.getStatus());
		assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", checkToken.getMessage());
		
	}
	
	@Test
	public void testInvalidAudienceCheck() throws Exception{
		
		MvcResult checkResult = mock.perform(
				post("/token/check")
					.content(fakeToken("spring-tests", "diogosilverio.net").getToken())
				)
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
			.andReturn();
		
		final CheckToken checkToken = buildCheckToken(checkResult.getResponse().getContentAsString());
		
		assertEquals(Status.CORRUPTED, checkToken.getStatus());
		assertEquals("Expected aud claim to be: spring-test, but was: spring-tests.", checkToken.getMessage());
		
	}
	
	@Test
	public void testEmptyTokenSentToCheck() throws Exception{
		
		MvcResult checkResult = mock.perform(
				post("/token/check")
					.content("")
				)
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
			.andReturn();
		
		final CheckToken checkToken = buildCheckToken(checkResult.getResponse().getContentAsString());
		
		assertEquals(Status.CORRUPTED, checkToken.getStatus());
		assertEquals("JWT String argument cannot be null or empty.", checkToken.getMessage());
		
	}
	
	private Token fakeToken(String audience, String issuer){
		
		final AuthorizedUser authorizedUser = new AuthorizedUser(1L, "user", audience, new HashMap<>());
		
		String tokenStr = Jwts
						.builder()
							.setClaims(authorizedUser.getClaims())
							.setSubject(authorizedUser.getUserName())
							.setAudience(authorizedUser.getAudience())
							.setIssuer(issuer)
							.setId(authorizedUser.getUserId().toString())
							.setExpiration(Date.from(LocalDateTime.now().plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant()))
							.setIssuedAt(new Date())
							.signWith(SignatureAlgorithm.RS256, privateKey)
						.compact();
		
		return new Token(issuer, tokenStr);
	}
	
	/**
	 * @return
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 */
	private Token retrieveToken() throws Exception, UnsupportedEncodingException {
		MvcResult tokenResult = mock.perform(
				post("/token/new")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(userJson())
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andReturn();
		
		return buildToken(tokenResult.getResponse().getContentAsString());
	}
	
	private AuthorizedUser emptyUser(){
		return new AuthorizedUser(null, "", "", new HashMap<>());
	}
	
	private String emptyUserJson(){
		return new Gson().toJson(emptyUser());
	}
	
	private AuthorizedUser user(){
		return new AuthorizedUser(1L, "user", "spring-test", new HashMap<>());
	}
	
	private String userJson(){
		return new Gson().toJson(user());
	}
	
	private AuthorizedUser userInvalidAud(){
		return new AuthorizedUser(1L, "user", "spring-etc", null);
	}
	
	private String userInvalidAudJson(){
		return gson.toJson(userInvalidAud());
	}
	
	private Token buildToken(String tokenJson){
		return gson.fromJson(tokenJson, Token.class);
	}
	
	private CheckToken buildCheckToken(String checkJson){
		return gson.fromJson(checkJson, CheckToken.class);
	}
	
}
