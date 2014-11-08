package hu.bme.aut.monopoly.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Step
{
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private int money;
    private BuildingPlace buyedBuilding;
    private BuildingPlace soldBuilding;
    private Place finishPlace;
    private List<HouseBuying> houseBuyings = new ArrayList<HouseBuying>();

    public int getMoney()
    {
        return money;
    }

    public void setMoney(int money)
    {
        this.money = money;
    }

    public BuildingPlace getBuyedBuilding()
    {
        return buyedBuilding;
    }

    public void setBuyedBuilding(BuildingPlace buyedBuilding)
    {
        this.buyedBuilding = buyedBuilding;
    }

    public BuildingPlace getSoldBuilding()
    {
        return soldBuilding;
    }

    public void setSoldBuilding(BuildingPlace soldBuilding)
    {
        this.soldBuilding = soldBuilding;
    }

    public List<HouseBuying> getHouseBuyings()
    {
        return houseBuyings;
    }

    public void setHouseBuyings(List<HouseBuying> houseBuyings)
    {
        this.houseBuyings = houseBuyings;
    }

    public int getId()
    {
        return id;
    }

    public Place getFinishPlace()
    {
        return finishPlace;
    }

    public void setFinishPlace(Place finishPlace)
    {
        this.finishPlace = finishPlace;
    }

}
