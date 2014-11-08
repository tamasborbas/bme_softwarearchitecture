package hu.bme.aut.monopoly.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;


@Entity
public class Player extends Participant
{

    private int money;
    private User user;
    @Enumerated(EnumType.STRING)
    private PlayerStatus playerStatus;
    private List<Step> steps = new ArrayList<Step>();

    public int getMoney()
    {
        return money;
    }

    public void setMoney(int money)
    {
        this.money = money;
    }

    public PlayerStatus getPlayerStatus()
    {
        return playerStatus;
    }

    public void setPlayerStatus(PlayerStatus playerStatus)
    {
        this.playerStatus = playerStatus;
    }

    public List<Step> getSteps()
    {
        return steps;
    }

    public void setSteps(List<Step> steps)
    {
        this.steps = steps;
    }

    @ManyToOne
    public User getUser()
    {
        return user;
    }

}
