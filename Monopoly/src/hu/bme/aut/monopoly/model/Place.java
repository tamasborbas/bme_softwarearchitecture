package hu.bme.aut.monopoly.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Entity
public class Place
{
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private Game game;

    @ManyToOne
    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    public int getId()
    {
        return id;
    }

}
