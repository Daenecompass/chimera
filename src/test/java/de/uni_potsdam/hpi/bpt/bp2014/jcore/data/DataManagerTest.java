package de.uni_potsdam.hpi.bpt.bp2014.jcore.data;

import de.uni_potsdam.hpi.bpt.bp2014.AbstractDatabaseDependentTest;
import de.uni_potsdam.hpi.bpt.bp2014.database.ExampleValueInserter;
import de.uni_potsdam.hpi.bpt.bp2014.jcomparser.json.DataAttribute;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.MockProvider;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.ScenarioInstance;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class DataManagerTest {

    @After
    public void tearDown() throws IOException, SQLException {
        AbstractDatabaseDependentTest.resetDatabase();
    }

    @Test
    public void testLoadDataObjects() {
        ScenarioInstance scenarioInstance = EasyMock.createNiceMock(ScenarioInstance.class);

        final int scenarioId = 1;
        final int scenarioInstanceId = 1;
        insertExampleValues(scenarioId, scenarioInstanceId);

        expect(scenarioInstance.getScenarioId()).andReturn(scenarioId);
        expect(scenarioInstance.getScenarioInstanceId()).andReturn(scenarioInstanceId);
        replay(scenarioInstance);

        DataManager dataManager = new DataManager(scenarioInstance);
        assertEquals(2, dataManager.getDataObjects().size());
        assertEquals(1, dataManager.getDataObjects().get(0).getDataClassId());
        assertEquals(2, dataManager.getDataObjects().get(1).getDataClassId());
    }

    @Test
    public void testChangeDataObjectInstanceState() {
        Assert.fail();
    }

    @Test
    public void testGetDataobjectInstanceForId() {
        ScenarioInstance scenarioInstance = EasyMock.createNiceMock(ScenarioInstance.class);
        DataManager dataManager = new DataManager(scenarioInstance);
        DataObject first = EasyMock.createNiceMock(DataObject.class);
        expect(first.getId()).andReturn(1);
        replay(first);

        DataObject second = EasyMock.createNiceMock(DataObject.class);
        expect(second.getId()).andReturn(2);
        replay(second);

        dataManager.setDataObjects(Arrays.asList(first, second));
        Optional<DataObject> dataObject = dataManager.getDataobjectForId(2);
        assertTrue(dataObject.isPresent());
        assertEquals(second, dataObject.get());

        Optional<DataObject> nonExistingDataobject = dataManager.getDataobjectForId(3);
        assertFalse(nonExistingDataobject.isPresent());
    }

    @Test
    public void testGetAllDataAttributeInstances() {
        DataAttributeInstance attr1 = EasyMock.createNiceMock(DataAttributeInstance.class);
        DataAttributeInstance attr2 = EasyMock.createNiceMock(DataAttributeInstance.class);
        DataAttributeInstance attr3 = EasyMock.createNiceMock(DataAttributeInstance.class);

        DataObject dataObject1 = EasyMock.createNiceMock(DataObject.class);
        expect(dataObject1.getDataAttributeInstances()).andReturn(Arrays.asList(attr1, attr2));

        DataObject dataObject2 = EasyMock.createNiceMock(DataObject.class);
        expect(dataObject2.getDataAttributeInstances()).andReturn(Arrays.asList(attr3));

        ScenarioInstance scenarioInstance = EasyMock.createNiceMock(ScenarioInstance.class);
        replay(attr1, attr2, attr3, dataObject1, dataObject2, scenarioInstance);
        DataManager dataManager = new DataManager(scenarioInstance);
        dataManager.setDataObjects(Arrays.asList(dataObject1, dataObject2));
        assertEquals(Arrays.asList(attr1, attr2, attr3),
                dataManager.getAllDataAttributeInstances());
    }

    @Test
    public void testLockDataobject() {
        ScenarioInstance scenarioInstance = EasyMock.createNiceMock(ScenarioInstance.class);
        expect(scenarioInstance.getScenarioId()).andReturn(1);
        expect(scenarioInstance.getScenarioInstanceId()).andReturn(1);
        replay(scenarioInstance);
        insertExampleValues(1, 1);
        DataManager dataManager = new DataManager(scenarioInstance);
        List<DataObject> dataObjects = dataManager.getDataObjects();
        assertEquals(2, dataObjects.stream().filter(x -> !x.isLocked()).count());
        dataManager.lockDataobject(dataObjects.get(0).getId());
        assertEquals(1, dataObjects.stream().filter(x -> !x.isLocked()).count());
    }

    private void insertExampleValues(int scenarioId, int scenarioInstanceId) {
        ExampleValueInserter inserter = new ExampleValueInserter();
        int firstDataclass = 1;
        int secondDataclass = 2;
        int firstState = 1;
        int secondState = 2;
        inserter.insertDataObject(scenarioId, scenarioInstanceId, firstState, firstDataclass, false);
        inserter.insertDataObject(scenarioId, scenarioInstanceId, secondState, secondDataclass, false);

    }
}