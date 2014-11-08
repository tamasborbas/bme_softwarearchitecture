package hu.bme.aut.monopoly.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;


@Entity
@NamedQueries({

        @NamedQuery(name = "User.getUserByEmailAndPass", query = "SELECT u from User u WHERE u.email like :emailPattern AND u.passwordHash like :passHashPattern"),

        @NamedQuery(name = "User.getUserByEmail", query = "SELECT u from User u WHERE u.email like :emailPattern") })
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private String email;
    private String passwordHash;
    private String name;
    @Enumerated(EnumType.STRING)
    private UserType userType;
    private List<Player> gamePlayers = new ArrayList<Player>();

    public int getId()
    {
        return id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public UserType getUserType()
    {
        return userType;
    }

    public void setUserType(UserType userType)
    {
        this.userType = userType;
    }

    @OneToMany
    public List<Player> getGamePlayers()
    {
        return gamePlayers;
    }

    public void setGamePlayers(List<Player> players)
    {
        this.gamePlayers = players;
    }

    public void addGamePlayer(Player player)
    {
        this.gamePlayers.add(player);
    }
}
