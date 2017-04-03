package org.reactor.monitoring.model.entity;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

@JsonRootName(value = "Test")
public class Test  extends AbstractEntity<Test>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 200916307235587720L;
	private Long id;
	private Long version;
	private String testId;
	private Product product;
	private String testName;
	private Set<TestLocation> testlocations;
	private Set<Location> locations;
		

	public Test clone(){
		Test clonedTest = new Test(id, testId,null,testName,null,null);
		return clonedTest;
	}
	
	/*@Override
	public void readData(ObjectDataInput arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeData(ObjectDataOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}
	*/
	@Override
	public int compareTo(Test that) {
		
		return id.compareTo(that.id);
	}
	
	public Test(){}

	public Test(Long id, String testId, Product product, String testName, Set<TestLocation> testlocations,
			Set<Location> locations) {
		super();
		this.id = id;
		this.testId = testId;
		this.product = product;
		this.testName = testName;
		this.testlocations = testlocations;
		this.locations = locations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((testId == null) ? 0 : testId.hashCode());
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
		Test other = (Test) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (testId == null) {
			if (other.testId != null)
				return false;
		} else if (!testId.equals(other.testId))
			return false;
		return true;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}


	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public Set<TestLocation> getTestlocations() {
		return testlocations;
	}

	public void setTestlocations(Set<TestLocation> testlocations) {
		this.testlocations = testlocations;
	}

	public Set<Location> getLocations() {
		return locations;
	}

	public void setLocations(Set<Location> locations) {
		this.locations = locations;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
	

	
	
	

}
