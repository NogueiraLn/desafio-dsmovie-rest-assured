package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class ScoreControllerRA {

	private String adminUser, adminPassword;
	private String adminToken;

	private Long existingMovieId, nonExistingMovieId;

	private Map<String, Object> putScoreInstance;

	@BeforeEach
	public void setUp() throws Exception {
		baseURI = "http://localhost:8080";

		adminUser = "alex@gmail.com";
		adminPassword = "123456";

		adminToken = TokenUtil.obtainAccessToken(adminUser, adminPassword);

		existingMovieId = 1L;
		nonExistingMovieId = 1000L;

		putScoreInstance = new HashMap<>();
		putScoreInstance.put("movieId", existingMovieId);
		putScoreInstance.put("score", 4.0);
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		putScoreInstance.put("movieId", nonExistingMovieId);
		JSONObject newScore = new JSONObject(putScoreInstance);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(newScore)
				.when()
				.put("/scores")
				.then()
				.statusCode(404)
				.body("error", equalTo("Recurso não encontrado"))
				.body("status", equalTo(404));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		putScoreInstance.remove("movieId");
		JSONObject newScore = new JSONObject(putScoreInstance);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(newScore)
				.when()
				.put("/scores")
				.then()
				.statusCode(422)
				.body("errors.message[0]", equalTo("Campo requerido"))
				.body("status", equalTo(422));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		putScoreInstance.put("score", -1.0);
		JSONObject newScore = new JSONObject(putScoreInstance);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(newScore)
				.when()
				.put("/scores")
				.then()
				.statusCode(422)
				.body("errors.message[0]", equalTo("Valor mínimo 0"))
				.body("status", equalTo(422));
	}
}
