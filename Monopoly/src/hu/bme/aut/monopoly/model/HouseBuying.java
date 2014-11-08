package hu.bme.aut.monopoly.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class HouseBuying
{
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private int buyedHouseNumber;
    private BuildingPlace forBuilding;

    public int getBuyedHouseNumber()
    {
        return buyedHouseNumber;
    }

    public void setBuyedHouseNumber(int buyedHouseNumber)
    {
        this.buyedHouseNumber = buyedHouseNumber;
    }

    public BuildingPlace getForBuilding()
    {
        return forBuilding;
    }

    public void setForBuilding(BuildingPlace forBuilding)
    {
        this.forBuilding = forBuilding;
    }

    public int getId()
    {
        return id;
    }

}
