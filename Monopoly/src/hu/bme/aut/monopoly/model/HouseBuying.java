package hu.bme.aut.monopoly.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement(name = "HouseBuying")
public class HouseBuying implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private int buyedHouseNumber;
    private BuildingPlace forBuilding;

    @XmlElement
    public int getBuyedHouseNumber()
    {
        return buyedHouseNumber;
    }

    public void setBuyedHouseNumber(int buyedHouseNumber)
    {
        this.buyedHouseNumber = buyedHouseNumber;
    }

    @XmlElement
    public BuildingPlace getForBuilding()
    {
        return forBuilding;
    }

    public void setForBuilding(BuildingPlace forBuilding)
    {
        this.forBuilding = forBuilding;
    }

    @XmlElement
    public int getId()
    {
        return id;
    }

}
