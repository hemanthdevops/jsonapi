package com.api.json;


import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class GetTest {

    private static final Logger LOGGER = LogManager.getLogger(GetTest.class.getName());
    /* Verify Status code is 200
     */
    @org.junit.Test
    public void verifyStatusCodeVerify(){
        given()
                .header("Content-type","application/json")
                .header("charset","utf-8")
                .when()
                .get("https://jsonplaceholder.typicode.com/posts").
                then()
                .statusCode(200) // verify status code 200
                .log().status();
        LOGGER.info("Status Code 200!!!");
    }


    /* Verify Schema as per JSON schema Draft V4
     */
    @org.junit.Test
    public void verifyGetRecordSchema(){
        JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
                .setValidationConfiguration(
                        ValidationConfiguration.newBuilder()
                                .setDefaultVersion(SchemaVersion.DRAFTV4).freeze())
                .freeze();
        JsonSchemaValidator.settings = settings()
                .with().jsonSchemaFactory(factory)
                .and().with().checkedValidation(false);
        given()
                .header("Content-type","application/json")
                .header("charset","utf-8")
                .when()
                .get("https://jsonplaceholder.typicode.com/posts")
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("event_0.json")
                        .using(factory));
        LOGGER.info("Schema Validated !!!");

    }


    /* Verify API returns more than 100 records
     */
    @org.junit.Test
    public void isThereHundredRecords(){
        given()
                .header("Content-type","application/json")
                .header("charset","utf-8")
                .when()
                .get("https://jsonplaceholder.typicode.com/posts").
                then()
                .contentType(ContentType.JSON)
                .statusCode(200) .log().all()
                .body("id", hasSize(100)); // verify 100 records exist or not
        LOGGER.info("100 Records present !!!");
    }



    /* Verify only one record is returned
     */
    @org.junit.Test
    public void verifySingleRecord(){
        Response response = given()
                .header("Content-type","application/json")
                .header("charset","utf-8")
                .when()
                .get("https://jsonplaceholder.typicode.com/posts/1").
                then()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.SC_OK)// Verify status code 200
                .extract().response();
               // verify not more than 1 record exist

        JsonPath json = response.jsonPath();
        assertNotEquals("2", json.getString("id"));
        LOGGER.info("Only one record present!!!");

    }



    /* Verify that id in response matches input(1)
     */
    @org.junit.Test
    public void verifySingleRecordId(){

       Response response = given()
                       .header("Content-type","application/json")
                .header("charset","utf-8")
                .when()
                .get("https://jsonplaceholder.typicode.com/posts/1").
                then()
                .statusCode(HttpStatus.SC_OK) //verify status code 200
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertEquals("1", json.getString("id")); // verify input(1) in id of response
    }

    /* Verify Schema
     */
    @org.junit.Test
    public void verifySingleRecordSchema(){

        JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
                .setValidationConfiguration(
                        ValidationConfiguration.newBuilder()
                                .setDefaultVersion(SchemaVersion.DRAFTV4).freeze())
                .freeze();
        JsonSchemaValidator.settings = settings()
                .with().jsonSchemaFactory(factory)
                .and().with().checkedValidation(false);

        Response response = given()
                .header("Content-type","application/json")
                .header("charset","utf-8")
                .when()
                .get("https://jsonplaceholder.typicode.com/posts/1").
                        then()
                .statusCode(HttpStatus.SC_OK) //verify status code 200
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertEquals("1", json.getString("id")); // verify input(1) in id of response
        response.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("event_2.json")  //Verify Schema as per JSON schema Draft V4
                        .using(factory));
    }


    /* Verify Status code is 404 for Invalid Posts and log full response
     */
   @Test
    public void verifyInvalidStatusCode(){
        given()
                .header("Content-type","application/json")
                .header("charset","utf-8")
                .when()
                .get("https://jsonplaceholder.typicode.com/invalidposts").
                then()
                .statusCode(HttpStatus.SC_NOT_FOUND)  // verify status code 404
                .log().all();  // log complete request and response details
                LOGGER.info("Status code 404 !!!");
    }


}
