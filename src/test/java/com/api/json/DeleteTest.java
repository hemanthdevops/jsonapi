package com.api.json;


import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class DeleteTest {

    private final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private final String POSTS = "posts";

    private static final Logger LOGGER = LogManager.getLogger(DeleteTest.class.getName());
    @Test
    public void jsonplaceholderDelete() {
        Response response = given()
                .header("Content-type","application/json")
                .header("charset","utf-8")
                .when()
                .delete(BASE_URL + "/" + POSTS + "/1")
                .then()
                .statusCode(HttpStatus.SC_OK) //Verify status code 200
                .extract()
                .response();
        LOGGER.info("Status Code 200 validated !!!");

        JsonPath json = response.jsonPath();
        assertEquals((Byte) null, json.get("id")); //Verify response for record deletion
        LOGGER.info("Record deleted !!!");

    }
}