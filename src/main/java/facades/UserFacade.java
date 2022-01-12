package facades;

import DTO.StatusDTO.StatusDTO;
import DTO.UserDTOS.UserDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Role;
import entities.User;
import errorhandling.API_Exception;
import security.errorhandling.AuthenticationException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lam@cphbusiness.dk
 */
public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private UserFacade() {
    }

    /**
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User getVerifiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password, user.getUserPass())) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public StatusDTO createUser(String username, String password) {
        EntityManager em = emf.createEntityManager();

        User user = new User(username, password);
        Role userRole;
        try {
            if (em.find(Role.class, "user") != null) {
                em.getTransaction().begin();
                userRole = em.find(Role.class, "user");
                user.addRole(userRole);
            } else {
                Role newUserRole = new Role("user");
                em.getTransaction().begin();
                em.persist(newUserRole);
                user.addRole(newUserRole);
            }
            em.persist(user);
            em.getTransaction().commit();
        }finally {
            em.close();
        }
        return new StatusDTO("Success", "User successfully created with username: " + username);
    }

    public StatusDTO deleteUser(String userName) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new StatusDTO("Success", "Deleted user on username: " + userName);
    }

    public StatusDTO updateUserPassword(String userName, String newPassword, String oldPassword) throws Exception {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            if(user.verifyPassword(oldPassword,user.getUserPass())){
                user.setUserPass(newPassword);
                em.merge(user);
            } else {
                throw new Exception();
            }
            em.getTransaction().commit();

        } finally {
            em.close();
        }

        return new StatusDTO("Success", "Password changed on username: " + userName);
    }

    public List<UserDTO> partialUsernameSearch(String userName) {
        EntityManager em = emf.createEntityManager();
        List<User> users;
        List<UserDTO> userDTOS = new ArrayList<>();

        try {
            TypedQuery<User> tq = em.createQuery("select u from User u where u.userName like :username",User.class);
            tq.setParameter("username","%" + userName + "%");
            users = tq.getResultList();
        } finally {
            em.close();
        }

        for (User user: users) {
            userDTOS.add(new UserDTO(user));
        }

        return userDTOS;
    }
}
