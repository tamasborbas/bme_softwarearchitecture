package hu.bme.aut.monopoly.model;

import javax.persistence.Entity;


@Entity
public class BuildingPlace extends Place
{
    private int houseNumber;
    private Participant ownerPlayer;
    private Building building;

    public int getHouseNumber()
    {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber)
    {
        this.houseNumber = houseNumber;
    }

    public Participant getOwnerPlayer()
    {
        return ownerPlayer;
    }

    public void setOwnerPlayer(Participant ownerPlayer)
    {
        this.ownerPlayer = ownerPlayer;
    }

    public Building getBuilding()
    {
        return building;
    }

    public void setBuilding(Building building)
    {
        this.building = building;
    }
}
