package hu.bme.aut.monopoly.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.NamedQueries;


@Entity
@XmlRootElement(name = "Place")
@NamedQueries({
		
	@NamedQuery(name = "Place.getPlaceById", query = "SELECT p FROM Place p WHERE p.id =:idPattern"),
//	Ez itt nem jó, mert nagyon, nagyon sok place-nek lehet ugyanaz a sequence numberje. Küldjek inkább id-t?
	@NamedQuery(name = "Place.getPlaceByPlaceSequenceNumber", query = "SELECT p FROM Place p WHERE p.placeSequenceNumber=:placeSequenceNumberPattern") 
})
public class Place implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Game game;
    private int placeSequenceNumber;

    @XmlElement
    @ManyToOne
    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    @XmlElement
    public int getId()
    {
        return id;
    }

    @XmlElement
    public int getPlaceSequenceNumber()
    {
        return placeSequenceNumber;
    }

    public void setPlaceSequenceNumber(int placeSequenceNumber)
    {
        this.placeSequenceNumber = placeSequenceNumber;
    }

}
