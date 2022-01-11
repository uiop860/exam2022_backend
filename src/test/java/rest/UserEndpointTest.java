package rest;

import DTO.UserDTOS.UserDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nimbusds.jose.shaded.json.JSONObject;
import entities.Role;
import entities.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import io.restassured.specification.RequestSpecification;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

//Disabled
public class UserEndpointTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    private Gson gson = new Gson();

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();

        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.resetAutoIncrement").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.resetAutoIncrement").executeUpdate();
            em.getTransaction().commit();

            User user1 = new User("user1", "kode123");
            User user2 = new User("user2", "kode123");
            User admin = new User("admin", "kode123");
            User both = new User("user_admin", "kode123");

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");

            em.getTransaction().begin();
            em.persist(userRole);
            em.persist(adminRole);
            user1.addRole(userRole);
            em.persist(user1);
            em.getTransaction().commit();

            em.getTransaction().begin();
            user2.addRole(userRole);
            em.persist(user2);
            em.getTransaction().commit();

            em.getTransaction().begin();
            admin.addRole(adminRole);
            em.persist(admin);
            em.getTransaction().commit();

            em.getTransaction().begin();
            both.addRole(userRole);
            both.addRole(adminRole);
            em.persist(both);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;

    //Utility method to login and set the returned securityToken
    private static void login(String username, String password) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("username", username);
        requestParams.put("password", password);

        securityToken = given()
                .contentType("application/json")
                .body(requestParams.toJSONString())
            .when()
                .post("/user")
            .then()
                .extract().path("token");
    }

    private void logOut() {
        securityToken = null;
    }



    @Test
    public void createUserTest() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("username", "megatest");
        requestParams.put("password", "thisisatest123");

        logOut();
        given()
                .contentType("application/json")
                .body(requestParams.toJSONString())
            .when()
                .post("user/create")
            .then()
                .statusCode(200)
                .body("status", equalTo("Success"))
                .body("message", equalTo("User successfully created with username: megatest"));
    }

    @Test
    public void createAlreadyExistingUserTest() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("username", "user1");
        requestParams.put("password", "test");

        logOut();
        given()
                .contentType("application/json")
                .body(requestParams.toJSONString())
            .when()
                .post("user/create")
            .then()
                .statusCode(400)
                .body("message", equalTo("Failed to create user"));
    }

    @Test
    public void deleteUserTest() {
        login("admin", "kode123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
            .when()
                .delete("user/delete/user1")
            .then()
                .statusCode(200)
                .body("status", equalTo("Success"))
                .body("message", equalTo("Deleted user on username: user1"));
    }

    @Test
    public void deleteNotExistingUserTest() {
        login("admin", "kode123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .delete("user/delete/kage123")
                .then()
                .statusCode(400)
                .body("message", equalTo("Failed to delete user"));
    }

    @Test
    public void updateUserPasswordTest() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("username","user1");
        requestParams.put("password","test1");

        login("user1", "kode123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(requestParams.toJSONString())
            .when()
                .put("user/update/user1")
            .then()
                .statusCode(200)
                .body("status", equalTo("Success"))
                .body("message", equalTo("Password changed on username: user1"));
    }

    @Test
    public void partialUsernameSearchTest(){
        login("user1","kode123");
        Response response = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
            .when()
                .get("user/search/ser")
            .then()
                .statusCode(200)
                .extract().response();

        List<String> jsonResponse = response.jsonPath().getList("$");
        Assertions.assertEquals(3,jsonResponse.size());
    }
}
