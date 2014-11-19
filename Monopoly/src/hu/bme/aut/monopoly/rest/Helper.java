package hu.bme.aut.monopoly.rest;

import hu.bme.aut.monopoly.model.BuildingPlace;
import hu.bme.aut.monopoly.model.Game;
import hu.bme.aut.monopoly.model.MonopolyEntityManager;
import hu.bme.aut.monopoly.model.Place;
import hu.bme.aut.monopoly.model.SimplePlace;
import hu.bme.aut.monopoly.model.StartPlace;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class Helper
{
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

            StartPlace satartPlace = new StartPlace();
            satartPlace.setGame(game);
            // TODO through money
            satartPlace.setThroughMoney(1000);
            mem.commit(satartPlace);

            places.add(satartPlace);
            // TODO boardsize
            for (int i = 1; i < 40; i++)
            {
                if (i % 3 == 0)
                {
                    BuildingPlace buildingPlace = new BuildingPlace();
                    buildingPlace.setGame(game);
                    // Building building = new Building();
                    // building.setName("Building"+i);
                    // building.setPrice(100);
                    // building.setHousePrice(50);
                    // building.setBaseNightPayment(20);
                    mem.commit(buildingPlace);
                    places.add(buildingPlace);
                } else
                {
                    SimplePlace simplePlace = new SimplePlace();
                    simplePlace.setGame(game);
                    mem.commit(simplePlace);
                    places.add(simplePlace);
                }
            }
            game.setPlaces(places);
            mem.commit(game);
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mem.closeDB();
        return places;
    }
}
