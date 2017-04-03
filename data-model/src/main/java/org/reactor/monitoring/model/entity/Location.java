package org.reactor.monitoring.model.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "Location")
public class Location extends AbstractEntity<Location>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3760568503918407263L;
	private Long id;
	private String locationName;
	private Long version;
	private Set<TestLocation> testlocations;
	private Set<Test> tests;

	/*@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		// TODO Auto-generated method stub
		
	}*/
	
	public Location clone(){
		Location entity = new Location(id, locationName, null, null);
		return entity;
	}
	
	public Location(){}

	public Location(Long id,String locationName, Set<TestLocation> testlocations, Set<Test> tests) {
		super();
		this.id = id;
		this.locationName = locationName;
		this.testlocations = testlocations;
		this.tests = tests;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((locationName == null) ? 0 : locationName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (locationName == null) {
			if (other.locationName != null)
				return false;
		} else if (!locationName.equals(other.locationName))
			return false;
		return true;
	}

	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Set<TestLocation> getTestlocations() {
		return testlocations;
	}

	public void setTestlocations(Set<TestLocation> testlocations) {
		this.testlocations = testlocations;
	}

	public Set<Test> getTests() {
		return tests;
	}

	public void setTests(Set<Test> tests) {
		this.tests = tests;
	}

	

	
	
	

}
