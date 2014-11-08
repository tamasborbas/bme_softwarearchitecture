package hu.bme.aut.monopoly.rest;

import hu.bme.aut.monopoly.model.Game;
import hu.bme.aut.monopoly.model.GameStatus;
import hu.bme.aut.monopoly.model.MonopolyEntityManager;
import hu.bme.aut.monopoly.model.Player;
import hu.bme.aut.monopoly.model.PlayerStatus;
import hu.bme.aut.monopoly.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.json.JSONArray;
import org.json.JSONException;


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

}
