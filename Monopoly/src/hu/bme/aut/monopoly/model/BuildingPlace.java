package hu.bme.aut.monopoly.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement(name = "BuildingPlace")
public class BuildingPlace extends Place implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int houseNumber;
    private Player ownerPlayer;
    private Building building;

    @XmlElement
    public int getHouseNumber()
    {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber)
    {
        this.houseNumber = houseNumber;
    }

    @XmlElement
    @ManyToOne
    public Player getOwnerPlayer()
    {
        return ownerPlayer;
    }

    public void setOwnerPlayer(Player ownerPlayer)
    {
        this.ownerPlayer = ownerPlayer;
    }

    @XmlElement
    public Building getBuilding()
    {
        return building;
    }

    public void setBuilding(Building building)
    {
        this.building = building;
    }
}
