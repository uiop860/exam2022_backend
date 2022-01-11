package security;

import DTO.StatusDTO.StatusDTO;
import DTO.UserDTOS.UserDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import entities.User;
import errorhandling.API_Exception;
import errorhandling.GenericExceptionMapper;
import facades.UserFacade;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("user")
public class UserEndpoint {

    public static final int TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30 min
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String jsonString) throws AuthenticationException, API_Exception {
        String username;
        String password;
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            username = json.get("username").getAsString();
            password = json.get("password").getAsString();
        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }

        try {
            User user = USER_FACADE.getVerifiedUser(username, password);
            String token = createToken(username, user.getRolesAsStrings());
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("username", username);
            responseJson.addProperty("token", token);
            return Response.ok(new Gson().toJson(responseJson)).build();

        } catch (JOSEException | AuthenticationException ex) {
            if (ex instanceof AuthenticationException) {
                throw (AuthenticationException) ex;
            }
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new AuthenticationException("Invalid username or password! Please try again");
    }

    private String createToken(String userName, List<String> roles) throws JOSEException {

        StringBuilder res = new StringBuilder();
        for (String string : roles) {
            res.append(string);
            res.append(",");
        }
        String rolesAsString = res.length() > 0 ? res.substring(0, res.length() - 1) : "";
        String issuer = "semesterstartcode-dat3";

        JWSSigner signer = new MACSigner(SharedSecret.getSharedKey());
        Date date = new Date();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userName)
                .claim("username", userName)
                .claim("roles", rolesAsString)
                .claim("issuer", issuer)
                .issueTime(date)
                .expirationTime(new Date(date.getTime() + TOKEN_EXPIRE_TIME))
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("create")
    public String createUser(String jsonString) throws API_Exception {

        String username;
        String password;

        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            username = json.get("username").getAsString();
            password = json.get("password").getAsString();

        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }

        try {
            return gson.toJson(USER_FACADE.createUser(username, password));
        } catch (Exception e) {
            throw new API_Exception("Failed to create user", 400, e);
        }
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("delete/{userName}")
    @RolesAllowed("admin")
    public String deleteUser(@PathParam("userName") String userName) throws API_Exception {
        StatusDTO response;

        try {
            response = USER_FACADE.deleteUser(userName);
        } catch (Exception e) {
            throw new API_Exception("Failed to delete user", 400, e);
        }
        return gson.toJson(response);
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("update/{userName}")
    @RolesAllowed({"admin","user"})
    public String updateUserPassword(@PathParam("userName") String userName, String jsonString) throws API_Exception {

        String newPassword;
        StatusDTO response;

        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            newPassword = json.get("password").getAsString();

        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }

        try {
            response = USER_FACADE.updateUserPassword(userName, newPassword);

        } catch (Exception e) {
            throw new API_Exception("Failed to update user", 400, e);
        }

        return gson.toJson(response);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("search/{userName}")
    @RolesAllowed({"admin","user"})
    public String partialUsernameSearch(@PathParam("userName") String userName) throws API_Exception {

        List<UserDTO> response;

        try {
            response = USER_FACADE.partialUsernameSearch(userName);
        } catch(Exception e) {
            throw new API_Exception("Failed to find user(s)", 400, e);
        }
        return gson.toJson(response);
    }
}