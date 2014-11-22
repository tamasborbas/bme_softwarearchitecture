package hu.bme.aut.monopoly.model;

import java.util.Comparator;


public class PlayerComparator implements Comparator<Player>
{

    @Override
    public int compare(Player p1, Player p2)
    {
        return p1.getId() < p2.getId() ? -1 : p1.getId() > p2.getId() ? 1 : 0;
    }

}
