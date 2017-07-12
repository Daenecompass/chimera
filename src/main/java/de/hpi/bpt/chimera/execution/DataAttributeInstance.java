package de.hpi.bpt.chimera.execution;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import de.hpi.bpt.chimera.jcore.eventhandling.SseNotifier;
import de.hpi.bpt.chimera.model.datamodel.DataAttribute;

public class DataAttributeInstance {
	private DataAttribute dataAttribute;
	private Object value;

	public DataAttributeInstance(DataAttribute attribute) {
		this.setDataAttribute(attribute);
	}

	public DataAttribute getDataAttribute() {
		return dataAttribute;
	}

	public void setDataAttribute(DataAttribute dataAttribute) {
		this.dataAttribute = dataAttribute;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		// TODO: implement an independent version for the type of an value of an
		// data attribute
		Class<? extends Object> clazz = dataAttribute.getType().getClass();
		if (clazz.isInstance(value)) {
			this.value = value;
		} else {
			String errorMsg = String.format("Could not set value of DataAttribute %s. Expected: %s, Received: %s", dataAttribute.getName(), dataAttribute.getType().getClass().getName(), value.getClass().getName());
			throw new IllegalArgumentException(errorMsg);
		}

	}

	// Copied from old version
	// TODO: look if could be maked better
	// TODO: put this in DataAttribute in CaseModel
	private void validateValueType(String value) {
		// TODO notify frontend when value is wrong
		String excp = "Could not set data attribute value " + "because it did not have the correct data type.";
		try {
			switch (dataAttribute.getType()) {
			case "Integer":
				Integer.valueOf(value);
				break;
			case "Double":
				Double.valueOf(value);
				break;
			case "Boolean":
				if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
					throw new IllegalArgumentException(excp);
				}
				break;
			case "Date":
				new SimpleDateFormat("dd.MM.yyyy").parse(value);
				break;
			case "String":
			case "Enum":
			case "Class":
				break;
			default:
				throw new IllegalArgumentException("Attribute data type is not supported.");
			}
		} catch (IllegalArgumentException | ParseException e) {
			SseNotifier.notifyWarning("Data attribute " + dataAttribute.getName() + " could not be set " + "because the entered value did not match its data type.");
			throw new IllegalArgumentException(excp);
		}
	}


}