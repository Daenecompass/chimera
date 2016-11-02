package de.hpi.bpt.chimera;

import de.hpi.bpt.chimera.database.controlnodes.events.DbEventMapping;
import de.hpi.bpt.chimera.jcomparser.json.ScenarioData;
import de.hpi.bpt.chimera.jcore.ScenarioInstance;
import de.hpi.bpt.chimera.jcore.controlnodes.ActivityInstance;
import de.hpi.bpt.chimera.jcore.controlnodes.AbstractControlNodeInstance;
import org.apache.commons.io.FileUtils;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * This class should be used to hold helper methods which are needed in tests
 * which are concerned with scenarios.
 */
public class ScenarioTestHelper {
    // The class contains only static methods so there should be no public default constructor
    private ScenarioTestHelper() {
    }

    /**
     * Loads a scenario Json from file, and returns the initialized scenario instance
     * @param path FilePath to an scenario json.
     * @return An initialized scenario instance
     * @throws IOException
     */
    public static ScenarioInstance createScenarioInstance(String path) throws IOException {
        File file = new File(path);
        String json = FileUtils.readFileToString(file);
        try {
            ScenarioData scenario  = new ScenarioData(json);
            int databaseId = scenario.save();
            return new ScenarioInstance(databaseId);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    public static Optional<AbstractControlNodeInstance> findActivityInstanceInScenario(
            String name, ScenarioInstance scenarioInstance) {
        List<AbstractControlNodeInstance> controlNodeInstances =
                scenarioInstance.getEnabledControlNodeInstances();

        return controlNodeInstances.stream()
                .filter(ActivityInstance.class::isInstance)
                .filter(a -> ((ActivityInstance) a).getLabel().equals(name))
                .findFirst();
    }

    public static ActivityInstance findActivityInstanceInNodes(
            String activityName, List<AbstractControlNodeInstance> controlNodeInstances) {
        return searchActivityFromList(activityName, controlNodeInstances);
    }

    private static ActivityInstance searchActivityFromList(
            String activityName,
            List<AbstractControlNodeInstance> controlNodeInstances) {
        Optional<AbstractControlNodeInstance> instanceOptional = controlNodeInstances.stream()
                .filter(ActivityInstance.class::isInstance)
                .filter(a -> ((ActivityInstance) a).getLabel().equals(activityName))
                .findFirst();
        if (!instanceOptional.isPresent()) {
            throw new IllegalArgumentException("Invalid activity name");
        }
        return (ActivityInstance) instanceOptional.get();
    }


    public static void beginActivityByName(String name, ScenarioInstance scenarioInstance) {
        ActivityInstance activity = findActivityInstanceInNodes(
                name, scenarioInstance.getEnabledControlNodeInstances());
        activity.begin();
    }

    public static void terminateActivityByName(String activityName, ScenarioInstance scenarioInstance) {
        ActivityInstance activity = findActivityInstanceInNodes(
                activityName, scenarioInstance.getRunningControlNodeInstances());
        activity.terminate();
    }

    public static void executeActivityByName(String activityName, ScenarioInstance scenarioInstance) {
        beginActivityByName(activityName, scenarioInstance);
        terminateActivityByName(activityName, scenarioInstance);
    }


    /**
     *
     */
    public static boolean isActivityActivated(String activityName, ScenarioInstance scenarioInstance) {
        Optional<AbstractControlNodeInstance> activity = findActivityInstanceInScenario(
                activityName, scenarioInstance);
        return activity.isPresent();
    }

    public static int triggerEventInScenario(
            ScenarioInstance scenarioInstance, WebTarget base) {
        return triggerEventInScenario(scenarioInstance, base, "{}");
    }

    public static int triggerEventInScenario(
            ScenarioInstance scenarioInstance, WebTarget base, String body) {
        int scenarioId = scenarioInstance.getScenarioId();
        int scenarioInstanceId = scenarioInstance.getId();
        List<String> registeredEventKeys = scenarioInstance.getRegisteredEventKeys();
        String registeredEvent = registeredEventKeys.get(0);
        String route = String.format("scenario/%d/instance/%d/events/%s", scenarioId,
                scenarioInstanceId, registeredEvent);

        DbEventMapping eventMapping = new DbEventMapping();
        int eventNodeId = eventMapping.getEventControlNodeId(registeredEvent);

        base.path(route).request().post(Entity.json(body));
        return eventNodeId;
    }
}