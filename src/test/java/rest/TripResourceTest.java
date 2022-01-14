package rest;

import DTO.TripDTOS.TripDTO;
import com.google.gson.Gson;
import com.nimbusds.jose.shaded.json.JSONObject;
import entities.Guide;
import entities.Role;
import entities.Trip;
import entities.User;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TripResourceTest {

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
            em.createNamedQuery("Trip.deleteAllRows").executeUpdate();
            em.createNamedQuery("Trip.resetAutoIncrement").executeUpdate();
            em.createNamedQuery("Guide.deleteAllRows").executeUpdate();
            em.createNamedQuery("Guide.resetAutoIncrement").executeUpdate();
            em.getTransaction().commit();

            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("CET"));

            User user = new User("user", "kode123","Åmarksvej 24","27463547","user@user.com","1967","Mand");
            User admin = new User("admin", "kode123","Fredensvej 65", "96758453", "admin@admin.com","1990","Kvinde");
            User both = new User("user_admin", "kode123", "Kildevej 154","94857693","both@both.com","2000","Mand");
            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            cal.set(2022,Calendar.JANUARY,1);
            Trip trip1 = new Trip("Hike", cal.getTime() , "Himmelbjerget","2 hours","Water");
            cal.set(2022,Calendar.MARCH,17);
            Trip trip2 = new Trip("Dance",cal.getTime(),"Byen","5 hours","Dancing shoes");
            Guide guide1 = new Guide("Anders","Mand","1987","I like to swim","www.test.com");
            Guide guide2 = new Guide("Lisa","Kvinde","1975","I like to dance","www.test.com");

            em.getTransaction().begin();
            em.persist(userRole);
            em.persist(adminRole);
            em.getTransaction().commit();

            em.getTransaction().begin();
            em.persist(guide1);
            em.getTransaction().commit();

            em.getTransaction().begin();
            em.persist(guide2);
            em.getTransaction().commit();

            em.getTransaction().begin();
            trip1.addGuide(guide1);
            em.persist(trip1);
            em.getTransaction().commit();

            em.getTransaction().begin();
            trip2.addGuide(guide2);
            em.persist(trip2);
            em.getTransaction().commit();

            em.getTransaction().begin();
            user.addRole(userRole);
            user.addTrip(trip1);
            user.addTrip(trip2);
            em.persist(user);
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
    void getAllTripsTest() {
        login("user","kode123");
        System.out.println(securityToken);
        Response response = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
            .when()
                .get("trip/all")
            .then()
                .statusCode(200)
                .extract().response();

        List<String> jsonResponse = response.jsonPath().getList("$");
        Assertions.assertEquals(2,jsonResponse.size());
    }

    @Test
    void addTripToUserTest() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("username", "admin");
        requestParams.put("tripid", 1L);

        login("user","kode123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(requestParams.toJSONString())
            .when()
                .post("trip/adduser")
            .then()
                .statusCode(200)
                .body("status", equalTo("Success"))
                .body("message", equalTo("Trip added to admin"));
    }

    @Test
    void removeUserFromTripTest() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("username", "user");
        requestParams.put("tripid", 1L);

        login("user","kode123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(requestParams.toJSONString())
            .when()
                .post("trip/removeuser")
            .then()
                .statusCode(200)
                .body("status", equalTo("Success"))
                .body("message", equalTo("Trip removed from user"));
    }

    @Test
    void createTripTest(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("CET"));
        cal.set(2022,Calendar.SEPTEMBER,13);
        TripDTO trip = new TripDTO("Wakeboarding",cal.getTime(),"Hørsholm","3 hours","Wakeboard");

        login("user","kode123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(gson.toJson(trip))
            .when()
                .post("trip/create")
            .then()
                .statusCode(200)
                .body("status", equalTo("Success"))
                .body("message", equalTo("New trip created"));
    }

    @Disabled
    @Test
    void removeTripTest() {
        login("user","kode123");

        given()
                .contentType("application/json")
                .header("x-access-token",securityToken)
            .when()
                .post("trip/remove/1")
            .then()
                .statusCode(200)
                .body("status", equalTo("Success"));

    }

    @Test
    void addGuideToTripTest() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("guideId", 2L);
        requestParams.put("tripId", 1L);

        login("user","kode123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(requestParams.toJSONString())
            .when()
                .post("trip/addguide")
            .then()
                .statusCode(200)
                .body("status", equalTo("Success"))
                .body("message", equalTo("Guide added to trip"));
    }
}
