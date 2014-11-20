package hu.bme.aut.monopoly.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement(name = "Game")
@NamedQueries({

        @NamedQuery(name = "Game.getActiveGamesByEmail", query = "SELECT g FROM Game g, User u, Player p WHERE u =:userPattern and p MEMBER OF g.players and p.user = u and  g.gameStatus like 'inProgress'"),
        @NamedQuery(name = "Game.getOwnedInitGamesByEmail", query = "SELECT g FROM Game g, User u, Player p WHERE u =:userPattern and p MEMBER OF g.players and u.id = g.ownerOfGame.id and p.user = u and g.gameStatus like 'init'"),
        @NamedQuery(name = "Game.getOwnedGamesByUser", query = "SELECT g FROM Game g, User u, Player p WHERE u =:userPattern and p MEMBER OF g.players and u.id = g.ownerOfGame.id and p.user = u"),

        @NamedQuery(name = "Game.getGameById", query = "SELECT g FROM Game g WHERE g.id =:idPattern "),
        @NamedQuery(name = "Game.getPlacesByGameId", query = "SELECT p FROM Game g, Place p WHERE g.id =:idPattern  and p MEMBER OF g.places") })
public class Game implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;
    private String name;
    private User ownerOfGame;
    private Player actualPlayer;
    private List<Player> players = new ArrayList<Player>();
    private List<Place> places = new ArrayList<Place>();

    @XmlElement
    public GameStatus getGameStatus()
    {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus)
    {
        this.gameStatus = gameStatus;
    }

    @XmlElement
    @OneToMany
    public List<Player> getPlayers()
    {
        return players;
    }

    public void setPlayers(List<Player> players)
    {
        this.players.addAll(players);
    }

    @XmlElement
    @OneToMany
    public List<Place> getPlaces()
    {
        return places;
    }

    public void setPlaces(List<? extends Place> places)
    {
        this.places.addAll(places);
    }

    @XmlElement
    public int getId()
    {
        return id;
    }

    @XmlElement
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @XmlElement
    public User getOwnerOfGame()
    {
        return ownerOfGame;
    }

    public void setOwnerOfGame(User ownerOfGame)
    {
        this.ownerOfGame = ownerOfGame;
    }

    @XmlElement
    public Player getActualPlayer()
    {
        return actualPlayer;
    }

    public void setActualPlayer(Player actualPlayer)
    {
        this.actualPlayer = actualPlayer;
    }

}
