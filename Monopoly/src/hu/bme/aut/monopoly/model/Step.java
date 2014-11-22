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
    private BuildingPlace buyedBuilding;
    private Place finishPlace;
    private List<HouseBuying> houseBuyings = new ArrayList<HouseBuying>();
    private List<BuildingPlace> soldBuildings = new ArrayList<BuildingPlace>();

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

    @XmlElement
    public List<BuildingPlace> getSoldBuildings()
    {
        return soldBuildings;
    }

    
    public void setSoldBuildings(List<BuildingPlace> soldBuildings)
    {
        this.soldBuildings = soldBuildings;
    }
    public void addSoldBuilding(BuildingPlace soldBuilding)
    {
        this.soldBuildings.add(soldBuilding);
    }

//    public void removeSoldBuildings(BuildingPlace soldBuilding)
//    {
//        this.soldBuildings.remove(soldBuilding);
//    }

}
