package rest;

import DTO.GuideDTOS.GuideDTO;
import DTO.StatusDTOS.StatusDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import errorhandling.API_Exception;
import facades.GuideFacade;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.List;

@Path("guide")
public class GuideResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    public String getAllGuides() throws API_Exception {
        GuideFacade facade = GuideFacade.getGuideFacade(EMF);
        List<GuideDTO> guideDTO;

        try {
            guideDTO = facade.getAlleGuides();
        } catch(Exception e) {
            throw new API_Exception("Something went wrong", 400,e);
        }
        return gson.toJson(guideDTO);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String getGuide(@PathParam("id") Long id) throws API_Exception {

        GuideFacade facade = GuideFacade.getGuideFacade(EMF);
        GuideDTO guideDTO;

        try {
            guideDTO = facade.getGuide(id);
        } catch(Exception e) {
            throw new API_Exception("Something went wrong", 400,e);
        }
        return gson.toJson(guideDTO);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("create")
    public String createGuide(String jsonString) throws API_Exception, ParseException {
        GuideFacade facade = GuideFacade.getGuideFacade(EMF);
        StatusDTO statusDTO;
        GuideDTO guideDTO;

        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            guideDTO = gson.fromJson(json,GuideDTO.class);
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }

        try {
            statusDTO = facade.createGuide(guideDTO);
        } catch(Exception e) {
            throw new API_Exception("Something went wrong", 400, e);
        }
        return gson.toJson(statusDTO);
    }
}
