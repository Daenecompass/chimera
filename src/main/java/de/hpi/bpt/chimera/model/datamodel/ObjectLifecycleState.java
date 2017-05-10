package de.hpi.bpt.chimera.model.datamodel;

import java.util.ArrayList;
import java.util.List;

public class ObjectLifecycleState {
	String name;
	List<ObjectLifecycleState> successors;
	List<ObjectLifecycleState> predecessors;

	public ObjectLifecycleState() {
		this.successors = new ArrayList<>();
		this.predecessors = new ArrayList<>();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setSuccessors(List<ObjectLifecycleState> successors) {
		this.successors = successors;
	}

	public List<ObjectLifecycleState> getSuccessors() {
		return this.successors;
	}

	public void setPredecessors(List<ObjectLifecycleState> predecessors) {
		this.predecessors = predecessors;
	}

	public List<ObjectLifecycleState> getPredecessors() {
		return this.predecessors;
	}

	public void addSuccessor(ObjectLifecycleState successor) {
		successors.add(successor);
	}

	public void addPredecessor(ObjectLifecycleState predecessor) {
		predecessors.add(predecessor);
	}
}
