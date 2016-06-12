package de.uni_potsdam.hpi.bpt.bp2014.jcore.eventhandling;

import de.uni_potsdam.hpi.bpt.bp2014.ScenarioTestHelper;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.ScenarioInstance;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class TimerExecutionTest {
    @After
    public void teardown() {

    }

    @Test
    public void testTimerEventExecution() {
        String path = "src/test/resources/Scenarios/TimerEventScenario.json";
        try {
            ScenarioInstance scenarioInstance = ScenarioTestHelper.createScenarioInstance(path);
            List<String> registeredEventKeys = scenarioInstance.getRegisteredEventKeys();
            assertEquals(1, registeredEventKeys.size());
            Thread.sleep(4000);
            assertEquals(0, scenarioInstance.getRegisteredEventKeys().size());
            // TODO assert also that timer event is terminated not only unregistered
        } catch (IOException e) {
            fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
