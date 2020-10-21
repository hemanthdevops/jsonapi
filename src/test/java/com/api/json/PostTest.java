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
import static io.restassured.parsing.Parser.JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PostTest
{
    private final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private final String POSTS = "posts";

    private static final Logger LOGGER = LogManager.getLogger(PostTest.class.getName());
    /* POST body as string
       verify status code 201
       verify schema
       verify the record created
     */

    @Test
    public void jsonplaceholderPostStatusCodeVerify()
    {
        String S="{\"userId\":1,\"title\":\"foo\",\"body\":\"bar\"}";


        Response response = given()
                .header("Content-type","application/json")
                .header("charset","utf-8")
                .body(S)
                .when()
                .post(BASE_URL + "/" + POSTS)
                .then()
                .statusCode(HttpStatus.SC_CREATED)//Verify status code 201
                .extract()
                .response();
        LOGGER.info("Status Code 201 validated !!!");
    }


    @Test
    public void jsonplaceholderPostNewRecord()
    {
        String S="{\"userId\":1,\"title\":\"foo\",\"body\":\"bar\"}";


        Response response = given()
                .header("Content-type","application/json")
                .header("charset","utf-8")
                .body(S)
                .when()
                .post(BASE_URL + "/" + POSTS)
                .then()
                .statusCode(HttpStatus.SC_CREATED)//Verify status code 201
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertEquals("1", json.getString("userId"));
        assertEquals("foo", json.get("title"));
        assertEquals("bar", json.get("body"));
        response.then().log().all();  //verify updated records
        LOGGER.info("Records Present !!!");
    }

    @Test
    public void jsonplaceholderPostNewRecordSchema()
    {

        JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
                .setValidationConfiguration(
                        ValidationConfiguration.newBuilder()
                                .setDefaultVersion(SchemaVersion.DRAFTV4).freeze())
                .freeze();
        JsonSchemaValidator.settings = settings()
                .with().jsonSchemaFactory(factory)
                .and().with().checkedValidation(false);

        String S="{\"userId\":1,\"title\":\"foo\",\"body\":\"bar\"}";


        Response response = given()
                .header("Content-type","application/json")
                .header("charset","utf-8")
                .body(S)
                .when()
                .post(BASE_URL + "/" + POSTS)
                .then()
                .statusCode(HttpStatus.SC_CREATED)//Verify status code 201
                .extract()
                .response();

        response.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("event_0.json")  //verify the output with schema DraftV4
                        .using(factory));
        LOGGER.info("Schema validated with JSON Schema DraftV4!!!");

    }

}

