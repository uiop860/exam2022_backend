package facades;

import DTO.TripDTOS.TripDTO;
import entities.Guide;
import entities.Role;
import entities.Trip;
import entities.User;
import errorhandling.API_Exception;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class TripFacadeTest {

    private static EntityManagerFactory emf;
    private static TripFacade facade;

    @BeforeAll
    static void beforeAll() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = TripFacade.getTripFacade(emf);
    }

    @BeforeEach
    void setUp() {
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

    @Test
    void getAllTripsTest() throws API_Exception {
        List<TripDTO> trips;

        trips = facade.getAllTrips();

        Assertions.assertEquals(2, trips.size());
    }

    @Test
    void addTripToUserTest() throws API_Exception {
        EntityManager em = emf.createEntityManager();
        Trip trip;
        User user;

        try {
            facade.addTripToUser("admin", 1L);
            trip = em.find(Trip.class,1L);
            user = em.find(User.class,"admin");
        } finally {
            em.close();
        }
        Assertions.assertTrue(trip.getUsers().contains(user));
    }


    @Test
    void removeTripFromUserTest() throws API_Exception {
        EntityManager em = emf.createEntityManager();
        Trip trip;
        User user;

        try {
            facade.removeTripFromUser("user",1L);
            trip = em.find(Trip.class,1L);
            user = em.find(User.class,"user");
        } finally {
            em.close();
        }
        Assertions.assertFalse(trip.getUsers().contains(user));
    }

    @Test
    void createTripTest() throws Exception {
        EntityManager em = emf.createEntityManager();
        Trip trip;
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("CET"));
        cal.set(2022,Calendar.SEPTEMBER,13);

        try {
            facade.createTrip(new TripDTO("Wakeboarding",cal.getTime(),"Hørsholm","3 hours","Wakeboard"));
            TypedQuery<Trip> tq = em.createQuery("select t from Trip t where t.Name = 'Wakeboarding'",Trip.class);
            trip = tq.getSingleResult();
        } finally {
            em.close();
        }
        Assertions.assertEquals("Hørsholm",trip.getLocation());
    }

    @Test
    void removeTripTest() {
        EntityManager em = emf.createEntityManager();
        Trip trip;

        try{
            facade.removeTrip(1L);
            trip = em.find(Trip.class,1L);
        } finally {
            em.close();
        }
        Assertions.assertNull(trip);
    }

    @Test
    void addGuideToTrip() {
        EntityManager em = emf.createEntityManager();
        Trip trip;

        try{
            facade.addGuideToTrip(1L,2L);
            trip = em.find(Trip.class, 2L);
        } finally {
            em.close();
        }
        Assertions.assertEquals(1L,trip.getGuide().getId());
    }
}
