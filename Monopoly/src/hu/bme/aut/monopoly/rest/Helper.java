package hu.bme.aut.monopoly.rest;

import hu.bme.aut.monopoly.model.Building;
import hu.bme.aut.monopoly.model.BuildingPlace;
import hu.bme.aut.monopoly.model.Game;
import hu.bme.aut.monopoly.model.MonopolyEntityManager;
import hu.bme.aut.monopoly.model.Place;
import hu.bme.aut.monopoly.model.Player;
import hu.bme.aut.monopoly.model.SimplePlace;
import hu.bme.aut.monopoly.model.StartPlace;
import hu.bme.aut.monopoly.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Helper
{
    public static int initializationMoney = 2000;
    public static int throughMoney = 1000;
    public static int boardSize = 16;

    public static String getLoggedInUserEmail(HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);
        String loggedInUseremail = (String) session.getAttribute("loggedInUser");
        System.out.println(loggedInUseremail);
        return loggedInUseremail;
    }

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
                if (i % 2 == 0)
                {
                    BuildingPlace buildingPlace = new BuildingPlace();
                    buildingPlace.setGame(game);
                    buildingPlace.setPlaceSequenceNumber(i + 1);

                    // Itt le kéne kérdezni az (i/2)-es id-val rendelkezõ buildinget az adatbázisból, és azt
                    // beállítani
                    Building building = mem.getBuildingById(i / 2);
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

    public static JSONObject getAllDetailesOfPlayer(Player player) throws JSONException
    {
        System.out.println("PLAYER: " + player.getId());
        JSONObject aPlayerJsonObject = new JSONObject();
        aPlayerJsonObject.put("playerId", player.getId());
        aPlayerJsonObject.put("name", player.getUser().getName());
        aPlayerJsonObject.put("status", player.getPlayerStatus());

        aPlayerJsonObject.put("money", player.getMoney());
        aPlayerJsonObject.put("placeSequenceNumber", player.getSteps().get(player.getSteps().size() - 1)
                .getFinishPlace().getPlaceSequenceNumber());

        JSONArray ownedBuildingsJsonArray = new JSONArray();
        for (BuildingPlace buildingPlace : player.getBuildings())
        {
            JSONObject aOwnedBuildingJsonObject = new JSONObject();

            aOwnedBuildingJsonObject.put("buildingId", buildingPlace.getBuilding().getId());
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

}
