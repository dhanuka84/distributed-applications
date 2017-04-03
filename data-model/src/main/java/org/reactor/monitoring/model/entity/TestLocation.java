package org.reactor.monitoring.model.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "TestLocation")
public class TestLocation extends AbstractEntity<TestLocation>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1170882202066355803L;
	
	/**
     * Emedded composite identifier class that represents the
     * primary key columns of the many-to-many join table.
     */
    public static class Id implements Serializable,Cloneable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2591077542702799036L;
		private Long testId;
		private Long locationId;

        public Id() {}

        public Id(Long testId, Long locationId) {
			this.testId = testId;
			this.locationId = locationId;
		}
        
        public Id clone(){
        	Id newId = new Id(testId, locationId);
    		return newId;
    	}

		public boolean equals(Object o) {
			if (o instanceof Id) {
				Id that = (Id)o;
				return this.testId.equals(that.testId) &&
					   this.locationId.equals(that.locationId);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return testId.hashCode() + locationId.hashCode();
		}

		public Long getTestId() {
			return testId;
		}

		public void setTestId(Long testId) {
			this.testId = testId;
		}

		public Long getLocationId() {
			return locationId;
		}

		public void setLocationId(Long locationId) {
			this.locationId = locationId;
		}

		/*@Override
		public void writeData(ObjectDataOutput out) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void readData(ObjectDataInput in) throws IOException {
			// TODO Auto-generated method stub
			
		}*/
		
		
	}

	private Id id;
	
	private Test test;
	private Location location;
	private float availabilityCritical;
	private float availabilityWarning;
	private float responseCritical;
	private float responseWarning;
	private Long version;

	/*@Override
	public void writeData(ObjectDataOutput out) throws IOException {
		out.writeUTF(street);
        out.writeInt(zipCode);
        out.writeUTF(city);
        out.writeUTF(state);
		
	}

	@Override
	public void readData(ObjectDataInput in) throws IOException {
		// TODO Auto-generated method stub
		
	}*/
	public TestLocation clone(){
		Id newId = id.clone();
		TestLocation entity = new TestLocation(newId,null, null, availabilityCritical, availabilityWarning, responseCritical, responseWarning);
		return entity;
	}
	
	
	public TestLocation(){}

	public TestLocation(Id id ,Test test, Location location, float availabilityCritical, float availabilityWarning,
			float responseCritical, float responseWarning) {
		super();
		this.id = id;
		this.test = test;
		this.location = location;
		this.availabilityCritical = availabilityCritical;
		this.availabilityWarning = availabilityWarning;
		this.responseCritical = responseCritical;
		this.responseWarning = responseWarning;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		TestLocation other = (TestLocation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public float getAvailabilityCritical() {
		return availabilityCritical;
	}

	public void setAvailabilityCritical(float availabilityCritical) {
		this.availabilityCritical = availabilityCritical;
	}

	public float getAvailabilityWarning() {
		return availabilityWarning;
	}

	public void setAvailabilityWarning(float availabilityWarning) {
		this.availabilityWarning = availabilityWarning;
	}

	public float getResponseCritical() {
		return responseCritical;
	}

	public void setResponseCritical(float responseCritical) {
		this.responseCritical = responseCritical;
	}

	public float getResponseWarning() {
		return responseWarning;
	}

	public void setResponseWarning(float responseWarning) {
		this.responseWarning = responseWarning;
	}

	public Id getId() {
		return id;
	}

	public void setId(Id id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	
	

}
