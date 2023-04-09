package com.lab.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.hamcrest.core.Is;
import org.jboss.resteasy.reactive.common.util.DateUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Date;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.get;
import static io.restassured.http.ContentType.JSON;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuarkusRestResourceTest {

    private static final String DEFAULT_NAME = "Sammy";
    private static final String DEFAULT_DOB = "1995-01-01";
    private static final String DEFAULT_GENDER = "MALE";
    private static final String DEFAULT_MOBILE = "9393939393";
    private static final String DEFAULT_ADDRESS = "10, Bright Street, S909090";
    private static final String UPDATED_MOBILE = "9393939383";
    private static final String UPDATED_ADDRESS = "11, Clive Street, S909091";
    private static final int NB_CUSTOMERS = 3;
    private static String customerId;

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/api/customers/hello")
          .then()
             .statusCode(200)
             .body(is("Hello from RESTEasy Reactive"));
    }

    @Test
    void shouldNotGetUnknownCustomer() {
        Long randomId = new Random().nextLong();
        given()
                .pathParam("id", randomId)
                .when().get("/api/customers/{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }
    @Test
    void shouldNotAddInvalidItem() throws Exception {
        Customer customer = new Customer();
        customer.name = null;
        customer.dob = DateUtil.parseDate("1990-01-01", List.of("YYYY-MM-DD"));
        customer.address = "90, MG Road, Blr";
        customer.mobile = "909091919192";
        customer.gender = "MALE";

        given()
                .body(customer)
                .header(CONTENT_TYPE, JSON)
                .header(ACCEPT, JSON)
                .when()
                .post("/api/customers")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    @Order(1)
    void shouldGetInitialItems() {
        List<Customer> customers = get("/api/customers").then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(getCustomerTypeRef());
        assertEquals(NB_CUSTOMERS, customers.size());
    }

    @Test
    @Order(2)
    void shouldAddAnItem() {
        Customer customer = new Customer();
        customer.name = DEFAULT_NAME;
        customer.dob = DateUtil.parseDate(DEFAULT_DOB, List.of("YYYY-MM-DD"));;
        customer.gender = DEFAULT_GENDER;
        customer.mobile = DEFAULT_MOBILE;
        customer.address = DEFAULT_ADDRESS;

        String location = given()
                .body(customer)
                .header(CONTENT_TYPE, JSON)
                .header(ACCEPT, JSON)
                .when()
                .post("/api/customers")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().header("Location");
        assertTrue(location.contains("/api/customers"));

        // Stores the id
        String[] segments = location.split("/");
        customerId = segments[segments.length - 1];
        assertNotNull(customerId);

        given()
                .pathParam("id", customerId)
                .when().get("/api/customers/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .body("name", Is.is(DEFAULT_NAME))
                .body("gender", Is.is(DEFAULT_GENDER))
                .body("address", Is.is(DEFAULT_ADDRESS))
                .body("mobile", Is.is(DEFAULT_MOBILE));
                //date check pending

        List<Customer> customers = get("/api/customers").then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(getCustomerTypeRef());
        assertEquals(NB_CUSTOMERS + 1, customers.size());
    }

    @Test
    @Order(3)
    void testUpdatingAnItem() {
        Customer customer = new Customer();
        customer.id = Long.valueOf(customerId);
        customer.name = DEFAULT_NAME;
        customer.dob = DateUtil.parseDate(DEFAULT_DOB, List.of("YYYY-MM-DD"));;
        customer.gender = DEFAULT_GENDER;
        customer.mobile = UPDATED_MOBILE;
        customer.address = UPDATED_ADDRESS;

        given()
                .body(customer)
                .header(CONTENT_TYPE, JSON)
                .header(ACCEPT, JSON)
                .when()
                .put("/api/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .body("name", Is.is(DEFAULT_NAME))
                .body("gender", Is.is(DEFAULT_GENDER))
                .body("address", Is.is(UPDATED_ADDRESS))
                .body("mobile", Is.is(UPDATED_MOBILE));
                //date check pending

        List<Customer> customers = get("/api/customers").then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(getCustomerTypeRef());
        assertEquals(NB_CUSTOMERS + 1, customers.size());
    }

    @Test
    @Order(4)
    void shouldRemoveAnItem() {
        given()
                .pathParam("id", customerId)
                .when().delete("/api/customers/{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        List<Customer> customers = get("/api/customers").then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().body().as(getCustomerTypeRef());
        assertEquals(NB_CUSTOMERS, customers.size());
    }


    private TypeRef<List<Customer>> getCustomerTypeRef() {
        return new TypeRef<List<Customer>>() {
            // Kept empty on purpose
        };
    }


}