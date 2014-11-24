package hu.bme.aut.monopoly.rest;

import hu.bme.aut.monopoly.model.BuildingPlace;
import hu.bme.aut.monopoly.model.Game;
import hu.bme.aut.monopoly.model.GameStatus;
import hu.bme.aut.monopoly.model.HouseBuying;
import hu.bme.aut.monopoly.model.MonopolyEntityManager;
import hu.bme.aut.monopoly.model.Place;
import hu.bme.aut.monopoly.model.Player;
import hu.bme.aut.monopoly.model.PlayerComparator;
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


@Path("/gameapi")
public class GameApi
{

    // @Path("/NewGame")
    // @POST
    // public void startNewGame(String json)
    // {
    //
    // List<Player> players = new ArrayList<Player>();
    // JSONArray jsonTomb;
    // String nameOfGame = null;
    //
    // try
    // {
    // jsonTomb = new JSONArray(json);
    // nameOfGame = jsonTomb.getJSONObject(0).getString("nameOfGame");
    //
    // MonopolyEntityManager mem = new MonopolyEntityManager();
    // mem.initDB();
    //
    // for (int i = 1; i < jsonTomb.length(); i++)
    // {
    // User user = mem.getUserByEmail(jsonTomb.getJSONObject(i).getString("email"));
    // Player player = new Player();
    // // TODO erteket kitalalni
    // player.setMoney(10000);
    // player.setPlayerStatus(PlayerStatus.accepted);
    // // TODO USERID
    // players.add(player);
    // mem.commit(player);
    // user.addGamePlayer(player);
    // mem.commit(user);
    //
    // }
    //
    // Game game = new Game();
    // game.setName(nameOfGame);
    // game.setPlayers(players);
    // game.setGameStatus(GameStatus.init);
    //
    // mem.commit(game);
    //
    // mem.closeDB();
    // } catch (JSONException e1)
    // {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // } catch (Exception e)
    // {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    //
    // }

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
                    if ((player.getPlayerStatus() == PlayerStatus.accepted)
                            && !(player.getUser().getEmail().equals(loggedInUseremail)))
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

