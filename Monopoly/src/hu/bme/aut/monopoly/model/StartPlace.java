package hu.bme.aut.monopoly.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement(name = "StartPlace")
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
