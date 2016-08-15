package de.hpi.bpt.chimera.jcore.controlnodes;

import de.hpi.bpt.chimera.jcore.ScenarioInstance;
import de.hpi.bpt.chimera.jcore.flowbehaviors.ExclusiveGatewaySplitBehavior;
import de.hpi.bpt.chimera.database.controlnodes.DbControlNode;
import de.hpi.bpt.chimera.database.controlnodes.DbControlNodeInstance;
import de.hpi.bpt.chimera.database.controlnodes.DbGatewayInstance;

/**
 * Represents a gateway instance.
 * TODO review this class
 */
public class GatewayInstance extends AbstractControlNodeInstance {
	/**
	 * Database ConnectionWrapper objects.
	 */
	protected final DbControlNodeInstance dbControlNodeInstance = new DbControlNodeInstance();
	protected final DbControlNode dbControlNode = new DbControlNode();
	protected final DbGatewayInstance dbGatewayInstance = new DbGatewayInstance();
	protected GatewayType type;
	protected boolean automaticExecution;

	/**
	 * Creates and initializes a new gateway instance.
	 * Creates a new entry in the database for the new gateway instance.
	 *
	 * @param controlNodeId      This is the id of the control node.
	 * @param fragmentInstanceId This is the id of the fragment instance.
	 * @param scenarioInstance    This is an instance from the class ScenarioInstance.
	 */
	public GatewayInstance(int controlNodeId, int fragmentInstanceId,
			ScenarioInstance scenarioInstance) {
        this.automaticExecution = true;
		this.scenarioInstance = scenarioInstance;
		this.setControlNodeId(controlNodeId);
		this.setFragmentInstanceId(fragmentInstanceId);
        this.setState(State.INIT);
	}

	/**
	 * Creates and initializes a new gateway instance.
	 * Reads the information for an existing gateway instance from the database.
	 *
	 * @param controlNodeId      This is the id of the control node.
	 * @param fragmentInstanceId This is the id of the fragment instance.
	 * @param scenarioInstance    This is an instance from the class ScenarioInstance.
	 * @param instanceId         This is an id of the gateway instance.
	 */
	public GatewayInstance(int controlNodeId, int fragmentInstanceId,
			ScenarioInstance scenarioInstance, int instanceId) {
		this.automaticExecution = true;
		this.scenarioInstance = scenarioInstance;
		this.setControlNodeId(controlNodeId);
        this.setControlNodeInstanceId(instanceId);
		this.setFragmentInstanceId(fragmentInstanceId);
        this.setState(dbGatewayInstance.getState(instanceId));
    }

	/**
	 * Checks if the gateway can terminate.
	 *
	 * @param controlNodeId A control node id.
	 * @return true if the gateway can terminate
	 */
	public boolean checkTermination(int controlNodeId) {
		return ((ExclusiveGatewaySplitBehavior) getOutgoingBehavior())
				.checkTermination(controlNodeId);
	}

	@Override public void terminate() {
        setState(State.TERMINATED);
        dbGatewayInstance.setState(this.getControlNodeInstanceId(), getState().toString());
		getOutgoingBehavior().terminate();
	}

	@Override
    public void skip() {
        setState(State.SKIPPED);
        dbGatewayInstance.setState(this.getControlNodeInstanceId(), getState().toString());
        getScenarioInstance().getExecutingGateways().remove(this);
	}

    @Override
    public void begin() {
        setState(State.EXECUTING);
        dbGatewayInstance.setState(
                this.getControlNodeInstanceId(), getState().toString());
    }


	// ******************************* Getter & Setter ***************************//

	public GatewayType getType() {
		return this.type;
	}

	/**
	 * @return boolean isAutomaticExecution.
	 */
	public boolean isAutomaticExecution() {
		return automaticExecution;
	}

	/**
	 * @param automaticExecution a automatic execution state.
	 */
	public void setAutomaticExecution(boolean automaticExecution) {
		this.automaticExecution = automaticExecution;
	}
}
