package de.uni_potsdam.hpi.bpt.bp2014.jcomparser.json;

import de.uni_potsdam.hpi.bpt.bp2014.AbstractDatabaseDependentTest;
import de.uni_potsdam.hpi.bpt.bp2014.database.DbObject;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class EventTypeTest {
    @After
    public void removeEventType() throws IOException, SQLException {
        AbstractDatabaseDependentTest.resetDatabase();
    }

    @Test
    public void testEventType() throws JAXBException {
        File file = new File("src/test/resources/EventScenarios/EventTypeScenario.json");
        try {
            String json = FileUtils.readFileToString(file);
            ScenarioData scenario = new ScenarioData(json);
            scenario.save();

            // Test saving of event type
            String sql = "SELECT * FROM eventtype WHERE scenario_id = 0";
            DbObject dbObject = new DbObject();
            String dbName = dbObject.executeStatementReturnsString(sql, "name");
            assertEquals(dbName, "Customer");

            // get the id of the event type
            int eventTypeId = dbObject.executeStatementReturnsInt(sql, "id");


            // Test saving of attributes
            String sql2 = "SELECT * FROM eventtypeattribute WHERE eventtype_id = " + eventTypeId;
            String dbAttributeName = dbObject.executeStatementReturnsString(sql2, "name");
            assertEquals(dbAttributeName, "Name");

        } catch (IOException e) {
            e.printStackTrace();
            assert(false);
        }
    }

}
