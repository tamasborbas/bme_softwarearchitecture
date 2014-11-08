package hu.bme.aut.monopoly.model;

import javax.persistence.Entity;

@Entity
public class StartPlace extends Place
{
    private int throughMoney;

    public int getThroughMoney()
    {
        return throughMoney;
    }

    public void setThroughMoney(int throughMoney)
    {
        this.throughMoney = throughMoney;
    }
}
