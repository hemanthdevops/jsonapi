package com.api.json;


import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PUTTest {
    private final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private final String POSTS = "posts";
    private static final Logger LOGGER = LogManager.getLogger(PUTTest.class.getName());

    /* Send body as JSON
       verify status code 200
       verify the record created
       verify schema
     */

    @Test
    public void jsonplaceholderPutStatusCodeVerify() {

        JSONObject post = new JSONObject();
        post.put("id", "1");
        post.put("userId", "1");
        post.put("title", "abc");
        post.put("body", "xyz");


        Response response = given()
                .header("Content-type", "application/json")
                .header("charset", "utf-8")
                .body(post.toString())
                .when()
                .put(BASE_URL + "/" + POSTS + "/1")
                .then()
                .statusCode(HttpStatus.SC_OK)  // Validate status code 200
                .extract()
                .response();
        LOGGER.info("Status Code 200 validated !!!");
    }

    @Test
    public void jsonplaceholderPutNewRecord() {

        JSONObject post = new JSONObject();
        post.put("id", "1");
        post.put("userId", "1");
        post.put("title", "abc");
        post.put("body", "xyz");


        Response response = given()
                .header("Content-type", "application/json")
                .header("charset", "utf-8")
                .body(post.toString())
                .when()
                .put(BASE_URL + "/" + POSTS + "/1")
                .then()
                .statusCode(HttpStatus.SC_OK)  // Validate status code 200
                .extract()
                .response();
        LOGGER.info("Status Code 200 validated !!!");

        JsonPath json = response.jsonPath();
        assertEquals("1", json.getString("id"));
        assertEquals("1", json.getString("userId"));
        assertEquals("abc", json.get("title"));
        assertEquals("xyz", json.get("body"));
        response.then().log().all();  //print updated records
        LOGGER.info("Updated Record Present !!!");
    }


    @Test
    public void jsonplaceholderPutNewRecordSchema() {
        JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
                .setValidationConfiguration(
                        ValidationConfiguration.newBuilder()
                                .setDefaultVersion(SchemaVersion.DRAFTV4).freeze())
                .freeze();
        JsonSchemaValidator.settings = settings()
                .with().jsonSchemaFactory(factory)
                .and().with().checkedValidation(false);

        JSONObject post = new JSONObject();
        post.put("id", "1");
        post.put("userId", "1");
        post.put("title", "abc");
        post.put("body", "xyz");


        Response response = given()
                .header("Content-type", "application/json")
                .header("charset", "utf-8")
                .body(post.toString())
                .when()
                .put(BASE_URL + "/" + POSTS + "/1")
                .then()
                .statusCode(HttpStatus.SC_OK)  // Validate status code 200
                .extract()
                .response();

        response.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("event_1.json")  //verify the output with schema DraftV4
                        .using(factory));
        LOGGER.info("Schema validated with JSON Schema DraftV4!!!");

    }
}