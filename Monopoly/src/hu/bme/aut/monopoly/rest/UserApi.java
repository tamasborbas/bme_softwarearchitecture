package hu.bme.aut.monopoly.rest;

import hu.bme.aut.monopoly.email.EmailManager;
import hu.bme.aut.monopoly.model.Game;
import hu.bme.aut.monopoly.model.GameStatus;
import hu.bme.aut.monopoly.model.MonopolyEntityManager;
import hu.bme.aut.monopoly.model.PlayerStatus;
import hu.bme.aut.monopoly.model.User;
import hu.bme.aut.monopoly.model.UserType;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Class for the user specific rest requests
 */
@Path("/userapi")
public class UserApi
{
    /**
     * Checks the user name and the password, and create a session for the user
     */
    @Path("/Login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public HttpServletResponse login(String json, @Context
    HttpServletRequest request, @Context
    HttpServletResponse response)
    {
        System.out.println("Login start: " + json + "itt");

        JSONArray jsonTomb;
        String userName = null;
        String password = null;
        try
        {
            jsonTomb = new JSONArray(json);
            userName = jsonTomb.getJSONObject(0).getString("userName");
            password = jsonTomb.getJSONObject(0).getString("password");
        } catch (JSONException e1)
        {
            try
            {
                response.sendError(403, "Invalid JSON");
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            e1.printStackTrace();
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("loggedInUser", "");

        System.out.println(userName + " - " + password);
        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();

        if (mem.getUserIsRegistered(userName, password))
        {
            session.setAttribute("loggedInUser", mem.getUserByName(userName).getEmail());
            System.out.println("Login okay");
        } else
        {
            System.out.println("Login NOT okay");
            session.invalidate();
            try
            {
                response.sendError(401, "The user is not registered");
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        mem.closeDB();
        return response;
    }

    /**
     * Invalidate the session of the user
     */
    @Path("/Logout")
    @POST
    public HttpServletResponse logout(@Context
    HttpServletRequest request, @Context
    HttpServletResponse response)
    {
        System.out.println("logout");
        HttpSession session = request.getSession(true);
        session.invalidate();

        System.out.println("logout");
        return response;
    }

    /**
     * Checks the given parameters uniqueness, and than register the user to the database
     */
    @Path("/Registration")
    @POST
    public Response addUser(String json, @Context
    HttpServletRequest request) throws JSONException
    {

        // session, helyes regisztracional be is leptetjük.
        HttpSession session = request.getSession(true);
        session.setAttribute("loggedInUser", "");

        JSONArray jsonTomb;
        String email = null;
        String password = null;
        String name = null;
        try
        {

            jsonTomb = new JSONArray(json);
            email = jsonTomb.getJSONObject(0).getString("email");
            password = jsonTomb.getJSONObject(0).getString("password");
            name = jsonTomb.getJSONObject(0).getString("name");
        } catch (JSONException e1)
        {
            e1.printStackTrace();
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid JSON").build();
        }

        int errorCode = 0;

        if (null != email && null != password && null != name)
        {
            MonopolyEntityManager mem = new MonopolyEntityManager();

            try
            {
                mem.initDB();
                if (mem.isUserEmailRegistered(email))
                {
                    errorCode = 2;
                } else if (mem.isUserNameRegistered(name))
                {
                    errorCode = 1;
                } else
                {
                    mem.addNewUser(email, password, name, UserType.user);
                    mem.closeDB();
                }

            } catch (Exception e)
            {
                e.printStackTrace();
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Database error").build();
            }
            mem = new MonopolyEntityManager();
            mem.initDB();
        }

        JSONObject responseJsonObject = new JSONObject();
        responseJsonObject.put("errorCode", errorCode);

        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    /**
     * Sends a reminder email for registered users
     */
    @Path("/Remind")
    @POST
    public Response remind(String json) throws JSONException
    {
        boolean success = false;

        JSONArray jsonTomb;
        String email = null;
        try
        {
            jsonTomb = new JSONArray(json);
            email = jsonTomb.getJSONObject(0).getString("email");
        } catch (JSONException e1)
        {
            e1.printStackTrace();
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid JSON").build();
        }

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        User user;
        if (mem.isUserEmailRegistered(email))
        {
            user = mem.getUserByEmail(email);
        } else
        {
            return Response.status(Response.Status.NOT_FOUND).entity("The given email address not found in database")
                    .build();
        }
        mem.closeDB();

        success = EmailManager.sendReminderEmail(email, user.getName(), user.getPassword());

        JSONObject responseJsonObject = new JSONObject();
        try
        {
            responseJsonObject.put("success", success);
        } catch (JSONException e)
        {
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid JSON").build();
        }

        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    /**
     * Returns the personal details of the user
     */
    @Path("/GetProfil")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfil(@Context
    HttpServletRequest request)
    {
        String loggedInUseremail = Helper.getLoggedInUserEmail(request);
        JSONObject responseJsonObject = new JSONObject();

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        User user = mem.getUserByEmail(loggedInUseremail);

        int wonGamesNum = Helper.getNumberOfPlayerInAStatus(user, PlayerStatus.win);
        int invitationsNum = Helper.getNumberOfPlayerInAStatus(user, PlayerStatus.notAcceptedYet);

        int activeGamesNum = 0;
        List<Game> ownedGames = mem.getOwnedGamesByUser(user);
        for (Game game : ownedGames)
        {
            if (game.getGameStatus() == GameStatus.inProgress)
            {
                activeGamesNum++;
            }
        }
        try
        {
            responseJsonObject.put("name", user.getName());
            responseJsonObject.put("email", user.getEmail());
            responseJsonObject.put("participatedGamesNum", user.getGamePlayers().size());
            responseJsonObject.put("ownGamesNum", ownedGames.size());
            responseJsonObject.put("wonGamesNum", wonGamesNum);
            responseJsonObject.put("activeGamesNum", activeGamesNum);
            responseJsonObject.put("invitationsNum", invitationsNum);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
        }
        mem.closeDB();

        System.out.println(responseJsonObject);
        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

}
