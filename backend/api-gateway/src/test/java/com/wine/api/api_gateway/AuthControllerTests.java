package com.wine.api.api_gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.api.api_gateway.model.auth.LoginRequest;
import com.wine.api.api_gateway.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import org.mockserver.model.HttpRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerTests {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MockMvc mockMvc;

	private ClientAndServer mockServer;

	private final String GOOGLE_ID_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjczZTI1Zjk3ODkxMTljNzg3NWQ1ODA4N2E3OGFjMjNmNWVmMmVkYTMiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI0MDc0MDg3MTgxOTIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI0MDc0MDg3MTgxOTIuYXBwcy5nb29nbGV2c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTQwMjE3NDc1NzY2Njc0NzgyOTIiLCJlbWFpbCI6InBhY2VhZHJpYW5vbGF2b3JvQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9oYXNoIjoiM1BtV1hYUzd6LWRaLS1UU1B0ZVBoUSIsIm5hbWUiOiJBZHJpYW5vIFBhY2UiLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EvQUNnOG9jS1g0cnQ1akk4dTlsYV9aNmJ6bEpmM0swQm5XQzlRd192WDQ5aUV2UXNSZ3JiakxBPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6IkFkcmlhbm8iLCJmYW1pbHlfbmFtZSI6IlBhY2UiLCJpYXQiOjE3Mjk1MTY1NzMsImV4cCI6MTcyOTUyMDE3M30.Vtw0SlrTsrZZ4nAm5q-tlRUPjQvjXs5J1mbMR0pLO2dS7kB7MlgkTv5CtyN3hcfBOm428h1PwcSm94o3CtErCREvSbX4e_liFMtRuEmAdxtQIS8s4C--eHHr7j4PlyLiH0XURwXVQcMtEuZ11yY9aYJMfx9wwsTKaL0PcKtaPORQ1e124b70072fkssVhbe1HuuxN3OoC-zXTv4Cs0NqG4ofm785d_jPRDLBXxjaiskP3I9Z54CFed62IplF7Vztk9q6vw36osiqxHfZttu1CbZyfZkdug7dLm098jOnAPuRWcPj5uqtAXaSzfc9CXeSrvPwcRK5ax3eCqkKRUYJeQ";
	private String jsonBody;
	private User user;

	@BeforeEach
	public void before() throws JsonProcessingException {
		mockServer = ClientAndServer.startClientAndServer(8080);

		user = new User();
		user.setGoogleId("114021747576667478292");
		user.setName("Adriano Pace");
		user.setEmail("paceadrianolavoro@gmail.com");
		ObjectMapper objectMapper = new ObjectMapper();
		jsonBody = objectMapper.writeValueAsString(user);
	}

	private void setupMockResponse(String requestPath, int responseStatusCode, String responseBody) {
		mockServer.when(
				HttpRequest.request()
						.withMethod("POST")
						.withPath(requestPath)
						.withBody(jsonBody)
		).respond(
				HttpResponse.response()
						.withStatusCode(responseStatusCode)
						.withBody(responseBody)
		);
	}


	@Test
	void authLoginResponseOK() throws Exception {
		String googleTokenInfoResponse = "{\n" +
				" \"iss\": \"https://accounts.google.com\",\n" +
				" \"azp\": \"407408718192.apps.googleusercontent.com\",\n" +
				" \"aud\": \"407408718192.apps.googleusercontent.com\",\n" +
				" \"sub\": \"114021747576667478292\",\n" +
				" \"email\": \"paceadrianolavoro@gmail.com\",\n" +
				" \"email_verified\": \"true\",\n" +
				" \"name\": \"Adriano Pace\",\n" +
				" \"picture\": \"https://lh3.googleusercontent.com/a-/somepicurl\",\n" +
				" \"given_name\": \"Adriano\",\n" +
				" \"family_name\": \"Pace\",\n" +
				" \"iat\": \"1729516573\",\n" +
				" \"exp\": \"1729520173\",\n" +
				" \"jti\": \"abcd1234efgh5678\"\n" +
				"}";

		setupMockResponse("/api/v1/users", 200, jsonBody);

		mockServer.when(
				HttpRequest.request()
						.withMethod("GET")
						.withPath("/tokeninfo")
						.withQueryStringParameter("id_token", GOOGLE_ID_TOKEN)
		).respond(
				HttpResponse.response()
						.withStatusCode(200)
						.withBody(googleTokenInfoResponse)
		);

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setGoogleIdToken(GOOGLE_ID_TOKEN);
		mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"googleIdToken\": \"" + loginRequest.getGoogleIdToken() + "\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}

/*
	@Test
	void authLoginResponseBadRequest() throws Exception {

		String errorResponse = "{\n" +
				"  \"error\": \"invalid_token\",\n" +
				"  \"error_description\": \"Invalid Value\"\n" +
				"}";

		setupMockResponse("/api/v1/users", 200, jsonBody);

		mockServer.when(
				HttpRequest.request()
						.withMethod("GET")
						.withPath("/tokeninfo")
						.withQueryStringParameter("id_token", GOOGLE_ID_TOKEN)
		).respond(
				HttpResponse.response()
						.withStatusCode(400)
						.withBody(errorResponse)
		);

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setGoogleIdToken(GOOGLE_ID_TOKEN);
		mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"googleIdToken\": \"" + loginRequest.getGoogleIdToken() + "\"}"))
				.andExpect(status().isBadRequest());
	}


	@Test
	void databaseServiceResponseOK() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

		setupMockResponse("/api/v1/users", 200, jsonBody);

		String response = restTemplate.postForObject("http://localhost:8080/api/v1/users", requestEntity, String.class);

		assertThat(response).satisfies(r -> {
			assertThat(r).contains("googleId");
			assertThat(r).contains("name");
			assertThat(r).contains("email");
		});
	}
*/

	@AfterEach
	void stopServer() {
		mockServer.stop();
	}
}
