package rest;

import com.google.gson.Gson;
import com.nimbusds.jose.shaded.json.JSONObject;
import entities.Role;
import entities.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class DemoResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api/";

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
                .post("user")
                .then()
                .extract().path("token");
    }

    private void logOut() {
        securityToken = null;
    }

    @Test
    public void restNoAuthenticationRequiredTest() {
        given()
                .contentType("application/json")
                .when()
                .get("info/")
                .then()
                .statusCode(200)
                .body("msg", equalTo("Hello anonymous"));
    }

    @Test
    public void restForAdminTest() {
        login("admin", "kode123");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("info/admin")
                .then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin) User: admin"));
    }

    @Test
    public void restForUserTest() {
        login("user1", "kode123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("info/user")
                .then()
                .statusCode(200)
                .body("msg", equalTo("Hello to User: user1"));
    }

    @Test
    public void autorizedUserCannotAccesAdminPageTest() {
        login("user1", "kode123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("info/admin")
                .then() //Call Admin endpoint as user
                .statusCode(401);
    }

    @Test
    public void autorizedAdminCannotAccesUserPageTest() {
        login("admin", "kode123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("info/user")
                .then() //Call User endpoint as Admin
                .statusCode(401);
    }

    @Test
    public void restForMultiRole1Test() {
        login("user_admin", "kode123");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("info/admin")
                .then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin) User: user_admin"));
    }

    @Test
    public void restForMultiRole2Test() {
        login("user_admin", "kode123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("info/user")
                .then()
                .statusCode(200)
                .body("msg", equalTo("Hello to User: user_admin"));
    }

    @Test
    public void userNotAuthenticatedTest() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("info/user")
                .then()
                .statusCode(403)
                .body("code", equalTo(403))
                .body("message", equalTo("Not authenticated - do login"));
    }

    @Test
    public void adminNotAuthenticatedTest() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("info/user")
                .then()
                .statusCode(403)
                .body("code", equalTo(403))
                .body("message", equalTo("Not authenticated - do login"));
    }

}
