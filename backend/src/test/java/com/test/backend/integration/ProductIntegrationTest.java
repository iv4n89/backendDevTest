package com.test.backend.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Product Integration Tests")
public class ProductIntegrationTest {

    @LocalServerPort
    private int port;

    private static WireMockServer wireMockServer;
    private static int wireMockPort;

    // Initialize WireMock server before all tests
    // use dynamic port to avoid conflicts with mock server
    @BeforeAll
    static void setupWireMock() {
        wireMockServer = new WireMockServer(
                WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        wireMockPort = wireMockServer.port();

        WireMock.configureFor("localhost", wireMockPort);
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("api.product.base-url",
                () -> "http://localhost:" + wireMockPort);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        wireMockServer.resetAll();
    }

    @AfterAll
    static void tearDownWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Case 1: Product with similar ids - should return 200 OK with similar products")
    void shouldReturnSimilarProductsWhenProductExists() {
        // Mock similarids endpoint
        stubFor(get(urlEqualTo("/product/1/similarids"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"2\", \"3\", \"4\"]")));

        // Mock product details
        stubFor(get(urlEqualTo("/product/2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                    {
                                        "id": "2",
                                        "name": "Dress",
                                        "price": 19.99,
                                        "availability": true
                                    }
                                """)));

        stubFor(get(urlEqualTo("/product/3"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                    {
                                        "id": "3",
                                        "name": "Shirt",
                                        "price": 29.99,
                                        "availability": true
                                    }
                                """)));

        stubFor(get(urlEqualTo("/product/4"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                    {
                                        "id": "4",
                                        "name": "Pants",
                                        "price": 39.99,
                                        "availability": false
                                    }
                                """)));

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/product/1/similar")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThan(0)))
                .body("[0].id", notNullValue())
                .body("[0].name", notNullValue())
                .body("[0].price", notNullValue())
                .body("[0].availability", notNullValue());
    }

    @Test
    @Order(2)
    @DisplayName("Case 2: Product without similar ids - should return 200 OK with empty list")
    void shouldReturnEmptyListWhenNoSimilarProducts() {
        // Mock product with no similar ids
        stubFor(get(urlEqualTo("/product/999/similarids"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application-json")
                        .withBody("[]")));

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/product/999/similar")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(0));
    }

    @Test
    @Order(3)
    @DisplayName("Case 3: Product with unavailable similar products - should filter them")
    void shouldFilterUnavailableSimilarProducts() {
        // Mock similarids endpoint
        stubFor(get(urlEqualTo("/product/5/similarids"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"6\", \"7\", \"8\"]")));

        // Mock product details with one unavailable product
        stubFor(get(urlEqualTo("/product/6"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                                {
                                                    "id": "6",
                                                    "name": "Hat",
                                                    "price": 15.99,
                                                    "availability": true
                                }
                                            """)));

        stubFor(get(urlEqualTo("/product/7"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                                {
                                                    "id": "7",
                                                    "name": "Scarf",
                                                    "price": 9.99,
                                                    "availability": false
                                }
                                            """)));

        stubFor(get(urlEqualTo("/product/8"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                                {
                                                    "id": "8",
                                                    "name": "Gloves",
                                                    "price": 12.99,
                                                    "availability": true
                                }
                                            """)));

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/product/5/similar")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(2))
                .body("id", not(hasItem("7")));
    }

    @Test
    @Order(4)
    @DisplayName("Case 4: Non-existent product - should return 200 OK with empty list")
    void shouldReturnEmptyListWhenProductDoesNotExist() {
        // Mock similarids endpoint returning 404
        stubFor(get(urlEqualTo("/product/404/similarids"))
                .willReturn(aResponse()
                        .withStatus(404)));

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/product/404/similar")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(0));
    }

    @Test
    @Order(5)
    @DisplayName("Case 5: External service failure - should return 200 OK with empty list")
    void shouldReturnEmptyListWhenExternalServiceFails() {
        // Mock similarids endpoint returning 500
        stubFor(get(urlEqualTo("/product/500/similarids"))
                .willReturn(aResponse()
                        .withStatus(500)));

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/product/500/similar")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(0));
    }
}
