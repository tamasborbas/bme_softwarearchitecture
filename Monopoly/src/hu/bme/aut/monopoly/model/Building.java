package hu.bme.aut.monopoly.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement(name = "Building")
public class Building implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private String name;
    private int price;
    private int housePrice;
    private int baseNightPayment;
    private int perHousePayment;

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
    public int getPrice()
    {
        return price;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    @XmlElement
    public int getHousePrice()
    {
        return housePrice;
    }

    public void setHousePrice(int housePrice)
    {
        this.housePrice = housePrice;
    }

    @XmlElement
    public int getBaseNightPayment()
    {
        return baseNightPayment;
    }

    public void setBaseNightPayment(int baneNightPayment)
    {
        this.baseNightPayment = baneNightPayment;
    }

    @XmlElement
    public int getPerHousePayment()
    {
        return perHousePayment;
    }

    public void setPerHousePayment(int perHousePayment)
    {
        this.perHousePayment = perHousePayment;
    }

    @XmlElement
    public int getId()
    {
        return id;
    }

}
