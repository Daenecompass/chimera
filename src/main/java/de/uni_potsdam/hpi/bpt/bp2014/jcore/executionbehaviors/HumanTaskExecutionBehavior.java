package de.uni_potsdam.hpi.bpt.bp2014.jcore.executionbehaviors;

import de.uni_potsdam.hpi.bpt.bp2014.database.DataObject;
import de.uni_potsdam.hpi.bpt.bp2014.database.data.DbDataFlow;
import de.uni_potsdam.hpi.bpt.bp2014.database.data.DbDataNode;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.data.DataAttributeInstance;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.ScenarioInstance;
import de.uni_potsdam.hpi.bpt.bp2014.jcore.controlnodes.AbstractControlNodeInstance;

import java.util.LinkedList;

/**
 * This class specifies the human task execution behavior.
 */
public class HumanTaskExecutionBehavior extends TaskExecutionBehavior {

	/**
	 *
	 * @param activityInstanceId This is an activty instance id.
	 * @param scenarioInstance	  This is a scenario instance.
	 * @param controlNodeInstance This is a control node instance.
	 */
	public HumanTaskExecutionBehavior(int activityInstanceId, ScenarioInstance scenarioInstance,
			AbstractControlNodeInstance controlNodeInstance) {
		super(activityInstanceId, scenarioInstance, controlNodeInstance);
	}

	@Override public void execute() {
		DbDataFlow dbDataFlow = new DbDataFlow();
		//allow an activity to terminate if it has no data attributes in output.
		if (dbDataFlow.getOutputSetsForControlNode(
				getControlNodeInstance().getControlNodeId())
				.isEmpty()) {
			this.setCanTerminate(true);
		} else if (getScenarioInstance().getDataAttributeInstances().isEmpty()) {
			this.setCanTerminate(true);
		} else {
			LinkedList<Integer> outputSets = dbDataFlow.getOutputSetsForControlNode(
					getControlNodeInstance().getControlNodeId());
			int outputSet = outputSets.getFirst();
			DbDataNode dbDataNode = new DbDataNode();
			LinkedList<DataObject> dataObjects = dbDataNode
					.getDataObjectsForDataSets(outputSet);
			boolean hasAttribute = false;
			for (DataObject dataObject : dataObjects) {
				if (this.dataObjectHasAttributes(dataObject)) {
					hasAttribute = true;
					break;
				}
			}
			if (!hasAttribute) {
				this.setCanTerminate(true);
			}
		}
	}

	private boolean dataObjectHasAttributes(DataObject dataObject) {
		for (DataAttributeInstance dataAttributeInstance : getScenarioInstance()
				.getDataAttributeInstances().values()) {
			if (dataAttributeInstance.getDataObject().getDataClassId()
					== dataObject.getId()) {
				return true;
			}
		}
		return false;
	}

}
