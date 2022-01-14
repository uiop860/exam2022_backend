package utils;

import entities.Guide;
import entities.Role;
import entities.Trip;
import entities.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Calendar;
import java.util.TimeZone;

public class SetupTestUsers {

    public static void main(String[] args) {
        setupTestUsers();
    }

    public static void setupTestUsers() {
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

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
        Trip trip3 = new Trip("Dance",cal.getTime(),"Byen","5 hours","Dancing shoes");
        Trip trip4 = new Trip("Dance",cal.getTime(),"Byen","5 hours","Dancing shoes");
        Trip trip5 = new Trip("Dance",cal.getTime(),"Byen","5 hours","Dancing shoes");
        Trip trip6 = new Trip("Dance",cal.getTime(),"Byen","5 hours","Dancing shoes");
        Trip trip7 = new Trip("Dance",cal.getTime(),"Byen","5 hours","Dancing shoes");
        Trip trip8 = new Trip("Dance",cal.getTime(),"Byen","5 hours","Dancing shoes");
        Trip trip9 = new Trip("Dance",cal.getTime(),"Byen","5 hours","Dancing shoes");
        Trip trip10 = new Trip("Dance",cal.getTime(),"Byen","5 hours","Dancing shoes");
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
        em.persist(trip3);
        em.persist(trip4);
        em.persist(trip5);
        em.persist(trip6);
        em.persist(trip7);
        em.persist(trip8);
        em.persist(trip9);
        em.persist(trip10);
        em.persist(userRole);
        em.persist(adminRole);
        em.persist(user);
        em.persist(admin);
        em.persist(both);
        em.getTransaction().commit();
    }

}