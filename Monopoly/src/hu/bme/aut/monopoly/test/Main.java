package hu.bme.aut.monopoly.test;

import hu.bme.aut.monopoly.model.Game;
import hu.bme.aut.monopoly.model.GameStatus;
import hu.bme.aut.monopoly.model.MonopolyEntityManager;
import hu.bme.aut.monopoly.model.Player;
import hu.bme.aut.monopoly.model.PlayerStatus;
import hu.bme.aut.monopoly.model.User;
import hu.bme.aut.monopoly.model.UserType;
import hu.bme.aut.monopoly.rest.Helper;

import java.util.ArrayList;
import java.util.List;


public class Main
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();

        // uj user hozzaadasa
        try
        {
             mem.addNewUser("anna@gmail.com", "a70f9e38ff015afaa9ab0aacabee2e13", "anna", UserType.user);
             mem.addNewUser("admin@gmail.com", "21232f297a57a5a743894a0e4a801fc3", "admin", UserType.user);
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // user lekerdezese nev alapjan

        // User user = mem.getUserByEmail("admin@gmail.com");
        // System.out.println(user.getEmail() + " - " + user.getName());
        //
        // mem.listAndCreatePlayerTest();
        // mem.addNewPlayerTest();

        // jatekosok jatekhoz rendelese
         openGame(mem, "TEST2", GameStatus.inProgress);
         openGame(mem, "TEST3", GameStatus.init);

        Helper.makeBoard(1);
        Helper.makeBoard(2);

        // List<? extends Place> places = mem.getPlacesByGameId(3);
        // for (Place place : places)
        // {
        // System.out.println(place.getId() + " - " + place.getClass());
        // }

        try
        {
            // BuildingPlace buildingPlace = mem.getBuildingPlaceById(54);
            // Building building = new Building();
            // building.setName("Hilton Hotel");
            // building.setBaseNightPayment(100);
            // building.setHousePrice(5);
            // building.setPerHousePayment(2);
            // building.setPrice(200);
            // mem.commit(building);
            // buildingPlace.setBuilding(building);
            // buildingPlace.setOwnerPlayer(mem.getPlayerById(4));
            // mem.commit(buildingPlace);

        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mem.closeDB();

    }

    private static void openGame(MonopolyEntityManager mem, String gameName, GameStatus gs)
    {
        List<Player> players = new ArrayList<Player>();
        // mem.initDB();
        User user = mem.getUserByEmail("admin@gmail.com");
        Player player = new Player();
        // TODO erteket kitalalni
        player.setMoney(10000);
        player.setPlayerStatus(PlayerStatus.accepted);
        players.add(player);

        User user2 = mem.getUserByEmail("anna@gmail.com");
        Player player2 = new Player();
        // TODO erteket kitalalni
        player2.setMoney(10000);
        player2.setPlayerStatus(PlayerStatus.accepted);
        // TODO USERID
        players.add(player2);
        try
        {
            mem.commit(player);
            user.addGamePlayer(player);
            mem.commit(user);

            mem.commit(player2);
            user2.addGamePlayer(player2);
            mem.commit(user2);

            Game game = new Game();
            game.setName(gameName);
            game.setPlayers(players);
            game.setGameStatus(gs);
            game.setActualPlayer(player);
            game.setOwnerOfGame(user);
            mem.commit(game);

            player.setGame(game);
            player.setUser(user);
            mem.commit(player);

            player2.setGame(game);
            player2.setUser(user2);
            mem.commit(player2);

        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
