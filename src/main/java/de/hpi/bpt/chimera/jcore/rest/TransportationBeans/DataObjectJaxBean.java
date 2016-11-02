package de.hpi.bpt.chimera.jcore.rest.TransportationBeans;

import de.hpi.bpt.chimera.jcore.ExecutionService;
import de.hpi.bpt.chimera.jcore.data.DataObject;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A JAX bean which is used for dataobject data.
 * It contains the data of one dataobject.
 * It can be used to create a JSON Object
 */
@XmlRootElement
public class DataObjectJaxBean {
    /**
     * The label of the data object.
     */
    private String label;

    /**
     * The id the dataobject (not the instance) has inside
     * the database.
     */
    private int id;
    /**
     * The state inside the database of the dataobject
     * which is stored in the table.
     * The label not the id will be saved.
     */
    private String state;

    /**
     * An array of all dataAttributes belonging to this dataObject.
     * Each attribute has an id, name, type and value.
     */
    private DataAttributeJaxBean[] attributeConfiguration;

    public DataObjectJaxBean() {};

    public DataObjectJaxBean(DataObject dataObject) {
        this.setId(dataObject.getId());
        this.setLabel(dataObject.getName());
        this.setState(dataObject.getStateName());
    }

    // TODO does this really need the execution service
    public DataObjectJaxBean (DataObject dataObjectInstance, ExecutionService executionService) {
        this.setId(dataObjectInstance.getId());
        this.setLabel(dataObjectInstance.getName());
        this.setState(dataObjectInstance.getStateName());
        this.setAttributeConfiguration(executionService
                .getDataAttributesForDataObjectInstance(dataObjectInstance));
    }



    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public DataAttributeJaxBean[] getAttributeConfiguration() {
        return attributeConfiguration;
    }

    public void setAttributeConfiguration(DataAttributeJaxBean[] attributeConfiguration) {
        this.attributeConfiguration = attributeConfiguration;
    }
}
