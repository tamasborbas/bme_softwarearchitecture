package hu.bme.aut.monopoly.rest;

import hu.bme.aut.monopoly.email.EmailManager;
import hu.bme.aut.monopoly.model.Game;
import hu.bme.aut.monopoly.model.GameStatus;
import hu.bme.aut.monopoly.model.MonopolyEntityManager;
import hu.bme.aut.monopoly.model.Place;
import hu.bme.aut.monopoly.model.Player;
import hu.bme.aut.monopoly.model.PlayerStatus;
import hu.bme.aut.monopoly.model.StartPlace;
import hu.bme.aut.monopoly.model.Step;
import hu.bme.aut.monopoly.model.User;
import hu.bme.aut.monopoly.model.UserType;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
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


/**
 * Class for the game management specific rest requests
 */
@Path("/gamemanagementapi")
public class GameManagementApi
{
    /**
     * Gives the active games with properties
     */
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

        JSONArray activeGamesJsonArray = new JSONArray();

        List<Game> activeGamesByEmail = mem.getActiveGamesByEmail(loggedInUseremail);
        // vegig megyunk az aktiv jatekokon
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

                // a jatekhoz tartozo accepted, nem actual playerek osszegyujtese
                for (Player player : game.getPlayers())
                {
                    System.out.println("JATEKOS NEV: " + player.getId() + " - " + player.getUser().getName());
                    if ((player.getPlayerStatus() == PlayerStatus.accepted) && (player != game.getActualPlayer()))
                    {
                        JSONObject aAcceptedPlayer = new JSONObject();
                        aAcceptedPlayer.put("name", player.getUser().getName());
                        acceptedPlayersJsonArray.put(aAcceptedPlayer);
                    }
                }
                aActiveGame.put("players", acceptedPlayersJsonArray);

                System.out.println("A GAME OBJECT: " + aActiveGame);
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

    /**
     * Gets the invitations of the user
     */
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

        // a userhez tartozo notAcceptedYet statuszu jatekosok lekerdezese
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

    /**
     * Accept a invitation
     */
    @Path("/AcceptInvitation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptInvitation(String json, @Context
    HttpServletRequest request)
    {
        String loggedInUseremail = Helper.getLoggedInUserEmail(request);
        return Helper.modifyPlayerStatus(json, request, PlayerStatus.accepted, loggedInUseremail);
    }

    /**
     * Accept a invitation from email
     */
    @Path("/AcceptInvitationFromEmail")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptInvitationFromEmail(String json, @Context
    HttpServletRequest request)
    {
        String userEmail = "";
        try
        {
            userEmail = Helper.getEmailFromJson(json);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid JSON").build();
        }
        return Helper.modifyPlayerStatus(json, request, PlayerStatus.accepted, userEmail);
    }

    /**
     * Refuse a invitation
     */
    @Path("/RefuseInvitation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response refuseInvitation(String json, @Context
    HttpServletRequest request)
    {
        String loggedInUseremail = Helper.getLoggedInUserEmail(request);
        return Helper.modifyPlayerStatus(json, request, PlayerStatus.refused, loggedInUseremail);
    }

    /**
     * Refuse a invitation
     */
    @Path("/RefuseInvitationFromEmail")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response refuseInvitationFromEmail(String json, @Context
    HttpServletRequest request)
    {
        String userEmail = "";
        try
        {
            userEmail = Helper.getEmailFromJson(json);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid JSON").build();
        }
        return Helper.modifyPlayerStatus(json, request, PlayerStatus.refused, userEmail);
    }

