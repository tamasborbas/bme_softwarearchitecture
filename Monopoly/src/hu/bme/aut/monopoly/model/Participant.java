package hu.bme.aut.monopoly.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


@Entity
public class Participant
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Game game;
    private List<BuildingPlace> buildings = new ArrayList<BuildingPlace>();

    @ManyToOne
    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    @OneToMany
    public List<BuildingPlace> getBuildings()
    {
        return buildings;
    }

    public void setBuildings(List<BuildingPlace> buildings)
    {
        this.buildings = buildings;
    }

    public Long getId()
    {
        return id;
    }

}
