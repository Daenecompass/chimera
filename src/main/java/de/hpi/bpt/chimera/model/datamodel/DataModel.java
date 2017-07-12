package de.hpi.bpt.chimera.model.datamodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class DataModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int dbId;

	private int versionNumber;
	@OneToMany(cascade = CascadeType.PERSIST)
	private List<DataModelClass> dataModelClasses;

	public int getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}

	public List<DataModelClass> getDataModelClasses() {
		return dataModelClasses;
	}

	public void setDataModelClasses(List<DataModelClass> dataModelClasses) {
		this.dataModelClasses = dataModelClasses;
	}

	public Map<String, DataModelClass> getNameToDataModelClass() {
		Map<String, DataModelClass> nameToDataModelClass = new HashMap<>();

		for (DataModelClass dataModelClass : dataModelClasses) {
			nameToDataModelClass.put(dataModelClass.getName(), dataModelClass);
		}
		return nameToDataModelClass;
	}
}