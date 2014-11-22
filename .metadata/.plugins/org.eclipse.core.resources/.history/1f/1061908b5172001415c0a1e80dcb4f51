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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement(name = "Player")
@NamedQuery(name = "Player.getPlayerById", query = "SELECT p FROM Player p WHERE p.id =:idPattern")
public class Player implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Game game;
    private List<BuildingPlace> buildings = new ArrayList<BuildingPlace>();
    private int money;
    private User user;
    @Enumerated(EnumType.STRING)
    private PlayerStatus playerStatus;
    private List<Step> steps = new ArrayList<Step>();

    @XmlElement
    public int getMoney()
    {
        return money;
    }

    public void setMoney(int money)
    {
        this.money = money;
    }

    @XmlElement
    public PlayerStatus getPlayerStatus()
    {
        return playerStatus;
    }

    public void setPlayerStatus(PlayerStatus playerStatus)
    {
        this.playerStatus = playerStatus;
    }

    @XmlElement
    public List<Step> getSteps()
    {
        return steps;
    }

    public void setSteps(List<Step> steps)
    {
        this.steps = steps;
    }

    public void addStep(Step step)
    {
        this.steps.add(step);
    }

    @XmlElement
    @ManyToOne
    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    @XmlElement
    @ManyToOne
    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    @XmlElement
    @OneToMany
    public List<BuildingPlace> getBuildings()
    {
        return buildings;
    }

    public void setBuildings(List<BuildingPlace> buildings)
    {
        this.buildings = buildings;
    }

    @XmlElement
    public int getId()
    {
        return id;
    }

}
