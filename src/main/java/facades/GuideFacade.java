package facades;

import DTO.GuideDTOS.GuideDTO;
import DTO.StatusDTOS.StatusDTO;
import entities.Guide;
import errorhandling.API_Exception;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class GuideFacade {

    private static EntityManagerFactory emf;
    private static GuideFacade instance;

    private GuideFacade() {
    }

    /**
     * @param _emf
     * @return the instance of this facade.
     */
    public static GuideFacade getGuideFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new GuideFacade();
        }
        return instance;
    }

    public GuideDTO getGuide(Long id) {
        EntityManager em = emf.createEntityManager();
        GuideDTO guideDTO;
        try {
            em.getTransaction().begin();
            Guide guide = em.find(Guide.class, id);
            em.getTransaction().commit();
            guideDTO = new GuideDTO(guide);
        } finally {
            em.close();
        }
        return guideDTO;
    }

    public List<GuideDTO> getAlleGuides() {
        EntityManager em = emf.createEntityManager();
        List<Guide> guides;
        try {
            TypedQuery<Guide> tq = em.createQuery("select g from Guide g", Guide.class);
            guides = tq.getResultList();
        } finally {
            em.close();
        }
        return new GuideDTO(guides).getGuides();
    }

    public StatusDTO createGuide(GuideDTO guideDTO) throws API_Exception {
        EntityManager em = emf.createEntityManager();
        Guide guide = new Guide(guideDTO.getName(), guideDTO.getGender(), guideDTO.getBirthYear(), guideDTO.getProfile(), guideDTO.getImageUrl());

        try {
            em.getTransaction().begin();
            em.persist(guide);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new API_Exception();
        } finally {
            em.close();
        }

        return new StatusDTO("Success", "New guide created");
    }
}
