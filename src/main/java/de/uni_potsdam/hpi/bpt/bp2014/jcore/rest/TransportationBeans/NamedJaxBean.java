package de.uni_potsdam.hpi.bpt.bp2014.jcore.rest.TransportationBeans;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A JAX bean which is used for a naming an entity.
 * Therefor a name can be transmitted.
 */
@XmlRootElement
public class NamedJaxBean {
    /**
     * The name which should be assigned to the entity.
     */
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
