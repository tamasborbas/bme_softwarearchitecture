package hu.bme.aut.monopoly.rest;

import hu.bme.aut.monopoly.model.Building;
import hu.bme.aut.monopoly.model.BuildingPlace;
import hu.bme.aut.monopoly.model.Game;
import hu.bme.aut.monopoly.model.MonopolyEntityManager;
import hu.bme.aut.monopoly.model.Place;
import hu.bme.aut.monopoly.model.SimplePlace;
import hu.bme.aut.monopoly.model.StartPlace;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class Helper
{
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
}
