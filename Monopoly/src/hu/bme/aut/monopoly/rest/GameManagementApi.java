package hu.bme.aut.monopoly.rest;

import hu.bme.aut.monopoly.model.Game;
import hu.bme.aut.monopoly.model.MonopolyEntityManager;
import hu.bme.aut.monopoly.model.Player;
import hu.bme.aut.monopoly.model.PlayerStatus;
import hu.bme.aut.monopoly.model.User;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@Path("/gamemanagementapi")
public class GameManagementApi
{
    @Path("/GetActiveGames")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActiveGames(@Context
    HttpServletRequest request)
    {
        System.out.println("GetActiveGames");
        String loggedInUseremail = Helper.getLoggedInUserEmail(request);

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();

        List<Game> activeGamesByEmail = mem.getActiveGamesByEmail(loggedInUseremail);

        JSONArray activeGamesJsonArray = new JSONArray();
        for (Game game : activeGamesByEmail)
        {
            System.out.println("JATEK NEV: " + game.getName());

            JSONObject aActiveGame = new JSONObject();
            try
            {
                aActiveGame.put("id", game.getId());
                aActiveGame.put("name", game.getName());
                aActiveGame.put("actualPlayer", game.getActualPlayer().getUser().getName());

                System.out.println("AKTIV JATEK: " + aActiveGame);
                JSONArray acceptedPlayersJsonArray = new JSONArray();

                for (Player player : game.getPlayers())
                {
                    System.out.println("JATEKOS NEV: " + player.getId() + " - " + player.getUser().getName());
                    if ((player.getPlayerStatus() == PlayerStatus.accepted) && !(player != game.getActualPlayer()))
                    {
                        JSONObject aAcceptedPlayer = new JSONObject();
                        aAcceptedPlayer.put("name", player.getUser().getName());
                        acceptedPlayersJsonArray.put(aAcceptedPlayer);
                    }
                }
                aActiveGame.put("players", acceptedPlayersJsonArray);

                System.out.println("A GAME OBJECT: " + aActiveGame);
                // activeGamesJsonArray.put(aActiveGame);

            } catch (JSONException e)
            {
                e.printStackTrace();
                return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
            }
            activeGamesJsonArray.put(aActiveGame);
        }
        mem.closeDB();

        JSONObject responseJsonObject = new JSONObject();
        try
        {
            responseJsonObject.put("activeGames", activeGamesJsonArray);
        } catch (JSONException e)
        {

            e.printStackTrace();
            return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
        }

        System.out.println("ELKULDOTT: " + responseJsonObject);
        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    @Path("/GetInvitations")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInvitations(@Context
    HttpServletRequest request)
    {
        System.out.println("GetInvitations");

        String loggedInUseremail = Helper.getLoggedInUserEmail(request);

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        User user = mem.getUserByEmail(loggedInUseremail);
        mem.closeDB();

        List<Player> gamePlayers = user.getGamePlayers();
        JSONArray notAcceptedYetGamesJsonArray = new JSONArray();

        for (Player player : gamePlayers)
        {
            if (player.getPlayerStatus() == PlayerStatus.notAcceptedYet)
            {
                try
                {
                    notAcceptedYetGamesJsonArray.put(Helper.getGameDetailes(player.getGame()));
                    System.out.println(player.getGame().getName());
                } catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
                }
            }
        }

        JSONObject responseJsonObject = new JSONObject();
        try
        {
            responseJsonObject.put("nayGames", notAcceptedYetGamesJsonArray);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
        }

        System.out.println(responseJsonObject);
        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    @Path("/AcceptInvitation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptInvitation(String json, @Context
    HttpServletRequest request)
    {
        return Helper.modifyPlayerStatus(json, request, PlayerStatus.accepted);
    }

    @Path("/RefuseInvitation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response refuseInvitation(String json, @Context
    HttpServletRequest request)
    {
        return Helper.modifyPlayerStatus(json, request, PlayerStatus.refused);
    }

}
