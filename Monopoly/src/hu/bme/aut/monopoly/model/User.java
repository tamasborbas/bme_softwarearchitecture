package hu.bme.aut.monopoly.model;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@NamedQueries({

        @NamedQuery(name = "User.getUserByEmailAndPass", query = "SELECT u from User u WHERE u.email like :emailPattern AND u.passwordHash like :passHashPattern"),

        @NamedQuery(name = "User.getUserByEmail", query = "SELECT u from User u WHERE u.email like :emailPattern"),
        @NamedQuery(name = "User.getUserByName", query = "SELECT u from User u WHERE u.name like :namePattern") })
@XmlRootElement(name = "User")
public class User implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private String email;
    private String passwordHash;
    private String name;
    @Enumerated(EnumType.STRING)
    private UserType userType;
    private List<Player> gamePlayers = new ArrayList<Player>();

    @XmlElement
    public int getId()
    {
        return id;
    }

    @XmlElement
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @XmlElement
    public String getPasswordHash()
    {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }

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
    public UserType getUserType()
    {
        return userType;
    }

    public void setUserType(UserType userType)
    {
        this.userType = userType;
    }

    @XmlElement
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
