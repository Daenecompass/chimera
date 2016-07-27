package de.hpi.bpt.chimera.jcore.controlnodes;

import de.hpi.bpt.chimera.jcore.ScenarioInstance;
import de.hpi.bpt.chimera.jcore.flowbehaviors.BoundaryEventOutgoingBehavior;
import de.hpi.bpt.chimera.jcore.flowbehaviors.EventOutgoingBehavior;

/**
 *
 */
public class BoundaryEvent extends AbstractEvent {

    /**
     * Create new BoundaryEvent instance
     *
     * @param controlNodeId id of the abstract control node which represents the event.
     * @param fragmentInstanceId databaseId of the Fragment where start event belongs to.
     * @param scenarioInstance ScenarioInstance object which holds control nodes.
     */
    public BoundaryEvent(int controlNodeId, int fragmentInstanceId,
                      ScenarioInstance scenarioInstance) {
        super(controlNodeId, fragmentInstanceId, scenarioInstance);
    }

public BoundaryEvent(int controlNodeId, int fragmentInstanceId,
                     ScenarioInstance scenarioInstance, int controlNodeInstanceId) {
    super(controlNodeId, fragmentInstanceId, scenarioInstance, controlNodeInstanceId);
}

    @Override
    public String getType() {
        return "BoundaryEvent";
    }


    @Override
    protected EventOutgoingBehavior createOutgoingBehavior() {
        return new BoundaryEventOutgoingBehavior(this.getControlNodeId(),
                scenarioInstance, getFragmentInstanceId(), getControlNodeInstanceId());
    }


}
