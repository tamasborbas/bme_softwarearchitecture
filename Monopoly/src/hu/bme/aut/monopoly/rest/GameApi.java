package hu.bme.aut.monopoly.rest;

import hu.bme.aut.monopoly.model.Game;
import hu.bme.aut.monopoly.model.GameStatus;
import hu.bme.aut.monopoly.model.MonopolyEntityManager;
import hu.bme.aut.monopoly.model.Player;
import hu.bme.aut.monopoly.model.PlayerStatus;
import hu.bme.aut.monopoly.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

        HttpSession session = request.getSession(true);
        String loggedInUseremail = (String) session.getAttribute("loggedInUser");
        System.out.println(loggedInUseremail);

        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();

        List<Game> activeGamesByEmail = mem.getActiveGamesByEmail(loggedInUseremail);

        JSONArray activeGamesJsonArray = new JSONArray();
        for (Game game : activeGamesByEmail)
        {
            System.out.println(game.getName());

            JSONObject aActiveGame = new JSONObject();
            try
            {
                aActiveGame.put("id", game.getId());
                aActiveGame.put("name", game.getName());
                aActiveGame.put("actualPlayer", game.getActualPlayer().getName());

                System.out.println(aActiveGame);
                JSONArray acceptedPlayersJsonArray = new JSONArray();

                for (Player player : game.getPlayers())
                {
                    System.out.println((player.getId()));

                    System.out.println("NEV" + player.getUser().getName());
                    if ((player.getPlayerStatus() == PlayerStatus.accepted)
                            && !(player.getUser().getEmail().equals(loggedInUseremail)))
                    {

                        JSONObject aAcceptedPlayer = new JSONObject();
                        aAcceptedPlayer.put("name", player.getUser().getName());
                        acceptedPlayersJsonArray.put(aAcceptedPlayer);

                        System.out.println("NEV2" + player.getUser().getName());
                    }
                }
                aActiveGame.put("players", acceptedPlayersJsonArray);

                System.out.println("AGAME: " + aActiveGame);
                // activeGamesJsonArray.put(aActiveGame);

            } catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            activeGamesJsonArray.put(aActiveGame);
            System.out.println(activeGamesJsonArray);
        }

        mem.closeDB();

        return JResponse.ok(activeGamesJsonArray).build();
    }

    @Path("/StartGame")
    @POST
    public void startGame(String json, @Context
    HttpServletRequest request)
    {
        JSONArray jsonTomb;
        int gameId;

        try
        {
            jsonTomb = new JSONArray(json);
            gameId = jsonTomb.getJSONObject(0).getInt("gameId");

            HttpSession session = request.getSession(true);
            String loggedInUserEmail = (String) session.getAttribute("loggedInUser");
            System.out.println(loggedInUserEmail);

            MonopolyEntityManager mem = new MonopolyEntityManager();
            mem.initDB();
            Game game = mem.getGameById(gameId);
            game.setGameStatus(GameStatus.inProgress);

            mem.closeDB();

        } catch (JSONException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
