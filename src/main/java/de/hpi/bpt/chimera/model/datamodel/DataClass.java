package de.hpi.bpt.chimera.model.datamodel;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class DataClass extends DataModelClass {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int dbId;
	
	@OneToOne(cascade = CascadeType.PERSIST)
	ObjectLifecycle objectLifecycle;

	public void setObjectLifecycle(ObjectLifecycle objectLifecycle) {
		this.objectLifecycle = objectLifecycle;
	}

	public ObjectLifecycle getObjectLifecycle() {
		return this.objectLifecycle;
	}

	public Map<String, ObjectLifecycleState> getNameToObjectLifecycleState() {
		Map<String, ObjectLifecycleState> nameToObjectLifecycleState = new HashMap<>();

		for (ObjectLifecycleState objectLifecycleState : this.objectLifecycle.getObjectLifecycleStates()) {
			nameToObjectLifecycleState.put(objectLifecycleState.getName(), objectLifecycleState);
		}
		return nameToObjectLifecycleState;
	}
}