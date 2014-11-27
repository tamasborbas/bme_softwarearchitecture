package hu.bme.aut.monopoly.rest;

import hu.bme.aut.monopoly.model.Building;
import hu.bme.aut.monopoly.model.BuildingPlace;
import hu.bme.aut.monopoly.model.Game;
import hu.bme.aut.monopoly.model.GameStatus;
import hu.bme.aut.monopoly.model.MonopolyEntityManager;
import hu.bme.aut.monopoly.model.Place;
import hu.bme.aut.monopoly.model.Player;
import hu.bme.aut.monopoly.model.PlayerComparator;
import hu.bme.aut.monopoly.model.PlayerStatus;
import hu.bme.aut.monopoly.model.SimplePlace;
import hu.bme.aut.monopoly.model.StartPlace;
import hu.bme.aut.monopoly.model.Step;
import hu.bme.aut.monopoly.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Helper
{
    public static int initializationMoney = 2000;
    public static int throughMoney = 1000;
    public static int boardSize = 16;

    /**
     * Gets the user email rom the session
     */
    public static String getLoggedInUserEmail(HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);
        String loggedInUseremail = (String) session.getAttribute("loggedInUser");
        System.out.println(loggedInUseremail);
        return loggedInUseremail;
    }

    /**
     * Create the board
     */
    public static List<? extends Place> makeBoard(int gameId)
    {
        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        List<Place> places = new ArrayList<Place>();
        try
        {
            Game game = mem.getGameById(gameId);

            StartPlace startPlace = new StartPlace();
            startPlace.setGame(game);
            startPlace.setThroughMoney(throughMoney);
            startPlace.setPlaceSequenceNumber(1);
            mem.commit(startPlace);

            places.add(startPlace);
            for (int i = 1; i < boardSize; i++)
            {
                if ((i + 1) % 2 == 0)
                {
                    BuildingPlace buildingPlace = new BuildingPlace();
                    buildingPlace.setGame(game);
                    buildingPlace.setPlaceSequenceNumber(i + 1);

                    // Itt le kéne kérdezni az (i/2)-es id-val rendelkezõ buildinget az adatbázisból, és azt
                    // beállítani
                    Building building = mem.getBuildingById((i + 1) / 2);
                    buildingPlace.setBuilding(building);

                    // Building building = new Building();
                    // building.setName("Building"+i);
                    // building.setPrice(100);
                    // building.setHousePrice(50);
                    // building.setBaseNightPayment(20);
                    // mem.commit(buildingPlace);
                    places.add(buildingPlace);
                } else
                {
                    SimplePlace simplePlace = new SimplePlace();
                    simplePlace.setGame(game);
                    simplePlace.setPlaceSequenceNumber(i + 1);
                    // mem.commit(simplePlace);
                    places.add(simplePlace);
                }
            }

            EntityManager entityManager = mem.getEntityManager();
            entityManager.getTransaction().begin();
            try
            {
                for (Place place : places)
                {
                    entityManager.persist(place);
                }

                game.setPlaces(places);
                entityManager.persist(game);

                entityManager.getTransaction().commit();
            } catch (Exception e)
            {
                entityManager.getTransaction().rollback();
                throw e;
            }

        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mem.closeDB();
        return places;
    }

    /**
     * Gives the properties of the player
     */
    public static JSONObject getAllDetailesOfPlayer(Player player) throws JSONException
    {
        if (player != null)
        {
            System.out.println("PLAYER: " + player.getId());
            JSONObject aPlayerJsonObject = new JSONObject();
            aPlayerJsonObject.put("playerId", player.getId());
            aPlayerJsonObject.put("name", player.getUser().getName());
            aPlayerJsonObject.put("status", player.getPlayerStatus());
            aPlayerJsonObject.put("playerSequence", Helper.sortRealPlayer(player.getGame()).indexOf(player));

            aPlayerJsonObject.put("money", player.getMoney());
            aPlayerJsonObject.put("placeSequenceNumber", player.getSteps().get(player.getSteps().size() - 1)
                    .getFinishPlace().getPlaceSequenceNumber());

            JSONArray ownedBuildingsJsonArray = new JSONArray();
            for (BuildingPlace buildingPlace : player.getBuildings())
            {
                JSONObject aOwnedBuildingJsonObject = new JSONObject();

                aOwnedBuildingJsonObject.put("buildingId", buildingPlace.getId());
                aOwnedBuildingJsonObject.put("buildingName", buildingPlace.getBuilding().getName());
                aOwnedBuildingJsonObject.put("nuberOfHouse", buildingPlace.getHouseNumber());
                aOwnedBuildingJsonObject.put("price", buildingPlace.getBuilding().getPrice());
                aOwnedBuildingJsonObject.put("housePrice", buildingPlace.getBuilding().getHousePrice());
                aOwnedBuildingJsonObject.put("baseNightPayment", buildingPlace.getBuilding().getBaseNightPayment());
                aOwnedBuildingJsonObject.put("perHousePayment", buildingPlace.getBuilding().getPerHousePayment());
                aOwnedBuildingJsonObject.put("maxHouseNumber", 5 - buildingPlace.getHouseNumber());
                ownedBuildingsJsonArray.put(aOwnedBuildingJsonObject);
            }

            aPlayerJsonObject.put("ownedBuildings", ownedBuildingsJsonArray);
            return aPlayerJsonObject;
        }
        return null;
    }

    /**
     * Checks the given user is active player in the game
     */
    public static boolean isPlayerActualPlayerOfTheGame(Game game, boolean isActualPlayer, User user)
    {
        for (Player playerOfUser : user.getGamePlayers())
        {
            if (playerOfUser.getGame() == game && (playerOfUser == game.getActualPlayer()))
            {
                isActualPlayer = true;
            }
        }
        return isActualPlayer;
    }

    /**
     * Checks the given user status in the game
     */
    public static PlayerStatus playerStatusOfTheGame(Game game, User user)
    {
        for (Player playerOfUser : user.getGamePlayers())
        {
            if (playerOfUser.getGame() == game)
            {
                System.out.println("****************");
                System.out.println(user.getEmail());
                System.out.println(playerOfUser.getId());
                System.out.println(playerOfUser.getGame());
                return playerOfUser.getPlayerStatus();
            }
        }
        return null;
    }

    /**
     * Gets the game id from the JSON
     */
    public static int getGameIdFromJson(String json) throws JSONException
    {
        JSONObject jsonObject;
        System.out.println("JSON: " + json);
        int gameId = 0;

        jsonObject = new JSONObject(json);
        gameId = jsonObject.getInt("gameId");
        System.out.println("GAMEID: " + gameId);

        return gameId;
    }

    /**
     * Get email from JSON
     */
    public static String getEmailFromJson(String json) throws JSONException
    {
        JSONObject jsonObject;
        System.out.println("JSON: " + json);
        String email = "";

        jsonObject = new JSONObject(json);
        email = jsonObject.getString("email");
        System.out.println("GAMEID: " + email);

        return email;
    }

    /**
     * Gets the properties of the game
     */
    public static JSONObject getGameDetailes(Game game) throws JSONException
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
            // aPlayer.put("placeId",
            // player.getSteps().get(player.getSteps().size() -
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

    /**
     * Gets the properties of the game
     */
    public static JSONObject getBuildingPlaceDetailes(BuildingPlace buildingPlace, JSONObject buildingPlaceJsonObject)
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

    /**
     * Gets the number of players of the user in a status
     */
    public static int getNumberOfPlayerInAStatus(User user, PlayerStatus ps)
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

    /**
     * Modify the status of the layer
     */
    public static Response modifyPlayerStatus(String json, HttpServletRequest request, PlayerStatus playerStatus,
            String userEmail)
    {
        JSONObject responseJsonObject = new JSONObject();
        System.out.println("GetInvitations");

        // String loggedInUseremail = Helper.getLoggedInUserEmail(request);

        int gameId;
        try
        {
            gameId = Helper.getGameIdFromJson(json);
        } catch (JSONException e2)
        {
            e2.printStackTrace();
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid JSON").build();
        }

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        User user = mem.getUserByEmail(userEmail);
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

        boolean isThereNotAcceptedYet = false;
        int acceptanceNumber = 0;
        for (Player player : game.getPlayers())
        {
            if (player.getPlayerStatus() == PlayerStatus.notAcceptedYet)
            {
                isThereNotAcceptedYet = true;
            } else if (player.getPlayerStatus() == PlayerStatus.accepted)
            {
                acceptanceNumber++;
            }
        }

        try
        {
            if (!isThereNotAcceptedYet && acceptanceNumber >= 2)
            {
                // kezdojatekos belallitasa
                List<Player> realPlayers = sortRealPlayer(game);
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
            } else if (!isThereNotAcceptedYet)
            {
                game.setGameStatus(GameStatus.finished);
                mem.commit(game);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Database error").build();
        }
        mem.closeDB();
        System.out.println(responseJsonObject);

        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    /**
     * Put the players in order
     */
    public static List<Player> sortRealPlayer(Game game)
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

    public static Place getPlaceIdByPlayerAndPlaceSequenceNumber(int placeSequenceNumber, Player player,
            Place newPlace)
    {
        for (Place place : player.getGame().getPlaces())
        {
            if (place.getPlaceSequenceNumber() == placeSequenceNumber)
            {
                newPlace = place;
            }
        }
        return newPlace;
    }
}
