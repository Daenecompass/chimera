package de.hpi.bpt.chimera.jcomparser.json;

import de.hpi.bpt.chimera.jcomparser.saving.Connector;
import de.hpi.bpt.chimera.jcomparser.jaxb.DataNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.*;

/**
 * This class represents a DataClass.
 */
public class DataClass {
    private static Logger logger = Logger.getLogger(DataClass.class);

    /**
     * This is the databaseID of the dataClass.
     */
    protected int databaseId;

    /**
     * This is the name of the dataClass.
     */
    protected String name;

    /**
     * This is a list containing all attributes belonging to this dataClass.
     */
    protected List<DataAttribute> attributes = new ArrayList<>();
    /**
     * This contains the JSON representation of the dataClass.
     */
    protected JSONObject jsonRepresentation;
    /**
     * This contains the OLC of the dataClass (if one exists).
     */
    protected Olc olc;

    protected int isEvent = 0;


    protected Map<String, Integer> stateToDatabaseId = new HashMap<>();

    protected List<String> states = new ArrayList<>();

    protected List<DataNode> dataNodes = new ArrayList<>();

    public DataClass(final String element) {
        try {
            this.jsonRepresentation = new JSONObject(element);
            this.name = this.jsonRepresentation.getString("name");
            if (!StringUtils.isAlphanumericSpace(name)) {
                String errorMsg = "%s is not a valid data class name";
                throw new IllegalArgumentException(String.format(errorMsg, name));
            }
            generateDataAttributeList(this.jsonRepresentation.getJSONArray("attributes"));
            if (this.jsonRepresentation.has("olc")) {
                this.olc = new Olc(this.jsonRepresentation.get("olc").toString());
                this.states = this.olc.getStateNames();
            }
        } catch (JSONException e) {
            logger.trace(e);
            throw new JSONException("Invalid class json");
        }
    }

    /**
     * This method saves the dataClass and all associated classes to the database.
     * Classes associated with the data class are state and data nodes.
     *
     * @param scenarioId Database Id of the scenario
     * @return the databaseID of the dataClass.
     */
    public int save(int scenarioId) {
        Connector conn = new Connector();
        this.databaseId = conn.insertDataClass(this.name, this.isEvent);
        saveDataAttributes();
        Connector connector = new Connector();
        for (String state : this.states) {
            int stateID = connector.insertState(state, this.databaseId);
            stateToDatabaseId.put(state, stateID);
        }

        saveDataNodes(scenarioId);
        return databaseId;
    }

    /**
     * This method gets all the attributes from the JSON.
     * DataAttributes can only be alphanumerical.
     *
     * @param jsonAttributes This JSONArray contains all attributes from the JSON.
     */
    protected void generateDataAttributeList(JSONArray jsonAttributes) {
        int length = jsonAttributes.length();
        for (int i = 0; i < length; i++) {
            DataAttribute dataAttribute = new DataAttribute(
                jsonAttributes.getJSONObject(i).getString("name"),
                jsonAttributes.getJSONObject(i).getString("datatype"),
                jsonAttributes.getJSONObject(i).getString("_id")
            );
            this.attributes.add(dataAttribute);
        }
    }


    private void saveDataAttributes() {
        for (DataAttribute dataAttribute : this.getAttributes()) {
            dataAttribute.setDataClassId(databaseId);
            dataAttribute.save();
        }
    }

    private void saveDataNodes(int scenarioId) {
        dataNodes.forEach(x -> x.save(this, scenarioId));
    }

    public void addDataNode(DataNode dataNode) {
        this.dataNodes.add(dataNode);
    }


    public Optional<DataAttribute> getDataAttributeByName(String attributeName) {
        for (DataAttribute attribute : this.getAttributes()) {
            if (attribute.getName().equals(attributeName)) {
                return Optional.of(attribute);
            }
        }
        return Optional.empty();
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public String getName() {
        return name;
    }

    public List<DataAttribute> getAttributes() {
        return attributes;
    }

    public JSONObject getJsonRepresentation() {
        return jsonRepresentation;
    }

    public Map<String, Integer> getStateToDatabaseId() {
        return stateToDatabaseId;
    }

    public void setStateToDatabaseId(Map<String, Integer> stateToDatabaseId) {
        this.stateToDatabaseId = stateToDatabaseId;
    }

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
        this.states = states;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Olc getOlc() {
        return this.olc;
    }

    public void setOlc(Olc olc) {
        this.olc = olc;
    }

    public void setAttributes(List<DataAttribute> attributes) {
        this.attributes = attributes;
    }
}
