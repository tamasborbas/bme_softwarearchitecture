package hu.bme.aut.monopoly.rest;

import hu.bme.aut.monopoly.model.BuildingPlace;
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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.JResponse;


@Path("/gameapi")
public class GameApi
{

    @Path("/NewGame")
    @POST
    public void startNewGame(String json)
    {

        List<Player> players = new ArrayList<Player>();
        JSONArray jsonTomb;
        String nameOfGame = null;

        try
        {
            jsonTomb = new JSONArray(json);
            nameOfGame = jsonTomb.getJSONObject(0).getString("nameOfGame");

            MonopolyEntityManager mem = new MonopolyEntityManager();
            mem.initDB();

            for (int i = 1; i < jsonTomb.length(); i++)
            {
                User user = mem.getUserByEmail(jsonTomb.getJSONObject(i).getString("email"));
                Player player = new Player();
                // TODO erteket kitalalni
                player.setMoney(10000);
                player.setPlayerStatus(PlayerStatus.accepted);
                // TODO USERID
                players.add(player);
                mem.commit(player);
                user.addGamePlayer(player);
                mem.commit(user);

            }

            Game game = new Game();
            game.setName(nameOfGame);
            game.setPlayers(players);
            game.setGameStatus(GameStatus.init);

            mem.commit(game);

            mem.closeDB();
        } catch (JSONException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Path("/GetActiveGames")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JResponse<JSONArray> getActiveGames(@Context
    HttpServletRequest request)
    {
        System.out.println("GetActiveGames");

        String loggedInUseremail = Helper.getLoggedInUserEmail(request);

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();

        List<Game> activeGamesByEmail = mem.getActiveGamesByEmail(loggedInUseremail);
        System.out.println("***********************");
        System.out.println(activeGamesByEmail.toString());
        System.out.println("***********************");

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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            activeGamesJsonArray.put(aActiveGame);
            System.out.println("ELKULDOTT: " + activeGamesJsonArray);
        }

        mem.closeDB();

        return JResponse.ok(activeGamesJsonArray).build();
    }

    @Path("/OpenGame")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public JResponse<JSONObject> openGame(String json, @Context
    HttpServletRequest request)
    {
        // TODO minek ez?
        String loggedInUseremail = Helper.getLoggedInUserEmail(request);
        int gameId = getGameIdFromJson(json);

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
                System.out.println("PLAYER: " + player.getId());
                JSONObject aPlayer = new JSONObject();
                aPlayer.put("playerId", player.getId());
                aPlayer.put("name", player.getUser().getName());
                aPlayer.put("status", player.getPlayerStatus());

                if (player.getPlayerStatus() != PlayerStatus.refused)
                {

                    try
                    {
                        Step step = new Step();
                        // mindenkit a starta rakunk
                        step.setFinishPlace(game.getPlaces().get(0));
                        mem.commit(step);
                        player.addStep(step);
                        mem.commit(player);
                        aPlayer.put("placeId", player.getSteps().get(player.getSteps().size() - 1).getFinishPlace()
                                .getId());
                    } catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

                playersJsonArray.put(aPlayer);
            }
            gameDetailesJsonObject.put("players", playersJsonArray);

            JSONArray placesJsonArray = new JSONArray();
            for (Place place : game.getPlaces())
            {

                JSONObject aPlace = new JSONObject();
                aPlace.put("placeId", place.getId());

                aPlace.put("type", place.getClass());
                placesJsonArray.put(aPlace);
            }

            gameDetailesJsonObject.put("places", placesJsonArray);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mem.closeDB();
        System.out.println(gameDetailesJsonObject);
        return JResponse.ok(gameDetailesJsonObject).build();
    }

    private int getGameIdFromJson(String json)
    {
        JSONArray jsonTomb;
        int gameId = 0;

        try
        {
            jsonTomb = new JSONArray(json);
            gameId = jsonTomb.getJSONObject(0).getInt("gameId");
            System.out.println("GAMEID: " + gameId);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return gameId;
    }

    @Path("/GetMyGames")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JResponse<JSONArray> getMyGames(@Context
    HttpServletRequest request)
    {
        System.out.println("GetMyGames");

        String loggedInUseremail = Helper.getLoggedInUserEmail(request);

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        List<Game> games = mem.getOwnedInitGamesByEmail(loggedInUseremail);
        mem.closeDB();

        JSONArray ownedGamesJsonArray = new JSONArray();
        for (Game game : games)
        {
            System.out.println(game.getName());
            try
            {
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
                    aPlayer.put("placeId", player.getSteps().get(player.getSteps().size() - 1).getFinishPlace()
                            .getId());

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
                ownedGamesJsonArray.put(aGameJsonObject);

            } catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // playersJsonArray.put(aPlayer);
        }

        System.out.println(ownedGamesJsonArray);
        return JResponse.ok(ownedGamesJsonArray).build();
    }

    @Path("/GetBuilding")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public JResponse<JSONObject> getBuilding(String json, @Context
    HttpServletRequest request)
    {
        // TODO minek ez?
        String loggedInUseremail = Helper.getLoggedInUserEmail(request);
        JSONArray jsonTomb;
        int buildigPlaceId = 0;

        try
        {
            jsonTomb = new JSONArray(json);
            buildigPlaceId = jsonTomb.getJSONObject(0).getInt("buildingPlaceId");
            System.out.println("BuildigPlaceID: " + buildigPlaceId);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        BuildingPlace buildingPlace = mem.getBuildingPlaceById(buildigPlaceId);
        mem.closeDB();

        JSONObject buildingPlaceJsonObject = new JSONObject();
        try
        {
            buildingPlaceJsonObject.put("ownerUserName", buildingPlace.getOwnerPlayer().getUser().getName());
            buildingPlaceJsonObject.put("houseNumber", buildingPlace.getHouseNumber());
            buildingPlaceJsonObject.put("name", buildingPlace.getBuilding().getName());
            buildingPlaceJsonObject.put("price", buildingPlace.getBuilding().getPrice());
            buildingPlaceJsonObject.put("housePrice", buildingPlace.getBuilding().getHousePrice());
            buildingPlaceJsonObject.put("baseNightPayment", buildingPlace.getBuilding().getBaseNightPayment());
            buildingPlaceJsonObject.put("perHousePayment", buildingPlace.getBuilding().getPerHousePayment());
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(buildingPlaceJsonObject);
        return JResponse.ok(buildingPlaceJsonObject).build();
    }

    @Path("/StartGame")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public JResponse<JSONObject> startGame(String json, @Context
    HttpServletRequest request)
    {
        JSONArray jsonTomb;
        int gameId;

        JSONObject responseJsonObject = new JSONObject();
        try
        {
            jsonTomb = new JSONArray(json);
            gameId = jsonTomb.getJSONObject(0).getInt("gameId");

            String loggedInUserEmail = Helper.getLoggedInUserEmail(request);

            try
            {
                MonopolyEntityManager mem = new MonopolyEntityManager();
                mem.initDB();
                Game game = mem.getGameById(gameId);
                game.setGameStatus(GameStatus.inProgress);
                mem.commit(game);
                mem.closeDB();
                responseJsonObject.put("success", true);
                return JResponse.ok(responseJsonObject).build();
            } catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (JSONException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();

        }
        try
        {
            responseJsonObject.put("success", false);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return JResponse.ok(responseJsonObject).build();
    }

    @Path("/GetInvitations")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public JResponse<JSONArray> getInvitations(@Context
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
                notAcceptedYetGamesJsonArray.put(player.getGame());
                System.out.println(player.getGame().getName());
            }
        }
        System.out.println(notAcceptedYetGamesJsonArray);
        return JResponse.ok(notAcceptedYetGamesJsonArray).build();
    }

    @Path("/AcceptInvitation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public JResponse<JSONObject> acceptInvitation(String json, @Context
    HttpServletRequest request)
    {
        return modifyPlayerStatus(json, request, PlayerStatus.accepted);
    }

    @Path("/RefuseInvitation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public JResponse<JSONObject> refuseInvitation(String json, @Context
    HttpServletRequest request)
    {
        return modifyPlayerStatus(json, request, PlayerStatus.refused);
    }

    @Path("/CreateGame")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public JResponse<JSONObject> createGame(String json, @Context
    HttpServletRequest request)
    {

        System.out.println(json);

        JSONArray jsonTomb;
        String gameName;
        List<Player> gamePlayers = new ArrayList<Player>();
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
                playersJsonArray.getString(i);
                String playerEmailOrName = playersJsonArray.getString(i);

                // username jott, es a username az adatbazisban - ok
                if ((mem.isUserNameRegistered(playerEmailOrName)))
                {
                    Player player = new Player();
                    player.setUser(mem.getUserByName(playerEmailOrName));
                    player.setMoney(1000);
                    mem.commit(player);
                    gamePlayers.add(player);
                }
                // valami jott, de az a usernamek kozott nincs az adatbazisban
                else
                {
                    // emailcim jott, benne van az emailek kozott
                    if (mem.isUserEmailRegistered(playerEmailOrName))
                    {
                        Player player = new Player();
                        User user = mem.getUserByEmail(playerEmailOrName);
                        player.setUser(user);
                        player.setMoney(1000);
                        mem.commit(player);
                        gamePlayers.add(player);
                        if (user.getUserType() == UserType.notRegistered)
                        {
                            notRegisteredUserEmailsJsonArray.put(playerEmailOrName);
                        }
                    }
                    // vagy email, ami nincsen az adatbazisban, vagy username, ami nincsen az adatbazisban
                    else
                    {
                        if (playerEmailOrName.contains("@"))
                        {
                            // ujemail, mi legyen?
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
            Game game = new Game();
            game.setName(gameName);
            game.setPlayers(gamePlayers);
            mem.commit(game);

        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mem.closeDB();

        JSONObject responseJsonObject = new JSONObject();
        try
        {

            responseJsonObject.put("code", errorCode);
            responseJsonObject.put("invalidUserNames", invalidUserNamesJsonArray);
            responseJsonObject.put("notRegisteredUserEmailsJsonArray", notRegisteredUserEmailsJsonArray);

        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("RESPONSE: " + responseJsonObject);

        return JResponse.ok(responseJsonObject).build();
    }

    @Path("/GetProfil")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public JResponse<JSONObject> getProfil(@Context
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
            responseJsonObject.put("passwordHash", user.getPasswordHash());
            responseJsonObject.put("participatedGamesNum", user.getGamePlayers().size());
            responseJsonObject.put("ownGamesNum", ownedGames.size());
            responseJsonObject.put("wonGamesNum", wonGamesNum);
            responseJsonObject.put("activeGamesNum", activeGamesNum);
            responseJsonObject.put("invitationsNum", invitationsNum);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mem.closeDB();

        System.out.println(responseJsonObject);
        return JResponse.ok(responseJsonObject).build();
    }

    @Path("/GetPlaceData")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public JResponse<JSONObject> getPlaceData(String json, @Context
    HttpServletRequest request)
    {
        System.out.println(json);
        JSONArray jsonTomb;
        int placeId = 0;
        try
        {
            jsonTomb = new JSONArray(json);
            placeId = jsonTomb.getJSONObject(0).getInt("placeId");
            System.out.println("PlaceID: " + placeId);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JSONObject responseJsonObject = new JSONObject();
        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        JSONObject placeJsonObject = new JSONObject();

        Place place = mem.getPlaceById(placeId);
        if (place.getClass() == StartPlace.class)
        {
            StartPlace startPlace = mem.getStartPlaceById(placeId);
            try
            {
                placeJsonObject.put("gameId", startPlace.getGame().getId());
                placeJsonObject.put("placeId", startPlace.getId());
                placeJsonObject.put("throughMoney", startPlace.getThroughMoney());

            } catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (place.getClass() == BuildingPlace.class)
        {
            BuildingPlace buildingPlace = mem.getBuildingPlaceById(placeId);
            try
            {
                placeJsonObject.put("gameId", buildingPlace.getGame().getId());
                placeJsonObject.put("placeId", buildingPlace.getId());
                placeJsonObject.put("houseNumber", buildingPlace.getHouseNumber());
                placeJsonObject.put("ownerPlayerId", buildingPlace.getOwnerPlayer().getId());
                placeJsonObject.put("buildingId", buildingPlace.getBuilding().getId());
                placeJsonObject.put("buildingName", buildingPlace.getBuilding().getName());
                placeJsonObject.put("buildingPrice", buildingPlace.getBuilding().getPrice());
                placeJsonObject.put("buildingHousePrice", buildingPlace.getBuilding().getHousePrice());
                placeJsonObject.put("buildingBaseNightPayment", buildingPlace.getBuilding().getBaseNightPayment());
                placeJsonObject.put("buildingPerHousePayment", buildingPlace.getBuilding().getPerHousePayment());

            } catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else
        {
            try
            {
                placeJsonObject.put("gameId", place.getGame().getId());
                placeJsonObject.put("placeId", place.getId());
            } catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println(placeJsonObject);
        mem.closeDB();

        return JResponse.ok(responseJsonObject).build();
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

    private JResponse<JSONObject> modifyPlayerStatus(String json, HttpServletRequest request,
            PlayerStatus playerStatus)
    {
        JSONObject responseJsonObject = new JSONObject();
        System.out.println("GetInvitations");

        String loggedInUseremail = Helper.getLoggedInUserEmail(request);

        int gameId = getGameIdFromJson(json);

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        User user = mem.getUserByEmail(loggedInUseremail);
        Game game = mem.getGameById(gameId);

        List<Player> gamePlayers = user.getGamePlayers();
        JSONArray notAcceptedYetGamesJsonArray = new JSONArray();

        for (Player player : gamePlayers)
        {
            System.out.println("PLAYERID: " + player.getId());
            if ((player.getGame() == game) && (player.getPlayerStatus() != playerStatus))
            {

                try
                {
                    player.setPlayerStatus(playerStatus);
                    mem.commit(player);
                    responseJsonObject.put("success", true);

                } catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    try
                    {
                        responseJsonObject.put("success", false);
                    } catch (JSONException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                }

                System.out.println(player.getGame().getName() + " - " + player.getId());
            }

        }
        mem.closeDB();
        System.out.println(notAcceptedYetGamesJsonArray);

        return JResponse.ok(responseJsonObject).build();
    }
}
