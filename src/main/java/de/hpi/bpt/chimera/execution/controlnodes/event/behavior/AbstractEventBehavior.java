package de.hpi.bpt.chimera.execution.controlnodes.event.behavior;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import de.hpi.bpt.chimera.execution.controlnodes.Behaving;
import de.hpi.bpt.chimera.execution.controlnodes.event.AbstractEventInstance;

@Entity
public class AbstractEventBehavior implements Behaving {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	int dbId;

	@OneToOne(cascade = CascadeType.ALL)
	private AbstractEventInstance eventInstance;


	/**
	 * for JPA only
	 */
	public AbstractEventBehavior() {
		// JPA needs an empty constructor to instantiate objects of this class
		// at runtime.
	}


	public AbstractEventBehavior(AbstractEventInstance eventInstance) {
		setEventInstance(eventInstance);
	}

	/**
	 * Automatic begin.
	 */
	@Override
	public void enableControlFlow() {
		getEventInstance().begin();
	}

	/**
	 * Automatic termination.
	 */
	@Override
	public void begin() {
		// no special behavior
		getEventInstance().terminate();
	}

	@Override
	public void terminate() {
		// no special behavior
	}

	@Override
	public void skip() {
		// no special behavior
	}

	public AbstractEventInstance getEventInstance() {
		return eventInstance;
	}

	public void setEventInstance(AbstractEventInstance eventInstance) {
		this.eventInstance = eventInstance;
	}
}
