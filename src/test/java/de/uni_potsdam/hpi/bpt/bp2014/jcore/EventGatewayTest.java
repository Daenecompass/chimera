package de.uni_potsdam.hpi.bpt.bp2014.jcore;

import de.uni_potsdam.hpi.bpt.bp2014.AbstractDatabaseDependentTest;
import de.uni_potsdam.hpi.bpt.bp2014.ScenarioTestHelper;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.eventhandling.EventDispatcher;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class EventGatewayTest extends JerseyTest {
    WebTarget base;

    @Override
    protected Application configure() {
        return new ResourceConfig(EventDispatcher.class);
    }

    @After
    public void teardown() throws IOException, SQLException {
        AbstractDatabaseDependentTest.resetDatabase();
    }

    @Before
    public void setUpBase() {
        base = target("eventdispatcher");
    }

    @Test
    public void testBoundaryDisablementWhenTerminatingActivity() {
        String path = "src/test/resources/Scenarios/EventGatewayScenario.json";
        try {
            ScenarioInstance scenarioInstance = ScenarioTestHelper.createScenarioInstance(path);
            List<String> registeredEvents = scenarioInstance.getRegisteredEventKeys();
            assertEquals(3, registeredEvents.size());
            ScenarioTestHelper.triggerEventInScenario(scenarioInstance, base, "");
            registeredEvents = scenarioInstance.getRegisteredEventKeys();
            assertEquals(0, registeredEvents.size());
        } catch (IOException e) {
            assert(false);
            e.printStackTrace();
        }
    }

    // Test whether all outgoing events are initialized from event based gateway
    @Test
    public void testEventEnablement() {
        String path = "src/test/resources/Scenarios/EventGatewayScenario.json";
        try {
            ScenarioInstance scenarioInstance = ScenarioTestHelper.createScenarioInstance(path);
            List<String> registeredEvents = scenarioInstance.getRegisteredEventKeys();
            assertEquals(3, registeredEvents.size());
        } catch (IOException e) {
            assert(false);
            e.printStackTrace();
        }

    }
}
