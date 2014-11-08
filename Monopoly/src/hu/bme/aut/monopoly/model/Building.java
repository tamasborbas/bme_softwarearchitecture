package hu.bme.aut.monopoly.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Building
{
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private String name;
    private int price;
    private int housePrice;
    private int baneNightPayment;
    private int perHousePayment;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getPrice()
    {
        return price;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public int getHousePrice()
    {
        return housePrice;
    }

    public void setHousePrice(int housePrice)
    {
        this.housePrice = housePrice;
    }

    public int getBaneNightPayment()
    {
        return baneNightPayment;
    }

    public void setBaneNightPayment(int baneNightPayment)
    {
        this.baneNightPayment = baneNightPayment;
    }

    public int getPerHousePayment()
    {
        return perHousePayment;
    }

    public void setPerHousePayment(int perHousePayment)
    {
        this.perHousePayment = perHousePayment;
    }

    public int getId()
    {
        return id;
    }

}
