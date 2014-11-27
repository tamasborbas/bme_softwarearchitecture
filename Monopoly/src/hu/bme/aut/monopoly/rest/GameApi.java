package hu.bme.aut.monopoly.rest;

import hu.bme.aut.monopoly.email.EmailManager;
import hu.bme.aut.monopoly.model.Building;
import hu.bme.aut.monopoly.model.BuildingPlace;
import hu.bme.aut.monopoly.model.Game;
import hu.bme.aut.monopoly.model.GameStatus;
import hu.bme.aut.monopoly.model.HouseBuying;
import hu.bme.aut.monopoly.model.MonopolyEntityManager;
import hu.bme.aut.monopoly.model.Place;
import hu.bme.aut.monopoly.model.Player;
import hu.bme.aut.monopoly.model.PlayerStatus;
import hu.bme.aut.monopoly.model.StartPlace;
import hu.bme.aut.monopoly.model.Step;
import hu.bme.aut.monopoly.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
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
 * Class for the game specific rest requests
 */
@Path("/gameapi")
public class GameApi
{
    /**
     * Gives the details of the game (players, places, )
     */
    @Path("/OpenGame")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response openGame(String json, @Context
    HttpServletRequest request)
    {
        String loggedInUseremail = Helper.getLoggedInUserEmail(request);
        String notLoggedInUseremail = null;
        int gameId;
        try
        {
            JSONArray jsonTomb;
            jsonTomb = new JSONArray(json);

            notLoggedInUseremail = jsonTomb.getJSONObject(0).getString("email");
            gameId = jsonTomb.getJSONObject(0).getInt("gameId");

            System.out.println("GAMEID: " + gameId);
        } catch (JSONException e1)
        {
            e1.printStackTrace();
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Invalid JSON: " + json).build();
        }

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();

        Game game = mem.getGameById(gameId);
        boolean isActualPlayer = false;
        PlayerStatus playerStatus = PlayerStatus.notAcceptedYet;

        // ha nem regisztralt a felhasznalo, akkor itt inditunk neki sessiont
        if (notLoggedInUseremail != null)
        {
            HttpSession session = request.getSession(true);
            session.setAttribute("notLoggedInUser", notLoggedInUseremail);
            User notLoggedInUser = mem.getUserByEmail(notLoggedInUseremail);
            System.out.println("not logged in: " + notLoggedInUseremail);
            System.out.println("not logged in: " + notLoggedInUser.getEmail());
            // ellenorizzuk, hogy o az aktualis jatekos-e
            isActualPlayer = Helper.isPlayerActualPlayerOfTheGame(game, isActualPlayer, notLoggedInUser);
            playerStatus = Helper.playerStatusOfTheGame(game, notLoggedInUser);
        }
        // bejelentkezett felhasznalo eseten is ellenorizzuk az aktualitast
        else
        {
            User loggedInUser = mem.getUserByEmail(loggedInUseremail);
            isActualPlayer = Helper.isPlayerActualPlayerOfTheGame(game, isActualPlayer, loggedInUser);
            playerStatus = Helper.playerStatusOfTheGame(game, loggedInUser);
        }

        JSONObject gameDetailesJsonObject = new JSONObject();
        try
        {
            JSONObject actPlayer = Helper.getAllDetailesOfPlayer(game.getActualPlayer());
            System.out.println("GAME: " + game.getName());
            gameDetailesJsonObject.put("id", game.getId());
            gameDetailesJsonObject.put("gameStatus", game.getGameStatus());
            gameDetailesJsonObject.put("name", game.getName());
            gameDetailesJsonObject.put("nameOfGameOwner", game.getOwnerOfGame().getName());
            gameDetailesJsonObject.put("actualPlayer", actPlayer);
            gameDetailesJsonObject.put("isActualPlayer", isActualPlayer);
            gameDetailesJsonObject.put("playerStatus", playerStatus);

            JSONArray acceptedPlayersJsonArray = new JSONArray();
            JSONArray loserPlayersJsonArray = new JSONArray();

            if (actPlayer != null)
            {
                for (Player player : game.getPlayers())
                {
                    if (player.getPlayerStatus() == PlayerStatus.accepted)
                    {
                        JSONObject aPlayerJsonObject = Helper.getAllDetailesOfPlayer(player);
                        acceptedPlayersJsonArray.put(aPlayerJsonObject);
                    } else if (player.getPlayerStatus() == PlayerStatus.lost)
                    {
                        JSONObject aPlayerJsonObject = new JSONObject();
                        aPlayerJsonObject.put("playerId", player.getId());
                        aPlayerJsonObject.put("name", player.getUser().getName());

                        loserPlayersJsonArray.put(aPlayerJsonObject);
                    }
                }
            }
            gameDetailesJsonObject.put("acceptedPlayers", acceptedPlayersJsonArray);
            gameDetailesJsonObject.put("loserPlayers", loserPlayersJsonArray);

            JSONArray placesJsonArray = new JSONArray();
            if (actPlayer != null)
            {
                for (Place place : game.getPlaces())
                {

                    JSONObject aPlace = new JSONObject();
                    aPlace.put("placeId", place.getId());
                    aPlace.put("placeSequenceNumber", place.getPlaceSequenceNumber());
                    aPlace.put("type", place.getClass().getSimpleName());

                    int owner = 0;
                    String placeName = "";
                    int totalPriceForNight = 0;
                    int price = 0;
                    if (place instanceof BuildingPlace)
                    {
                        BuildingPlace buildingPlace = mem.getBuildingPlaceById(place.getId());
                        Building building = buildingPlace.getBuilding();
                        placeName = building.getName();
                        if (buildingPlace.getOwnerPlayer() != null)
                        {
                            owner = buildingPlace.getOwnerPlayer().getId();
                        }
                        price = building.getPrice();
                        totalPriceForNight = building.getBaseNightPayment()
                                + (building.getPerHousePayment() * buildingPlace.getHouseNumber());
                    } else if (place instanceof StartPlace)
                    {
                        placeName = "Start";
                    }
                    aPlace.put("owner", owner);
                    aPlace.put("placeName", placeName);
                    aPlace.put("price", price);
                    aPlace.put("totalPriceForNight", totalPriceForNight);

                    // minden placehez egy lista, h melyik players all rajta ID-NAME
                    JSONArray playersOnPlaceJsonArray = new JSONArray();

                    List<Player> realPlayers = Helper.sortRealPlayer(game);
                    for (Player player : realPlayers)
                    {
                        if ((player.getSteps().get(player.getSteps().size() - 1).getFinishPlace() == place))
                        {
                            JSONObject aPlayerOnPlaceJsonObject = new JSONObject();
                            aPlayerOnPlaceJsonObject.put("playerId", player.getId());
                            aPlayerOnPlaceJsonObject.put("userName", player.getUser().getName());
                            aPlayerOnPlaceJsonObject.put("playerSequence", realPlayers.indexOf(player));
                            playersOnPlaceJsonArray.put(aPlayerOnPlaceJsonObject);
                        }
                    }
                    aPlace.put("playersOnPlace", playersOnPlaceJsonArray);
                    placesJsonArray.put(aPlace);
                }
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

    /**
     * Gives the properties of a building
     */
    @Path("/GetBuilding")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuilding(String json, @Context
    HttpServletRequest request)
    {
        JSONArray jsonTomb;
        int placeId = 0;

        try
        {
            jsonTomb = new JSONArray(json);
            placeId = jsonTomb.getJSONObject(0).getInt("placeId");
            System.out.println("BuildigPlaceId: " + placeId);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();
        JSONObject buildingPlaceJsonObject = new JSONObject();

        if (mem.getPlaceById(placeId) instanceof BuildingPlace)
        {
            BuildingPlace buildingPlace = mem.getBuildingPlaceById(placeId);
            System.out.println("BuildigPlace: " + buildingPlace.getId() + " - " + buildingPlace.getOwnerPlayer());
            if ((buildingPlace != null) && (buildingPlace.getOwnerPlayer() != null))
            {
                try
                {
                    buildingPlaceJsonObject.put("ownerUserName", buildingPlace.getOwnerPlayer().getUser().getName());
                    Helper.getBuildingPlaceDetailes(buildingPlace, buildingPlaceJsonObject);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        mem.closeDB();
        System.out.println(buildingPlaceJsonObject);
        return Response.ok(buildingPlaceJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    /**
     * Gives the properties of a place
     */
    @Path("/GetPlaceData")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlaceData(String json, @Context
    HttpServletRequest request)
    {
        // placeid kinyerese jsonbol
        System.out.println(json);
        JSONArray jsonTomb;
        int placeId = 0;

        try
        {
            jsonTomb = new JSONArray(json);
            placeId = jsonTomb.getJSONObject(0).getInt("placeId");
            System.out.println("PlaceId: " + placeId);
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
        Place place = mem.getPlaceById(placeId);
        try
        {
            // ha egy start mezorol van szo, akkor at kell adni a kor teljesitesekor jara osszeget
            if (place instanceof StartPlace)
            {
                StartPlace startPlace = mem.getPlaceById(placeId);

                placeJsonObject.put("placeId", startPlace.getId());
                placeJsonObject.put("placeSequenceNumber", startPlace.getPlaceSequenceNumber());
                placeJsonObject.put("throughMoney", startPlace.getThroughMoney());
                placeJsonObject.put("name", "Start");
            }
            // ha building place, akkor kuldjuk az epuletek adatait is
            else if (place instanceof BuildingPlace)
            {
                BuildingPlace buildingPlace = mem.getBuildingPlaceById(placeId);

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
                    placeJsonObject.put("name", buildingPlace.getBuilding().getName());
                    placeJsonObject.put("buildingPrice", buildingPlace.getBuilding().getPrice());
                    placeJsonObject.put("buildingHousePrice", buildingPlace.getBuilding().getHousePrice());
                    placeJsonObject
                            .put("buildingBaseNightPayment", buildingPlace.getBuilding().getBaseNightPayment());
                    placeJsonObject.put("buildingPerHousePayment", buildingPlace.getBuilding().getPerHousePayment());
                }

                // else
                // {
                // placeJsonObject.put("buildingId", "");
                // placeJsonObject.put("buildingName", "");
                // placeJsonObject.put("buildingPrice", "");
                // placeJsonObject.put("buildingHousePrice", "");
                // placeJsonObject.put("buildingBaseNightPayment", "");
                // placeJsonObject.put("buildingPerHousePaymentss", "");
                // }

                isBuilding = true;
            }
            // ha sima mezorol van szo
            else
            {
                placeJsonObject.put("gameId", place.getGame().getId());
                placeJsonObject.put("placeSequenceNumber", place.getPlaceSequenceNumber());
                placeJsonObject.put("placeId", place.getId());
                placeJsonObject.put("name", "");
            }
            System.out.println(placeJsonObject);

            responseJsonObject.put("buildingData", placeJsonObject);
            responseJsonObject.put("isBuilding", isBuilding);

        } catch (JSONException e)
        {
            mem.closeDB();
            e.printStackTrace();
            return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
        }
        mem.closeDB();

        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    /**
     * Gives the players property
     */
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
                ownedBuildingsJsonArray.put(Helper.getBuildingPlaceDetailes(buildingPlace, buildingPlaceJsonObject));
            }

            playerJsonObject.put("ownedBuildingPlaces", ownedBuildingsJsonArray);
        } catch (JSONException e)
        {
            e.printStackTrace();
            return Response.status(Response.Status.NO_CONTENT).entity("Can not create JSON.").build();
        } finally
        {
            mem.closeDB();
        }

        System.out.println(playerJsonObject);

        return Response.ok(playerJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    /**
     * Make a step
     */
    @Path("/MakeStep")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response makeStep(String json, @Context
    HttpServletRequest request)
    {
        JSONArray jsonTomb;
        int errorCode = 0;
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

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();

        Player player = mem.getPlayerById(playerId);

        Step oldStep = player.getSteps().get(player.getSteps().size() - 1);
        // dobas, illetve lepes helyessegenek ellenorzese
        if (roll < 1 || roll > 6
                || (oldStep.getFinishPlace().getPlaceSequenceNumber() + roll) % 16 != placeSequenceNumber)
        {
            errorCode = 2;
            System.out.println("CSALAS");
        }
        // ha nem tortenik csalas, akkor leptetunk
        else
        {
            Step step = new Step();
            Player ownerOfBuildingPlace = null;
            List<BuildingPlace> soldBuildingPlaces = new ArrayList<BuildingPlace>();
            List<HouseBuying> houseBuyings = new ArrayList<HouseBuying>();

            Place newPlace = null;
            newPlace = Helper.getPlaceIdByPlayerAndPlaceSequenceNumber(placeSequenceNumber, player, newPlace);

            // leptetes
            step.setFinishPlace(newPlace);
            player.addStep(step);
            System.out.println("UJ MEZO: " + step.getFinishPlace());
            try
            {
                // mem.commit(step);
                // StartPlacere leptunk
                if (newPlace instanceof StartPlace)
                {
                    System.out.println("START MEZO");
                    player.setMoney(player.getMoney() + Helper.throughMoney);
                }
                // buildingPlacere leptunk
                else if (newPlace instanceof BuildingPlace)
                {
                    System.out.println("EPITESI MEZO ");
                    BuildingPlace buildingPlace = mem.getPlaceById(newPlace.getId());

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

                    // eladott épületekkel szükséges mûveletek elvégzése (házak
                    // levétele a telekrõl, a telek
                    // tulajdonjogának törlése...)
                    if (isSold)
                    {
                        int soldBuildingPlaceId;
                        for (int i = 0; i < soldBuildingsIds.length(); i++)

                        {
                            soldBuildingPlaceId = soldBuildingsIds.getJSONObject(i).getInt("placeID");
                            BuildingPlace soldBuildingPlace = mem.getBuildingPlaceById(soldBuildingPlaceId);

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

                    // ellenorizzuk, hogy van-e ilyen id-ju building place
                    if (mem.getBuildingPlaceById(buildingId) != null)
                    {
                        BuildingPlace boughtBuildingPlace = mem.getBuildingPlaceById(buildingId);

                        HouseBuying houseBuying = new HouseBuying();
                        houseBuying.setBuyedHouseNumber(number);
                        houseBuying.setForBuilding(boughtBuildingPlace);
                        // mem.commit(houseBuying);
                        houseBuyings.add(houseBuying);

                        // Ez kimaradt, növelni kell a házak számát az adott
                        // telken
                        int newSumHouseNumber = boughtBuildingPlace.getHouseNumber() + number;
                        if (newSumHouseNumber > 5)
                        {
                            // csalt, nem érvényes a lépés
                            // ilyenkor hogyan vonod vissza az eddig
                            // felkommitolt dolgokat az adatbázisból?
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

                // Ide kellene majd még, hogy megvizsgáljuk a pénzét, ha
                // negatív, akkor menjen csõdbe (telkek elvétele
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

                //mem.commit(step);

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

            // Szerintem csak itt kéne kommitolni mindent, azt nem lehet?
            // mondjuk a houseBuyings-os listán
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
                        entityManager.persist(aSoldBuildingPlace);
                    }
                }

                for (HouseBuying houseBuying : houseBuyings)
                {
                    entityManager.persist(houseBuying);
                }

                entityManager.persist(player);
                entityManager.persist(step);

                // kovetkezo jatekos beallitasa
                List<Player> realPlayers = Helper.sortRealPlayer(player.getGame());
                int nextActualPlayerIndex = (realPlayers.indexOf(player) + 1) % realPlayers.size();
                Game game = player.getGame();
                game.setActualPlayer(realPlayers.get(nextActualPlayerIndex));
                entityManager.persist(game);

                entityManager.getTransaction().commit();

                // osszefoglalo kikuldese
                EmailManager.sendStepSummaryEmail(realPlayers.get(nextActualPlayerIndex).getUser().getEmail(), game);
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

        // ha nem volt hiba es nem regisztralt felhasznaloval van dolgunk, akkor
        // lezarjuk a hozza tartozo sessiont
        HttpSession session = request.getSession(true);
        if ((errorCode == 0) && (session.getAttribute("notLoggedInUser") != null))
        {
            String notLoggedInUseremail = (String) session.getAttribute("notLoggedInUser");
            if ((notLoggedInUseremail != null) && !(notLoggedInUseremail.equals("")))
            {
                session.invalidate();
            }
        }

        return Response.ok(responseJsonObject.toString(), MediaType.APPLICATION_JSON).build();
    }

}
