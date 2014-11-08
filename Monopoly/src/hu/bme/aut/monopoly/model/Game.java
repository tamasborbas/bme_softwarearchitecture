package hu.bme.aut.monopoly.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


@Entity
public class Game
{
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;
    private String name;
    private User ownerOfGame;
    private User actualPlayer;
    private List<Participant> players = new ArrayList<Participant>();
    private List<Place> places = new ArrayList<Place>();

    public GameStatus getGameStatus()
    {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus)
    {
        this.gameStatus = gameStatus;
    }

    @OneToMany
    public List<Participant> getPlayers()
    {
        return players;
    }

    public void setPlayers(List<? extends Participant> players)
    {
        this.players.addAll(players);
    }

    @OneToMany
    public List<Place> getPlaces()
    {
        return places;
    }

    public void setPlaces(List<Place> places)
    {
        this.places = places;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public User getOwnerOfGame()
    {
        return ownerOfGame;
    }

    public void setOwnerOfGame(User ownerOfGame)
    {
        this.ownerOfGame = ownerOfGame;
    }

    public User getActualPlayer()
    {
        return actualPlayer;
    }

    public void setActualPlayer(User actualPlayer)
    {
        this.actualPlayer = actualPlayer;
    }

}
