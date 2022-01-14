package facades;

import DTO.StatusDTOS.StatusDTO;
import DTO.TripDTOS.TripDTO;
import entities.Guide;
import entities.Trip;
import entities.User;
import errorhandling.API_Exception;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class TripFacade {

    private static EntityManagerFactory emf;
    private static TripFacade instance;

    private TripFacade() {
    }

    /**
     * @param _emf
     * @return the instance of this facade.
     */
    public static TripFacade getTripFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new TripFacade();
        }
        return instance;
    }

    public List<TripDTO> getAllTrips() throws API_Exception {
        EntityManager em = emf.createEntityManager();
        List<Trip> trips;
        try {
            TypedQuery<Trip> tq = em.createQuery("select t from Trip t", Trip.class);
            trips = tq.getResultList();
        } catch (Exception e) {
            throw new API_Exception();
        } finally {
            em.close();
        }
        return new TripDTO(trips).getTrips();
    }

    public StatusDTO addTripToUser(String username, Long tripId) throws API_Exception {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, tripId);
            User user = em.find(User.class, username);
            user.addTrip(trip);
            em.merge(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new API_Exception();
        } finally {
            em.close();
        }
        return new StatusDTO("Success", "Trip added to " + username);
    }

    public StatusDTO removeTripFromUser(String username, Long tripId) throws API_Exception {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, tripId);
            User user = em.find(User.class, username);
            user.removeTrip(trip);
            em.merge(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new API_Exception();
        } finally {
            em.close();
        }
        return new StatusDTO("Success", "Trip removed from " + username);
    }

    public StatusDTO createTrip(TripDTO tripDTO) throws Exception {
        EntityManager em = emf.createEntityManager();
        Trip trip = new Trip(tripDTO.getName(), tripDTO.getDate(), tripDTO.getLocation(), tripDTO.getDuration(), tripDTO.getPackingList());

        try {
            em.getTransaction().begin();
            em.persist(trip);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new Exception();
        } finally {
            em.close();
        }
        return new StatusDTO("Success", "New trip created");
    }

    public StatusDTO removeTrip(Long tripId) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, tripId);
            em.remove(trip);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new StatusDTO("Success", "Trip removed");
    }

    public StatusDTO addGuideToTrip(Long guideId, Long tripId) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            Guide guide = em.find(Guide.class, guideId);
            Trip trip = em.find(Trip.class, tripId);
            trip.addGuide(guide);
            em.merge(trip);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new StatusDTO("Success", "Guide added to trip");
    }
}
