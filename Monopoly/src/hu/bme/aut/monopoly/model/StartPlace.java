package hu.bme.aut.monopoly.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement(name = "StartPlace")
@NamedQueries({
        @NamedQuery(name = "StartPlace.getStartPlaceById", query = "SELECT p FROM StartPlace p WHERE p.id =:idPattern"),
        @NamedQuery(name = "StartPlace.getStartPlaceByPlaceSequenceNumber", query = "SELECT p FROM StartPlace p WHERE p.placeSequenceNumber =:placeSequenceNumberPattern") })
public class StartPlace extends Place implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int throughMoney;

    @XmlElement
    public int getThroughMoney()
    {
        return throughMoney;
    }

    public void setThroughMoney(int throughMoney)
    {
        this.throughMoney = throughMoney;
    }
}