    @Path("/OpenGame")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response openGame(String json, @Context
    HttpServletRequest request)
    {
        // TODO minek ez?
        String loggedInUseremail = Helper.getLoggedInUserEmail(request);
        int gameId;
        try
        {
            gameId = getGameIdFromJson(json);
        } catch (JSONException e1)
        {
            e1.printStackTrace();
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid JSON").build();
        }

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        Game game = mem.getGameById(gameId);

        JSONObject gameDetailesJsonObject = new JSONObject();
        try
        {
            System.out.println("GAME: " + game.getName());
            gameDetailesJsonObject.put("id", game.getId());
            gameDetailesJsonObject.put("gameStatus", game.getGameStatus());
            gameDetailesJsonObject.put("name", game.getName());
            gameDetailesJsonObject.put("ownerOfGame", game.getOwnerOfGame().getId());
            gameDetailesJsonObject.put("actualPlayer", game.getActualPlayer().getId());

            JSONArray playersJsonArray = new JSONArray();

            for (Player player : game.getPlayers())
            {
                if (player.getPlayerStatus() != PlayerStatus.refused)
                {

                    System.out.println("PLAYER: " + player.getId());
                    JSONObject aPlayerJsonObject = new JSONObject();
                    aPlayerJsonObject.put("playerId", player.getId());
                    aPlayerJsonObject.put("name", player.getUser().getName());
                    aPlayerJsonObject.put("status", player.getPlayerStatus());
                    boolean isActualPlayer = player.getGame().getActualPlayer().equals(player);
                    aPlayerJsonObject.put("isActualPlayer", isActualPlayer);

                    JSONObject actualPlayerJsonObject = new JSONObject();
                    if (isActualPlayer)
                    {
                        actualPlayerJsonObject.put("money", player.getMoney());
                        actualPlayerJsonObject.put("placeSequenceNumber",
                                player.getSteps().get(player.getSteps().size() - 1).getFinishPlace()
                                        .getPlaceSequenceNumber());

                        JSONArray ownedBuildingsJsonArray = new JSONArray();
                        for (BuildingPlace buildingPlace : player.getBuildings())
                        {
                            JSONObject aOwnedBuildingJsonObject = new JSONObject();

                            aOwnedBuildingJsonObject.put("buildingId", buildingPlace.getBuilding().getId());
                            aOwnedBuildingJsonObject.put("buildingName", buildingPlace.getBuilding().getName());
                            aOwnedBuildingJsonObject.put("maxHouseNumber", 5 - buildingPlace.getHouseNumber());
                            ownedBuildingsJsonArray.put(aOwnedBuildingJsonObject);
                        }

                        actualPlayerJsonObject.put("ownedBuildings", ownedBuildingsJsonArray);
                        aPlayerJsonObject.put("actualPlayer", actualPlayerJsonObject);
                    } else
                    {
                        aPlayerJsonObject.put("actualPlayer", "");
                    }

                    // !!!! place ahol all

                    // Step step = new Step();
                    // // mindenkit a starta rakunk
                    // step.setFinishPlace(game.getPlaces().get(0));
                    // mem.commit(step);
                    // player.addStep(step);
                    // mem.commit(player);

                    playersJsonArray.put(aPlayerJsonObject);
                }
            }
            gameDetailesJsonObject.put("players", playersJsonArray);

            JSONArray placesJsonArray = new JSONArray();
            for (Place place : game.getPlaces())
            {

                JSONObject aPlace = new JSONObject();
                aPlace.put("placeId", place.getId());
                aPlace.put("placeSequenceNumber", place.getPlaceSequenceNumber());
                aPlace.put("type", place.getClass().getSimpleName());

                // minden placehez egy lista, h melyik players all rajta ID-NAME
                JSONArray playersOnPlaceJsonArray = new JSONArray();
                for (Player player : game.getPlayers())
                {
                    if (player.getSteps().get(player.getSteps().size() - 1).getFinishPlace() == place)
                    {
                        JSONObject aPlayerOnPlaceJsonObject = new JSONObject();
                        aPlayerOnPlaceJsonObject.put("playerId", player.getId());
                        aPlayerOnPlaceJsonObject.put("userName", player.getUser().getName());
                        playersOnPlaceJsonArray.put(aPlayerOnPlaceJsonObject);
                    }
                }
                aPlace.put("playersOnPlace", playersOnPlaceJsonArray);
                placesJsonArray.put(aPlace);
            }

            gameDetailesJsonObject.put("places", placesJsonArray);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid JSON").build();
        }

        mem.closeDB();
        System.out.println(gameDetailesJsonObject);
        return Response.ok(gameDetailesJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    private int getGameIdFromJson(String json) throws JSONException
    {
        JSONArray jsonTomb;
        int gameId = 0;

        jsonTomb = new JSONArray(json);
        gameId = jsonTomb.getJSONObject(0).getInt("gameId");
        System.out.println("GAMEID: " + gameId);

        return gameId;
    }

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
                ownedGamesJsonArray.put(getGameDetailes(game));
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

    private JSONObject getGameDetailes(Game game) throws JSONException
    {
        System.out.println(game.getName());
        JSONObject aGameJsonObject = new JSONObject();

        aGameJsonObject.put("id", game.getId());
        aGameJsonObject.put("name", game.getName());
        JSONArray acceptedPlayersJsonArray = new JSONArray();
        JSONArray notAcceptedYetPlayersJsonArray = new JSONArray();
        JSONArray refusedPlayersJsonArray = new JSONArray();
        for (Player player : game.getPlayers())
        {
            JSONObject aPlayer = new JSONObject();
            aPlayer.put("playerId", player.getId());
            aPlayer.put("name", player.getUser().getName());
            aPlayer.put("status", player.getPlayerStatus());
            // aPlayer.put("placeId", player.getSteps().get(player.getSteps().size() -
            // 1).getFinishPlace().getId());

            if (player.getPlayerStatus() == PlayerStatus.accepted)
            {
                acceptedPlayersJsonArray.put(aPlayer);
            } else if (player.getPlayerStatus() == PlayerStatus.notAcceptedYet)
            {
                notAcceptedYetPlayersJsonArray.put(aPlayer);
            } else if (player.getPlayerStatus() == PlayerStatus.refused)
            {
                refusedPlayersJsonArray.put(aPlayer);
            }
        }

        aGameJsonObject.put("acceptedPlayers", acceptedPlayersJsonArray);
        aGameJsonObject.put("notAcceptedYetPlayers", notAcceptedYetPlayersJsonArray);
        aGameJsonObject.put("refusedPlayers", refusedPlayersJsonArray);

        return aGameJsonObject;
    }

    @Path("/GetBuilding")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuilding(String json, @Context
    HttpServletRequest request)
    {
        // TODO minek ez?
        String loggedInUseremail = Helper.getLoggedInUserEmail(request);
        JSONArray jsonTomb;
        int placeSequenceNumber = 0;

        try
        {
            jsonTomb = new JSONArray(json);
            placeSequenceNumber = jsonTomb.getJSONObject(0).getInt("placeSequenceNumber");
            System.out.println("BuildigPlaceSequenceNumber: " + placeSequenceNumber);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        JSONObject buildingPlaceJsonObject = new JSONObject();

        if (mem.getPlaceByPlaceSequenceNumber(placeSequenceNumber) instanceof BuildingPlace)
        {
            BuildingPlace buildingPlace = mem.getBuildingPlaceByPlaceSequenceNumber(placeSequenceNumber);
            System.out.println("BuildigPlace: " + buildingPlace.getId() + " - " + buildingPlace.getOwnerPlayer());
            if ((buildingPlace != null) && (buildingPlace.getOwnerPlayer() != null))
            {
                try
                {
                    buildingPlaceJsonObject.put("ownerUserName", buildingPlace.getOwnerPlayer().getUser().getName());
                    getBuildingPlaceDetailes(buildingPlace, buildingPlaceJsonObject);
                } catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        mem.closeDB();
        System.out.println(buildingPlaceJsonObject);
        return Response.ok(buildingPlaceJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    private JSONObject getBuildingPlaceDetailes(BuildingPlace buildingPlace, JSONObject buildingPlaceJsonObject)
            throws JSONException
    {
        buildingPlaceJsonObject.put("houseNumber", buildingPlace.getHouseNumber());
        buildingPlaceJsonObject.put("name", buildingPlace.getBuilding().getName());
        buildingPlaceJsonObject.put("price", buildingPlace.getBuilding().getPrice());
        buildingPlaceJsonObject.put("housePrice", buildingPlace.getBuilding().getHousePrice());
        buildingPlaceJsonObject.put("baseNightPayment", buildingPlace.getBuilding().getBaseNightPayment());
        buildingPlaceJsonObject.put("perHousePayment", buildingPlace.getBuilding().getPerHousePayment());
        buildingPlaceJsonObject.put("placeSequenceNumber", buildingPlace.getPlaceSequenceNumber());
        buildingPlaceJsonObject.put("placeId", buildingPlace.getId());

        return buildingPlaceJsonObject;
    }

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
        try
        {
            jsonTomb = new JSONArray(json);
            gameId = jsonTomb.getJSONObject(0).getInt("gameId");

            String loggedInUserEmail = Helper.getLoggedInUserEmail(request);

            MonopolyEntityManager mem = new MonopolyEntityManager();
            mem.initDB();
            Game game = mem.getGameById(gameId);

            // ha nem fogadta el a felhasznalo a jatekra valo felkeres, akkor elutasitjuk a jatek megkezdesekor
            for (Player player : game.getPlayers())
            {
                if (player.getPlayerStatus() == PlayerStatus.notAcceptedYet)
                {
                    player.setPlayerStatus(PlayerStatus.refused);
                    mem.commit(player);
                }
            }

            // kezdojatekos belallitasa
            List<Player> realPlayers = sortRelaPlayer(game);
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
            mem.closeDB();

        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            success = false;

        }
        try
        {
            responseJsonObject.put("success", success);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    private List<Player> sortRelaPlayer(Game game)
    {
        List<Player> realPlayers = new ArrayList<Player>();
        for (Player player : game.getPlayers())
        {
            if (player.getPlayerStatus() == PlayerStatus.accepted)
            {
                realPlayers.add(player);
            }
        }

        java.util.Collections.sort(realPlayers, new PlayerComparator());
        return realPlayers;
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
                    notAcceptedYetGamesJsonArray.put(getGameDetailes(player.getGame()));
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
        return modifyPlayerStatus(json, request, PlayerStatus.accepted);
    }

    @Path("/RefuseInvitation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response refuseInvitation(String json, @Context
    HttpServletRequest request)
    {
        return modifyPlayerStatus(json, request, PlayerStatus.refused);
    }

    @Path("/CreateGame")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGame(String json, @Context
    HttpServletRequest request)
    {
        boolean success = true;
        List<User> validUsers = new ArrayList<User>();
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
                    // Player player = new Player();
                    // player.setUser(mem.getUserByName(playerEmailOrName));
                    // player.setMoney(1000);
                    // mem.commit(player);
                    // gamePlayers.add(player);
                }
                // valami jott, de az a usernamek kozott nincs az adatbazisban
                else
                {
                    // emailcim jott, benne van az emailek kozott
                    if (mem.isUserEmailRegistered(playerEmailOrName))
                    {
                        User user = mem.getUserByEmail(playerEmailOrName);

                        if (user.getUserType() == UserType.notRegistered)/* && (/*van-e aktiv jeteka)) */
                        {
                            notRegisteredUserEmailsJsonArray.put(playerEmailOrName);
                            errorCode = 1;
                        } else
                        {
                            validUsers.add(user);
                        }
                    }
                    // vagy email, ami nincsen az adatbazisban, vagy username, ami nincsen az adatbazisban
                    else
                    {
                        if (playerEmailOrName.contains("@"))
                        {
                            // ujemail, mi legyen?-->vegyuk fel a user tablaba mint nem regisztralt felhasznalo
                            // uj user a listahoz adni
                            User user = new User();
                            user.setEmail(playerEmailOrName);
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

                            validUsers.add(mem.getUserByName(playerEmailOrName));
                        }
                        // nem regisztralt username
                        else
                        {
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
                mem.initDB();
                User ownerUser = mem.getUserByEmail(loggedInUseremail);
                mem.closeDB();

                EntityManager entityManager = mem.getEntityManager();
                entityManager.getTransaction().begin();
                try
                {
                    Game game = new Game();
                    game.setName(gameName);
                    entityManager.persist(game);

                    List<Player> gamePlayers = new ArrayList<Player>();
                    for (User user : validUsers)
                    {
                        Player player = new Player();
                        if (user == ownerUser)
                        {
                            player.setPlayerStatus(PlayerStatus.accepted);
                            game.setOwnerOfGame(ownerUser);
                        } else
                        {
                            player.setPlayerStatus(PlayerStatus.notAcceptedYet);
                        }

                        player.setMoney(Helper.throughMoney);
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

        int wonGamesNum = getNumberOfGameInAStatus(user, PlayerStatus.win);
        int invitationsNum = getNumberOfGameInAStatus(user, PlayerStatus.notAcceptedYet);

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

    @Path("/GetPlaceData")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlaceData(String json, @Context
    HttpServletRequest request)
    {
        System.out.println(json);
        JSONArray jsonTomb;
        int placeSequenceNumber = 0;
        try
        {
            jsonTomb = new JSONArray(json);
            placeSequenceNumber = jsonTomb.getJSONObject(0).getInt("placeSequenceNumber");
            System.out.println("PlacePlaceSequenceNumber: " + placeSequenceNumber);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid JSON").build();
        }

        JSONObject responseJsonObject = new JSONObject();
        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        JSONObject placeJsonObject = new JSONObject();
        boolean isBuilding = false;

        Place place = mem.getPlaceByPlaceSequenceNumber(placeSequenceNumber);
        try
        {
            if (place instanceof StartPlace)
            {
                StartPlace startPlace = mem.getStartPlaceByPlaceSequenceNumber(placeSequenceNumber);

                placeJsonObject.put("placeId", startPlace.getId());
                placeJsonObject.put("placeSequenceNumber", startPlace.getPlaceSequenceNumber());
                placeJsonObject.put("throughMoney", startPlace.getThroughMoney());

            } else if (place instanceof BuildingPlace)
            {
                BuildingPlace buildingPlace = mem.getBuildingPlaceByPlaceSequenceNumber(placeSequenceNumber);

                placeJsonObject.put("gameId", buildingPlace.getGame().getId());
                placeJsonObject.put("placeId", buildingPlace.getId());
                placeJsonObject.put("placeSequenceNumber", buildingPlace.getPlaceSequenceNumber());
                placeJsonObject.put("houseNumber", buildingPlace.getHouseNumber());
                if (buildingPlace.getOwnerPlayer() != null)
                {
                    placeJsonObject.put("ownerPlayerId", buildingPlace.getOwnerPlayer().getId());
                } else
                {
                    placeJsonObject.put("ownerPlayerId", "");
                }
                if (buildingPlace.getBuilding() != null)
                {
                    placeJsonObject.put("buildingId", buildingPlace.getBuilding().getId());
                    placeJsonObject.put("buildingName", buildingPlace.getBuilding().getName());
                    placeJsonObject.put("buildingPrice", buildingPlace.getBuilding().getPrice());
                    placeJsonObject.put("buildingHousePrice", buildingPlace.getBuilding().getHousePrice());
                    placeJsonObject
                            .put("buildingBaseNightPayment", buildingPlace.getBuilding().getBaseNightPayment());
                    placeJsonObject.put("buildingPerHousePayment", buildingPlace.getBuilding().getPerHousePayment());

                } else
                {
                    placeJsonObject.put("buildingId", "");
                    placeJsonObject.put("buildingName", "");
                    placeJsonObject.put("buildingPrice", "");
                    placeJsonObject.put("buildingHousePrice", "");
                    placeJsonObject.put("buildingBaseNightPayment", "");
                    placeJsonObject.put("buildingPerHousePaymentss", "");
                }

                isBuilding = true;

            } else
            {
                placeJsonObject.put("gameId", place.getGame().getId());
                placeJsonObject.put("placeSequenceNumber", place.getPlaceSequenceNumber());
                placeJsonObject.put("placeId", place.getId());

            }
            System.out.println(placeJsonObject);
            mem.closeDB();

            responseJsonObject.put("buildingData", placeJsonObject);
            responseJsonObject.put("isBuilding", isBuilding);

        } catch (JSONException e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
        }

        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    @Path("/GetPlayerData")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlayerData(String json, @Context
    HttpServletRequest request)
    {
        System.out.println("JSON: " + json);
        JSONArray jsonTomb;
        int playerId = 0;
        try
        {
            jsonTomb = new JSONArray(json);
            playerId = jsonTomb.getJSONObject(0).getInt("playerId");
            System.out.println("PlayerID: " + playerId);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid JSON").build();
        }

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        JSONObject playerJsonObject = new JSONObject();

        Player player = mem.getPlayerById(playerId);
        System.out.println(player.getId());
        try
        {
            playerJsonObject.put("gameId", player.getGame().getId());
            playerJsonObject.put("gameName", player.getGame().getName());
            playerJsonObject.put("playerId", player.getId());
            playerJsonObject.put("playerSequenceNumber", player.getGame().getPlayers().indexOf(player));
            playerJsonObject.put("playerStatus", player.getPlayerStatus());
            playerJsonObject.put("money", player.getMoney());
            playerJsonObject.put("userId", player.getUser().getId());
            playerJsonObject.put("userName", player.getUser().getName());

            JSONArray ownedBuildingsJsonArray = new JSONArray();
            for (BuildingPlace buildingPlace : player.getBuildings())
            {
                JSONObject buildingPlaceJsonObject = new JSONObject();
                System.out.println(buildingPlace);
                ownedBuildingsJsonArray.put(getBuildingPlaceDetailes(buildingPlace, buildingPlaceJsonObject));
            }

            playerJsonObject.put("ownedBuildingPlaces", ownedBuildingsJsonArray);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
        }

        System.out.println(playerJsonObject);
        mem.closeDB();

        return Response.ok(playerJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    @Path("/MakeStep")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response makeStep(String json, @Context
    HttpServletRequest request)
    {
        int errorCode = 0;

        String loggedInUseremail = Helper.getLoggedInUserEmail(request);

        JSONArray jsonTomb;
        int playerId = 0;
        int placeSequenceNumber = 0;
        int roll = 0;
        boolean isBuildingBought = false;
        boolean isPayed = false;
        boolean isSold = false;
        JSONArray soldBuildingsIds = new JSONArray();
        JSONArray boughtHouseNumberForBuildings = new JSONArray();

        try
        {
            jsonTomb = new JSONArray(json);
            playerId = jsonTomb.getJSONObject(0).getInt("playerId");
            placeSequenceNumber = jsonTomb.getJSONObject(0).getInt("placeSequenceNumber");
            roll = jsonTomb.getJSONObject(0).getInt("roll");
            isBuildingBought = jsonTomb.getJSONObject(0).getBoolean("isBuildingBought");
            isPayed = jsonTomb.getJSONObject(0).getBoolean("isPayed");
            isSold = jsonTomb.getJSONObject(0).getBoolean("isSold");
            soldBuildingsIds = jsonTomb.getJSONObject(0).getJSONArray("soldBuildingsIds");
            boughtHouseNumberForBuildings = jsonTomb.getJSONObject(0).getJSONArray("boughtHouseNumberForBuildings");

            System.out.println("BuildiSequenceNumber: " + placeSequenceNumber);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid JSON").build();
        }

        // playerId = 1;
        // placeSequenceNumber = 3;
        // roll = 2;

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        Player player = mem.getPlayerById(playerId);

        if (roll < 1
                || roll > 6
                || ((player.getSteps().get(player.getSteps().size() - 1).getFinishPlace().getPlaceSequenceNumber() + roll) % 16 != placeSequenceNumber))
        {
            errorCode = 2;
            System.out.println("CSALAS");
        } else
        {
            Step step = new Step();
            Player ownerOfBuildingPlace = null;
            List<BuildingPlace> soldBuildingPlaces = new ArrayList<BuildingPlace>();
            List<HouseBuying> houseBuyings = new ArrayList<HouseBuying>();

            // leptetes
            step.setFinishPlace(mem.getPlaceByPlaceSequenceNumber(placeSequenceNumber));
            player.addStep(step);
            System.out.println("UJ MEZO: " + step.getFinishPlace());
            try
            {
                // mem.commit(step);
                // StartPlacere leptunk
                if (mem.getPlaceByPlaceSequenceNumber(placeSequenceNumber) instanceof StartPlace)
                {
                    System.out.println("START MEZO");
                    player.setMoney(player.getMoney() + Helper.throughMoney);
                }
                // buildingPlacere leptunk
                else if (mem.getPlaceByPlaceSequenceNumber(placeSequenceNumber) instanceof BuildingPlace)
                {
                    System.out.println("EPITESI MEZO ");
                    BuildingPlace buildingPlace = mem.getPlaceByPlaceSequenceNumber(placeSequenceNumber);

                    // megvásárolta az adott telket
                    if (isBuildingBought)
                    {
                        player.setMoney(player.getMoney() - buildingPlace.getBuilding().getPrice());
                        step.setBuyedBuilding(buildingPlace);
                        // mem.commit(step);
                        player.addBuilding(buildingPlace);
                        // mem.commit(player);

                    }

                    // fizetett a telek gazdájának megfelelõ összeget
                    else if (isPayed)
                    {

                        int amount;
                        amount = buildingPlace.getBuilding().getBaseNightPayment() + buildingPlace.getHouseNumber()
                                * buildingPlace.getBuilding().getPerHousePayment();

                        ownerOfBuildingPlace = buildingPlace.getOwnerPlayer();
                        ownerOfBuildingPlace.setMoney(ownerOfBuildingPlace.getMoney() + amount);
                        // mem.commit(ownerOfBuildingPlace);

                        player.setMoney(player.getMoney() - amount);
                        // mem.commit(player);
                    }

                    // eladott épületekkel szükséges mûveletek elvégzése (házak levétele a telekrõl, a telek
                    // tulajdonjogának törlése...)
                    if (isSold)
                    {
                        int soldBuildingPlaceSequenceNumber;
                        for (int i = 0; i < soldBuildingsIds.length(); i++)

                        {
                            soldBuildingPlaceSequenceNumber = soldBuildingsIds.getJSONObject(i).getInt(
                                    "placeSequenceNumber");
                            BuildingPlace soldBuildingPlace = mem
                                    .getBuildingPlaceByPlaceSequenceNumber(soldBuildingPlaceSequenceNumber);

                            player.setMoney(player.getMoney()
                                    + ((int) (soldBuildingPlace.getBuilding().getPrice() / 2)));
                            player.removeBuilding(soldBuildingPlace);
                            // mem.commit(player);

                            soldBuildingPlace.setHouseNumber(0);
                            soldBuildingPlace.setOwnerPlayer(null);
                            // mem.commit(soldBuildingPlace);
                            soldBuildingPlaces.add(soldBuildingPlace);
                            step.addSoldBuilding(soldBuildingPlace);
                        }

                        // mem.commit(step);

                    }
                }

                // Külön kell végig menni a telkeire vett házakon
                int buildingId;
                int number;
                for (int i = 0; i < boughtHouseNumberForBuildings.length(); i++)
                {
                    buildingId = boughtHouseNumberForBuildings.getJSONObject(i).getInt("buildingId");
                    number = boughtHouseNumberForBuildings.getJSONObject(i).getInt("number");

                    // LEELLENORIZNI H VAN_E ILYEN ID BUILDING
                    if (mem.getBuildingPlaceById(buildingId) != null)
                    {
                        BuildingPlace boughtBuildingPlace = mem.getBuildingPlaceById(buildingId);

                        HouseBuying houseBuying = new HouseBuying();
                        houseBuying.setBuyedHouseNumber(number);
                        houseBuying.setForBuilding(boughtBuildingPlace);
                        // mem.commit(houseBuying);
                        houseBuyings.add(houseBuying);

                        // Ez kimaradt, növelni kell a házak számát az adott telken
                        int newSumHouseNumber = boughtBuildingPlace.getHouseNumber() + number;
                        if (newSumHouseNumber > 5)
                        {
                            // csalt, nem érvényes a lépés
                            // ilyenkor hogyan vonod vissza az eddig felkommitolt dolgokat az adatbázisból?
                            errorCode = 2;
                            throw new IllegalArgumentException("Too much house bought for: "
                                    + boughtBuildingPlace.getId());
                        }
                        boughtBuildingPlace.setHouseNumber(newSumHouseNumber);

                        step.setBuyedBuilding(boughtBuildingPlace);
                        // mem.commit(step);

                        player.setMoney(player.getMoney() - boughtBuildingPlace.getBuilding().getPrice()
                                * boughtBuildingPlace.getHouseNumber());
                        // mem.commit(player);
                    } else
                    {
                        // nincsen ilyen id a buildingPlace között
                        errorCode = 2;
                        throw new IllegalArgumentException("No BuildingPlace with id : " + buildingId);
                    }

                }
                step.setHouseBuyings(houseBuyings);

                // Ide kellene majd még, hogy megvizsgáljuk a pénzét, ha negatív, akkor menjen csõdbe (telkek elvétele
                // és ürítése, állapot átállítása...)

                if (player.getMoney() < 0)
                {
                    for (BuildingPlace buildingPlace : player.getBuildings())
                    {
                        buildingPlace.setHouseNumber(0);
                        buildingPlace.setOwnerPlayer(null);
                        player.removeBuilding(buildingPlace);
                    }
                    player.setPlayerStatus(PlayerStatus.lost);
                }

                mem.commit(step);

                // Ha kell gyoztesnek allitjuk
                int numberOfAcceptedPlayer = 0;
                for (Player aPlayer : player.getGame().getPlayers())
                {
                    if (aPlayer.getPlayerStatus() == PlayerStatus.accepted)
                    {
                        numberOfAcceptedPlayer++;
                    }
                }

                if (numberOfAcceptedPlayer == 1)
                {
                    player.setPlayerStatus(PlayerStatus.win);
                    player.getGame().setGameStatus(GameStatus.finished);
                }

            } catch (Exception e)
            {
                e.printStackTrace();
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Database error").build();
            }

            // Szerintem csak itt kéne kommitolni mindent, azt nem lehet? mondjuk a houseBuyings-os listán
            // végigmenve mindegyiket egyesével meg a player-t is
            EntityManager entityManager = mem.getEntityManager();
            entityManager.getTransaction().begin();
            try
            {
                if (isPayed)
                {
                    entityManager.persist(ownerOfBuildingPlace);
                }
                if (isSold)
                {
                    for (BuildingPlace aSoldBuildingPlace : soldBuildingPlaces)
                    {
                        mem.commit(aSoldBuildingPlace);
                    }
                }

                for (HouseBuying houseBuying : houseBuyings)
                {
                    entityManager.persist(houseBuying);
                }

                entityManager.persist(player);
                entityManager.persist(step);

                // kovetkezo jatekos beallitasa
                List<Player> realPlayers = sortRelaPlayer(player.getGame());
                int nextActualPlayerIndex = (realPlayers.indexOf(player) + 1) % realPlayers.size();
                Game game = player.getGame();
                game.setActualPlayer(realPlayers.get(nextActualPlayerIndex));
                entityManager.persist(game);

                entityManager.getTransaction().commit();
            } catch (Exception e)
            {
                entityManager.getTransaction().rollback();
                e.printStackTrace();
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Database error").build();
            }

        }

        mem.closeDB();

        JSONObject responseJsonObject = new JSONObject();

        try
        {
            responseJsonObject.put("errorCode", errorCode);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
        }
        System.out.println(responseJsonObject);
        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    private int getNumberOfGameInAStatus(User user, PlayerStatus ps)
    {
        int gamesNum = 0;
        for (Player player : user.getGamePlayers())
        {
            if (player.getPlayerStatus() == ps)
            {
                gamesNum++;
            }
        }
        return gamesNum;
    }

    private Response modifyPlayerStatus(String json, HttpServletRequest request, PlayerStatus playerStatus)
    {
        JSONObject responseJsonObject = new JSONObject();
        System.out.println("GetInvitations");

        String loggedInUseremail = Helper.getLoggedInUserEmail(request);

        int gameId;
        try
        {
            gameId = getGameIdFromJson(json);
        } catch (JSONException e2)
        {
            e2.printStackTrace();
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid JSON").build();
        }

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        User user = mem.getUserByEmail(loggedInUseremail);
        Game game = mem.getGameById(gameId);

        List<Player> gamePlayers = user.getGamePlayers();
        JSONArray notAcceptedYetGamesJsonArray = new JSONArray();

        try
        {
            for (Player player : gamePlayers)
            {
                System.out.println("PLAYERID: " + player.getId() + " - " + player.getPlayerStatus());
                if ((player.getGame() == game) && (player.getPlayerStatus() != playerStatus)
                        && (player.getPlayerStatus() != PlayerStatus.refused))
                {

                    try
                    {
                        player.setPlayerStatus(playerStatus);
                        mem.commit(player);
                        responseJsonObject.put("success", true);

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        responseJsonObject.put("success", false);
                    }

                    System.out.println(player.getGame().getName() + " - " + player.getId());
                } else
                {
                    responseJsonObject.put("success", false);
                }

            }
        } catch (JSONException e1)
        {
            e1.printStackTrace();
            return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
        }
        System.out.println(notAcceptedYetGamesJsonArray);

        int numberOfAcceptedPlayer = 0;
        for (Player player : gamePlayers)
        {
            if (player.getPlayerStatus() == PlayerStatus.accepted)
            {
                numberOfAcceptedPlayer++;
            }
        }
        if (numberOfAcceptedPlayer == gamePlayers.size())
        {
            game.setGameStatus(GameStatus.inProgress);
            try
            {
                mem.commit(game);
            } catch (Exception e)
            {
                e.printStackTrace();
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Database error").build();

            }
        }
        mem.closeDB();
        System.out.println(responseJsonObject);

        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }
}
