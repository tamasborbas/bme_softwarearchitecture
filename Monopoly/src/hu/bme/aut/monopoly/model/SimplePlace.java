package hu.bme.aut.monopoly.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement(name = "SimplePlace")
public class SimplePlace extends Place implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
