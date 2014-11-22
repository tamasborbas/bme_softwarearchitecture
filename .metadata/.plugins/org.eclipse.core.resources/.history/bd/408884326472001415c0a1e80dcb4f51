package hu.bme.aut.monopoly.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement(name = "Step")
public class Step implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int money;
    private BuildingPlace buyedBuilding;
    private BuildingPlace soldBuilding;
    private Place finishPlace;
    private List<HouseBuying> houseBuyings = new ArrayList<HouseBuying>();

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
    public BuildingPlace getBuyedBuilding()
    {
        return buyedBuilding;
    }

    public void setBuyedBuilding(BuildingPlace buyedBuilding)
    {
        this.buyedBuilding = buyedBuilding;
    }

    @XmlElement
    public BuildingPlace getSoldBuilding()
    {
        return soldBuilding;
    }

    public void setSoldBuilding(BuildingPlace soldBuilding)
    {
        this.soldBuilding = soldBuilding;
    }

    @XmlElement
    public List<HouseBuying> getHouseBuyings()
    {
        return houseBuyings;
    }

    public void setHouseBuyings(List<HouseBuying> houseBuyings)
    {
        this.houseBuyings = houseBuyings;
    }

    @XmlElement
    public int getId()
    {
        return id;
    }

    @XmlElement
    public Place getFinishPlace()
    {
        return finishPlace;
    }

    public void setFinishPlace(Place finishPlace)
    {
        this.finishPlace = finishPlace;
    }

}
