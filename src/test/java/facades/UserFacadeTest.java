package facades;

import DTO.UserDTOS.UserDTO;
import entities.Role;
import entities.User;
import errorhandling.API_Exception;
import org.junit.jupiter.api.*;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class UserFacadeTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;

    @BeforeAll
    static void beforeAll() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = UserFacade.getUserFacade(emf);
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

    @Test
    public void getVerifiedUserTest() throws AuthenticationException {
        User user = facade.getVerifiedUser("user1","kode123");

        Assertions.assertEquals("user1", user.getUserName());
        Assertions.assertTrue(user.verifyPassword("kode123",user.getUserPass()));
    }

    @Test
    public void createUserTest() throws API_Exception {
        EntityManager em = emf.createEntityManager();
        User user;

        facade.createUser("test","test");

        try {
            user = em.find(User.class,"test");
        } finally {
            em.close();
        }

        Assertions.assertEquals("test", user.getUserName());
        Assertions.assertTrue(user.verifyPassword("test",user.getUserPass()));
    }

    @Test
    public void deleteUserTest() {
        EntityManager em = emf.createEntityManager();
        User user;

        facade.deleteUser("user1");

        try {
            user = em.find(User.class, "user1");
        } finally {
            em.close();
        }

        Assertions.assertNull(user);
    }

    @Test
    public void updateUserPasswordTest() throws Exception {
        EntityManager em = emf.createEntityManager();
        User user;

        facade.updateUserPassword("user2","test123", "kode123");

        try {
            user = em.find(User.class, "user2");
        } finally {
            em.close();
        }

        Assertions.assertTrue(user.verifyPassword("test123",user.getUserPass()));
    }

    @Test
    public void partialUsernameSearch() {
        List<UserDTO> users = facade.partialUsernameSearch("ser");

        Assertions.assertEquals(3, users.size());
    }
}















