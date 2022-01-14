package rest;

import DTO.StatusDTOS.StatusDTO;
import DTO.TripDTOS.TripDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import errorhandling.API_Exception;
import facades.TripFacade;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("trip")
public class TripResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "user"})
    @Path("all")
    public String getAllTrips() throws API_Exception {
        TripFacade facade = TripFacade.getTripFacade(EMF);
        List<TripDTO> tripsDTO;

        try {
            tripsDTO = facade.getAllTrips();
        } catch (Exception e) {
            throw new API_Exception("Something went wrong", 400, e);
        }
        return gson.toJson(tripsDTO);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "user"})
    @Path("adduser")
    public String addUserToTrip(String jsonString) throws API_Exception {
        TripFacade facade = TripFacade.getTripFacade(EMF);
        StatusDTO statusDTO;
        Long tripId;
        String username;

        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            tripId = json.get("tripid").getAsLong();
            username = json.get("username").getAsString();
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }

        try {
            statusDTO = facade.addTripToUser(username, tripId);
        } catch (Exception e) {
            throw new API_Exception("Something went wrong", 400, e);
        }

        return gson.toJson(statusDTO);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "user"})
    @Path("removeuser")
    public String removeUserFromTrip(String jsonString) throws API_Exception {
        TripFacade facade = TripFacade.getTripFacade(EMF);
        StatusDTO statusDTO;
        Long tripId;
        String username;

        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            tripId = json.get("tripid").getAsLong();
            username = json.get("username").getAsString();
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }

        try {
            statusDTO = facade.removeTripFromUser(username, tripId);
        } catch (Exception e) {
            throw new API_Exception("Something went wrong", 400, e);
        }
        return gson.toJson(statusDTO);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin"})
    @Path("create")
    public String createTrip(String jsonString) throws API_Exception {
        TripFacade facade = TripFacade.getTripFacade(EMF);
        StatusDTO statusDTO;
        TripDTO tripDTO;

        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            tripDTO = gson.fromJson(json, TripDTO.class);
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }

        try {
            statusDTO = facade.createTrip(tripDTO);
        } catch (Exception e) {
            throw new API_Exception("Something went wrong", 400, e);
        }
        return gson.toJson(statusDTO);
    }

    @POST
    @Produces
    @RolesAllowed({"admin"})
    @Path("remove/{tripId}")
    public String removeTrip(@PathParam("tripId") Long tripId) throws API_Exception {
        TripFacade facade = TripFacade.getTripFacade(EMF);
        StatusDTO statusDTO;

        try {
            statusDTO = facade.removeTrip(tripId);
        } catch (Exception e) {
            throw new API_Exception("Something went wrong", 400, e);
        }
        return gson.toJson(statusDTO);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin"})
    @Path("addguide")
    public String addTripToGuide(String jsonString) throws API_Exception {
        TripFacade facade = TripFacade.getTripFacade(EMF);
        StatusDTO statusDTO;
        Long guideId;
        Long tripId;

        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            guideId = json.get("guideId").getAsLong();
            tripId = json.get("tripId").getAsLong();
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }

        try {
            statusDTO = facade.addGuideToTrip(guideId, tripId);
        } catch (Exception e) {
            throw new API_Exception("Something went wrong", 400, e);
        }

        return gson.toJson(statusDTO);
    }
}





