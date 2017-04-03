package org.reactor.monitoring.model.entity;

import java.util.HashSet;
import java.util.Set;

public class Product extends AbstractEntity<Product>/*,Comparable<Product>*/{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4767545329450301466L;
	private Long id;
	private Long version;
	private String productId;
	private Set<Test> tests;	
	
	public Product clone(){
		
		
		Set<Test> cloneTests = new HashSet<Test>();
		if(tests != null){
			for(Test test : tests){
				cloneTests.add((Test) test.clone());
			}
		}
		
		Product product = new Product(id, productId, cloneTests);
		return product;
		
	}

	/*@Override
	public void readData(ObjectDataInput arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeData(ObjectDataOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}*/
	/*@Override
	public int compareTo(Product that) {
		
		return id.compareTo(that.id);
	}*/
	
	public Product(){}

	public Product(Long id, String productId, Set<Test> tests) {
		super();
		this.id = id;
		this.productId = productId;
		this.tests = tests;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
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
		Product other = (Product) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
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

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Set<Test> getTests() {
		return tests;
	}

	public void setTests(Set<Test> tests) {
		this.tests = tests;
	}

	
	
	

}