    /**
     * Start a game
     */
    @Path("/StartGame")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response startGame(String json, @Context
    HttpServletRequest request)
    {
        JSONArray jsonTomb;
        int gameId;
        boolean success = true;
        JSONObject responseJsonObject = new JSONObject();
        MonopolyEntityManager mem = new MonopolyEntityManager();
        try
        {
            mem.initDB();
            jsonTomb = new JSONArray(json);
            gameId = jsonTomb.getJSONObject(0).getInt("gameId");

            Game game = mem.getGameById(gameId);

            // ha legal�bb 2-en fogadt�k el, akkor foglalkozunk a k�r�ssel
            int acceptanceNumber = 0;
            for (Player player : game.getPlayers())
            {
                if (player.getPlayerStatus() == PlayerStatus.accepted)
                {
                    acceptanceNumber++;
                }
            }
            if (acceptanceNumber < 2)
            {
                throw new Exception("Not enough acceptance");
            }

            // ha nem fogadta el a felhasznalo a jatekra valo felkeres, akkor
            // elutasitjuk a jatek megkezdesekor
            for (Player player : game.getPlayers())
            {
                if (player.getPlayerStatus() == PlayerStatus.notAcceptedYet)
                {
                    player.setPlayerStatus(PlayerStatus.refused);
                    mem.commit(player);
                }
            }

            // kezdojatekos belallitasa
            List<Player> realPlayers = Helper.sortRealPlayer(game);
            game.setActualPlayer(realPlayers.get(0));

            game.setGameStatus(GameStatus.inProgress);
            mem.commit(game);

            // Tabla elkeszitese
            Helper.makeBoard(gameId);

            // Kezdomezo kivalasztasa
            StartPlace startPlace = null;
            for (Place place : game.getPlaces())
            {
                if (place instanceof StartPlace)
                {
                    startPlace = mem.getStartPlaceById(place.getId());
                    System.out.println("START PLACE: " + startPlace.getId());
                }
            }

            // jatekosok kezdomezore allitasa
            if (startPlace != null)
            {
                for (Player player : realPlayers)
                {
                    System.out.println("REAL PLAYER: " + player.getId());
                    Step step = new Step();
                    step.setFinishPlace(startPlace);
                    player.addStep(step);
                    mem.commit(step);
                    mem.commit(player);
                }
            }
            // ha nincsen kezdomezo, akkor baj van
            else
            {
                success = false;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            success = false;
        } finally
        {
            mem.closeDB();
        }
        try
        {
            responseJsonObject.put("success", success);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    /**
     * Create a game
     */
    @Path("/CreateGame")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGame(String json, @Context
    HttpServletRequest request)
    {
        List<User> validUsers = new ArrayList<User>();
        List<User> inviteUsers = new ArrayList<User>();
        System.out.println(json);

        JSONArray jsonTomb;
        String gameName;

        JSONArray invalidUserNamesJsonArray = new JSONArray();
        JSONArray notRegisteredUserEmailsJsonArray = new JSONArray();

        int errorCode = 0;

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        try
        {
            jsonTomb = new JSONArray(json);
            gameName = jsonTomb.getJSONObject(0).getString("gameName");
            JSONArray playersJsonArray = jsonTomb.getJSONObject(0).getJSONArray("players");

            for (int i = 0; i < playersJsonArray.length(); i++)
            {
                System.out.println(playersJsonArray.getJSONObject(i).getString("player"));
                String playerEmailOrName = playersJsonArray.getJSONObject(i).getString("player");

                // username jott, es a username az adatbazisban - ok
                if ((mem.isUserNameRegistered(playerEmailOrName)))
                {
                    validUsers.add(mem.getUserByName(playerEmailOrName));
                    System.out.println("DB-USerName: " + mem.getUserByName(playerEmailOrName).getName());
                }
                // valami jott, de az a usernamek kozott nincs az adatbazisban
                else
                {
                    // emailcim jott, benne van az emailek kozott
                    if (mem.isUserEmailRegistered(playerEmailOrName))
                    {
                        User user = mem.getUserByEmail(playerEmailOrName);
                        boolean hasActiveGame = false;
                        for (Player p : user.getGamePlayers())
                        {
                            if (p.getPlayerStatus() == PlayerStatus.accepted
                                    || p.getPlayerStatus() == PlayerStatus.notAcceptedYet)
                            {
                                hasActiveGame = true;
                            }
                        }

                        if (user.getUserType() == UserType.notRegistered && hasActiveGame)
                        {
                            notRegisteredUserEmailsJsonArray.put(playerEmailOrName);
                            System.out.println("NOT REG TOMBBE: " + playerEmailOrName);
                            errorCode = 1;
                        } else
                        {
                            validUsers.add(user);
                            System.out.println("DB-Email: " + mem.getUserByEmail(playerEmailOrName).getEmail());
                        }
                    }
                    // vagy email, ami nincsen az adatbazisban, vagy username,
                    // ami nincsen az adatbazisban
                    else
                    {
                        if (playerEmailOrName.contains("@"))
                        {
                            // uj email eltarolasa a tablaban nem regisztralt felhasznalokent
                            User user = new User();
                            user.setEmail(playerEmailOrName);
                            user.setName((playerEmailOrName.replace("@", "")));
                            user.setUserType(UserType.notRegistered);

                            try
                            {
                                mem.commit(user);
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Database error")
                                        .build();
                            }

                            // validUsers.add(mem.getUserByName(playerEmailOrName));
                            validUsers.add(mem.getUserByEmail(playerEmailOrName));
                            System.out.println("NO-DB-Email: " + mem.getUserByEmail(playerEmailOrName).getEmail());
                            inviteUsers.add(mem.getUserByEmail(playerEmailOrName));

                        }
                        // nem regisztralt username
                        else
                        {
                            System.out.println("INVALID T�MBBE: " + playerEmailOrName);
                            invalidUserNamesJsonArray.put(playerEmailOrName);
                            errorCode = 1;
                        }
                    }
                }
            }

            if (errorCode == 0)
            {
                // Get the owner of the game
                String loggedInUseremail = Helper.getLoggedInUserEmail(request);
                User ownerUser = mem.getUserByEmail(loggedInUseremail);

                EntityManager entityManager = mem.getEntityManager();
                entityManager.getTransaction().begin();
                try
                {
                    Game game = new Game();
                    game.setName(gameName);
                    game.setGameStatus(GameStatus.init);
                    entityManager.persist(game);

                    List<Player> gamePlayers = new ArrayList<Player>();
                    for (User user : validUsers)
                    {
                        // System.out.println("Actual hozzaadasa: " +
                        // user.getEmail());
                        Player player = new Player();
                        if (user == ownerUser)
                        {
                            player.setPlayerStatus(PlayerStatus.accepted);
                            game.setOwnerOfGame(ownerUser);
                        } else
                        {
                            player.setPlayerStatus(PlayerStatus.notAcceptedYet);
                        }

                        player.setMoney(Helper.initializationMoney);
                        player.setUser(user);
                        player.setGame(game);

                        entityManager.persist(player);
                        gamePlayers.add(player);

                        user.addGamePlayer(player);
                        entityManager.persist(user);
                    }

                    game.setPlayers(gamePlayers);
                    entityManager.persist(game);

                    entityManager.getTransaction().commit();

                    // nem regisztralt felhasznalok meghivasa
                    for (User user : inviteUsers)
                    {
                        System.out.println("Invitation kuldese: " + user.getEmail());
                        EmailManager.sendInvitationEmail(user.getEmail(), game);
                    }

                } catch (Exception e)
                {
                    entityManager.getTransaction().rollback();
                    errorCode = 1;
                }
            }
        } catch (JSONException e)
        {

            e.printStackTrace();
            return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
        }
        mem.closeDB();

        JSONObject responseJsonObject = new JSONObject();
        try
        {

            responseJsonObject.put("code", errorCode);
            responseJsonObject.put("invalidUserNames", invalidUserNamesJsonArray);
            responseJsonObject.put("notRegisteredUserEmails", notRegisteredUserEmailsJsonArray);

        } catch (JSONException e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
        }

        System.out.println("RESPONSE: " + responseJsonObject);

        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    /**
     * Gives the owned (not started, and not totally refused) games of the user
     */
    @Path("/GetMyGames")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMyGames(@Context
    HttpServletRequest request)
    {
        System.out.println("GetMyGames");

        String loggedInUseremail = Helper.getLoggedInUserEmail(request);

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        List<Game> games = mem.getOwnedInitGamesByEmail(loggedInUseremail);
        mem.closeDB();
        System.out.println(games);
        JSONArray ownedGamesJsonArray = new JSONArray();
        for (Game game : games)
        {
            System.out.println(game.getName());
            try
            {
                ownedGamesJsonArray.put(Helper.getGameDetailes(game));
            } catch (JSONException e)
            {
                e.printStackTrace();
                return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
            }
        }

        JSONObject responseJsonObject = new JSONObject();
        try
        {
            responseJsonObject.put("myGames", ownedGamesJsonArray);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
        }
        System.out.println(responseJsonObject);
        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

}
