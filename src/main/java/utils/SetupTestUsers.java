package utils;

import entities.Guide;
import entities.Role;
import entities.Trip;
import entities.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class SetupTestUsers {

    public static void main(String[] args) {
        setupTestUsers();
    }

    public static void setupTestUsers() {
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        Calendar cal = Calendar.getInstance();

        User user = new User("user", "kode123");
        User admin = new User("admin", "kode123");
        User both = new User("user_admin", "kode123");
        Role userRole = new Role("user");
        Role adminRole = new Role("admin");
        cal.set(2022,Calendar.JANUARY,1);
        Trip trip1 = new Trip("Hike", cal.getTime() , cal.getTime(),"Himmelbjerget","2 hours","Water");
        cal.set(2022,Calendar.MARCH,17);
        Trip trip2 = new Trip("Dance",cal.getTime(),cal.getTime(),"Byen","5 hours","Dancing shoes");
        Guide guide1 = new Guide("Anders","Mand","1987","I like to swim","www.test.com");
        Guide guide2 = new Guide("Lisa","Kvinde","1975","I like to dance","www.test.com");

        user.addRole(userRole);
        admin.addRole(adminRole);
        both.addRole(userRole);
        both.addRole(adminRole);
        user.addTrip(trip1);
        user.addTrip(trip2);
        trip1.addGuide(guide1);
        trip2.addGuide(guide2);

        em.getTransaction().begin();
        em.persist(guide1);
        em.persist(guide2);
        em.persist(trip1);
        em.persist(trip2);
        em.persist(userRole);
        em.persist(adminRole);
        em.persist(user);
        em.persist(admin);
        em.persist(both);
        em.getTransaction().commit();
    }

}