package de.uni_potsdam.hpi.bpt.bp2014.jcore;

import de.uni_potsdam.hpi.bpt.bp2014.database.DataObject;
import de.uni_potsdam.hpi.bpt.bp2014.database.DbDataFlow;
import de.uni_potsdam.hpi.bpt.bp2014.database.DbDataNode;

import java.util.LinkedList;
import java.util.Map;

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
			if (dataAttributeInstance.getDataObjectInstance().getDataObjectId()
					== dataObject.getId()) {
				return true;
			}
		}
		return false;
	}

	@Override public void setDataAttributeValues(Map<Integer, String> values) {
		for (Integer i : values.keySet()) {
			DataAttributeInstance dataAttributeInstance = getScenarioInstance()
					.getDataAttributeInstances().get(i);
			dataAttributeInstance.setValue(values.get(i));
		}
		this.setCanTerminate(true);
	}

}
