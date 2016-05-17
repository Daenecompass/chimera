package de.uni_potsdam.hpi.bpt.bp2014.jcore.flowbehaviors;

import de.uni_potsdam.hpi.bpt.bp2014.database.controlnodes.DbBoundaryEvent;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.controlnodes.ActivityInstance;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.ScenarioInstance;

/**
 *
 */
public class BoundaryEventOutgoingBehavior extends EventOutgoingBehavior {
    public BoundaryEventOutgoingBehavior(int controlNodeId, ScenarioInstance scenarioInstance,
                                 int fragmentInstanceId, int controlNodeInstanceId) {
        super(controlNodeId, scenarioInstance, fragmentInstanceId, controlNodeInstanceId);
    }

    @Override public void terminate() {
        DbBoundaryEvent boundaryEvent = new DbBoundaryEvent();
        int attachedControlNode = boundaryEvent.getControlNodeAttachedToEvent(
                this.getControlNodeId(), this.getFragmentInstanceId());
        ScenarioInstance scenario = this.getScenarioInstance();

        scenario.getControlNodeInstances();
        ActivityInstance attachedActivity = (ActivityInstance) scenario.getControlNodeInstanceWithId(
                attachedControlNode);
        attachedActivity.cancel();

        super.terminate();
    }

}
