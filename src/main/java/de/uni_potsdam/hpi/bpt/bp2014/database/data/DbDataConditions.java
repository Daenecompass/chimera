package de.uni_potsdam.hpi.bpt.bp2014.database.data;

import java.util.*;

/**
 *
 */
public class DbDataConditions {

    /**
     *
     * @param activityId
     */
    public Map<String, Set<String>> loadInputSets(int activityId) {
        List<Integer> inputSetIds = new DbDataFlow().getInputSetsForControlNode(activityId);
        return loadDataDependency(inputSetIds);
    }

    public Map<String, Set<String>> loadOutputSets(int activityId) {
        List<Integer> outputSetIds = new DbDataFlow()
                .getOutputSetsForControlNode(activityId);
        return loadDataDependency(outputSetIds);
    }

    private Map<String, Set<String>> loadDataDependency(List<Integer> setIds) {
        Map<Integer, Set<Integer>> inputIdToStateIds = new HashMap<>();
        DbDataNode dbDataNode = new DbDataNode();
        for (Integer inputSetId : setIds) {
            Map<Integer, Integer> inputSet = dbDataNode.getDataClassIdToState(inputSetId);
            for (Map.Entry<Integer, Integer> entry : inputSet.entrySet()) {
                if (!inputIdToStateIds.containsKey(entry.getKey())) {
                    inputIdToStateIds.put(entry.getKey(), new HashSet<>());
                }
                inputIdToStateIds.get(entry.getKey()).add(entry.getValue());
            }
        }
        return resolveNames(inputIdToStateIds);
    }
    
    /**
     *
     * @return
     */
    private Map<String, Set<String>> resolveNames(Map<Integer, Set<Integer>> inputIdToStates) {
        Map<String, Set<String>> result = new HashMap<>();
        DbDataClass dataClass = new DbDataClass();
        DbState dbState = new DbState();
        for (Map.Entry<Integer, Set<Integer>> entry : inputIdToStates.entrySet()) {
            int dataclassId = entry.getKey();
            String dataclassName = dataClass.getName(dataclassId);
            result.put(dataclassName, new HashSet<>());
            for (Integer stateId : entry.getValue()) {
                String stateName = dbState.getStateName(stateId);
                result.get(dataclassName).add(stateName);
            }
        }
        return result;
    }
}