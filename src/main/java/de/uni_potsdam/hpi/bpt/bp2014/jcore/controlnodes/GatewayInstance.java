package de.uni_potsdam.hpi.bpt.bp2014.jcore.controlnodes;

import de.uni_potsdam.hpi.bpt.bp2014.database.DbControlNode;
import de.uni_potsdam.hpi.bpt.bp2014.database.DbControlNodeInstance;
import de.uni_potsdam.hpi.bpt.bp2014.database.DbGatewayInstance;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.ScenarioInstance;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.flowbehaviors.ExclusiveGatewaySplitBehavior;

/**
 * Represents a gateway instance.
 */
public class GatewayInstance extends AbstractControlNodeInstance {
	/**
	 * Database Connection objects.
	 */
	protected final DbControlNodeInstance dbControlNodeInstance = new DbControlNodeInstance();
	protected final DbControlNode dbControlNode = new DbControlNode();
	protected final DbGatewayInstance dbGatewayInstance = new DbGatewayInstance();
	protected GatewayType type;
	protected ScenarioInstance scenarioInstance;
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
		//looks if the Gateway Instance has already been initialized
		for (AbstractControlNodeInstance controlNodeInstance : scenarioInstance
				.getControlFlowEnabledControlNodeInstances()) {
			if (this.getFragmentInstanceId() == getControlNodeInstanceId()
					&& this.getControlNodeId() == controlNodeId) {
				//if it exist, only checks the control flow
				controlNodeInstance.enableControlFlow();
				return;
			}
		}
		this.automaticExecution = true;
		this.scenarioInstance = scenarioInstance;
		this.setControlNodeId(controlNodeId);
		this.setFragmentInstanceId(fragmentInstanceId);
		//scenarioInstance.getControlNodeInstances().add(this);
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
		this.setFragmentInstanceId(fragmentInstanceId);
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

	@Override public boolean terminate() {
		getStateMachine().terminate();
		getOutgoingBehavior().terminate();
		return true;
	}

	@Override public boolean skip() {
		return getStateMachine().skip();
	}

	// ******************************* Getter & Setter ***************************//

	public GatewayType getType() {
		return this.type;
	}
	/**
	 * @return ScenarioInstance.
	 */
	public ScenarioInstance getScenarioInstance() {
		return scenarioInstance;
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