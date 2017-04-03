package org.reactor.monitoring.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Calculation {

	public class Test {
		private final String result;
		private float duration;
		private final String location;
		private final String testId;
		private final String productId;
		private int count;

		public Test(String result, float duration, String location, String testId, String productId) {
			super();
			this.result = result;
			this.duration = duration;
			this.location = location;
			this.testId = testId;
			this.productId = productId;
		}

		public String getResult() {
			return result;
		}

		public float getDuration() {
			return duration;
		}

		public String getLocation() {
			return location;
		}

		public String getTestId() {
			return testId;
		}
		
		public String getProductId() {
			return productId;
		}
		
		public void incrementCount(){
			++count;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public void setDuration(float duration) {
			this.duration = duration;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((location == null) ? 0 : location.hashCode());
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
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (location == null) {
				if (other.location != null)
					return false;
			} else if (!location.equals(other.location))
				return false;
			if (testId == null) {
				if (other.testId != null)
					return false;
			} else if (!testId.equals(other.testId))
				return false;
			return true;
		}

		private Calculation getOuterType() {
			return Calculation.this;
		}
		
		

	}
	
	public static class Threashold{
		
		private final float poor;
		private final float degraded;
		
		public Threashold(float poor, float degraded) {
			super();
			this.poor = poor;
			this.degraded = degraded;
		}
		
		public float getPoor() {
			return poor;
		}
		public float getDegraded() {
			return degraded;
		}
		
		
	}
	
	public static class AverageRAGValue{
		private final float highertValue;
		private final float lowerValue;
		
		public AverageRAGValue(float highertValue, float lowerValue) {
			super();
			this.highertValue = highertValue;
			this.lowerValue = lowerValue;
		}

		public float getHighertValue() {
			return highertValue;
		}

		public float getLowerValue() {
			return lowerValue;
		}
		
		
	}
	
	public static class CompositeResultValue{
		private final RESULT result;
		private final float duration;
		//0.5
		private final float amberValue;
		private final String testId;
		private final Test test;
		
		public CompositeResultValue(RESULT result, final Test test, float amberValue) {
			super();
			this.result = result;
			this.test = test;
			this.duration = test.getDuration();
			this.testId = test.getTestId();
			this.amberValue = amberValue;
		}

		public RESULT getResult() {
			return result;
		}

		public float getDuration() {
			return duration;
		}

		public float getAmberValue() {
			return amberValue;
		}

		public String getTestId() {
			return testId;
		}
		
	}
	
	public static class CompositeRAG{
		private final RAG availabilityRAG;
		private final RAG responseTimeRAG;
		
		public CompositeRAG(RAG availabilityRAG, RAG responseTimeRAG) {
			super();
			this.availabilityRAG = availabilityRAG;
			this.responseTimeRAG = responseTimeRAG;
		}

		public RAG getAvailabilityRAG() {
			return availabilityRAG;
		}

		public RAG getResponseTimeRAG() {
			return responseTimeRAG;
		}
		
	}

	public enum RAG {
		RED, AMBER, GREEN;
		
	}

	public enum RESULT {
		PASSED(1), FAILED(0);

		private final int value;

		RESULT(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	}
	
	// 0.4 0.2 test wise
	public static Map<String, Threashold> THREASHOLDS = new HashMap<>();
	// currently 0.5 0.3 hard coded
	public static Map<String, AverageRAGValue> AVERAGERAG_PER_PRODUCT = new HashMap<>();
	// currently global value 0.5
	public static Map<String, Float> AMBERVALUE_PER_PRODUCT = new HashMap<>();
	
	public static void calculateHealth(String productId, final List<Test> tests){
		
		float amberValue = AMBERVALUE_PER_PRODUCT.get(productId);
		AverageRAGValue avRagValue = AVERAGERAG_PER_PRODUCT.get(productId);
		
		CompositeRAG composite = getAvailabilityAndResponseRAG(tests, amberValue, avRagValue);
		RAG availabilityRAG = composite.getAvailabilityRAG();
		RAG responseTimeRAG = composite.getResponseTimeRAG();
		
		Set<RAG> rags = new HashSet<RAG>();
		rags.add(responseTimeRAG);
		rags.add(availabilityRAG);
		RAG health = getMinimum(rags);
		
	}
	
	public static void calculateConsecutiveResponseRAG(final List<Test> tests, final float amberValue, AverageRAGValue avRagValue){
		//group of test group by location and testId
		Map<Test,Test> testMap = new HashMap<Test,Test>();
		for(Test test: tests){
			if (testMap.containsKey(test)) {
				Test addedTest = testMap.get(test);
				float averageValue = addedTest.getDuration();
				addedTest.incrementCount();
				averageValue = averageValue + test.getDuration();
				addedTest.setDuration(averageValue);
				continue;
			}
			testMap.put(test,test);
			test.incrementCount();
			
		}
		
		List<Test> averageTests = new ArrayList(testMap.values());
		CompositeRAG composite = getAvailabilityAndResponseRAG(averageTests, amberValue, avRagValue);
		RAG responseTimeRAG = composite.getResponseTimeRAG();
		
	}
	
	public static CompositeRAG getAvailabilityAndResponseRAG(final List<Test> tests, final float amberValue, AverageRAGValue avRagValue) {
		RAG availabilityRAG = null;
		RAG responseTimeRAG = null;

		Map<String, List<CompositeResultValue>> mapByTest = new HashMap<>();
		Map<String, List<CompositeResultValue>> mapByLocation = new HashMap<>();
		Set<Test> testSet = new HashSet<Test>();
		Set<RAG> rags = new HashSet<RAG>();

		for (Test test : tests) {
			if (testSet.contains(test)) {
				continue;
			} else {
				if(test.getCount() > 0){
					test.setDuration(test.getDuration()/test.getCount());
				}
				testSet.add(test);
				String name = test.getTestId();
				String location = test.getLocation();

				// Availability & Response time RAG
				groupResults(mapByTest, test, name, amberValue);
				groupResults(mapByLocation, test, location, amberValue);
			}
		}

		// X axis
		CompositeRAG testWise = calculateRAG(mapByTest,avRagValue);
		// Y axis
		CompositeRAG locationWise = calculateRAG(mapByLocation,avRagValue);
		
		rags.add(locationWise.getAvailabilityRAG());
		rags.add(testWise.getAvailabilityRAG());
		availabilityRAG = getMinimum(rags);
		
		rags = new HashSet<RAG>();
		rags.add(locationWise.getResponseTimeRAG());
		rags.add(testWise.getResponseTimeRAG());
		responseTimeRAG = getMinimum(rags);
		
		CompositeRAG composite = new CompositeRAG(availabilityRAG, responseTimeRAG);
		return composite;
	}
	
	private static RESULT convertToRESULT(final String result) {
		RESULT value = RESULT.FAILED;

		if (result != null && result.trim().length() > 0) {
			String upperValue = result.toUpperCase();
			if (upperValue.equals(RESULT.FAILED.toString())) {
				value = RESULT.FAILED;
			} else if (upperValue.equals(RESULT.PASSED.toString())) {
				value = RESULT.PASSED;
			}
		}

		return value;
	}
	
	private static void groupResults(final Map<String,List<CompositeResultValue>> map, Test test, String key, float amberValue){
		String result = test.getResult();
		if(map.containsKey(key)){
			CompositeResultValue composite = new CompositeResultValue(convertToRESULT(result),test,amberValue);
			map.get(key).add(composite);
		}else{
			List<CompositeResultValue> list = new ArrayList<>();
			list.add(new CompositeResultValue(convertToRESULT(result),test,amberValue));
			map.put(key, list);
		}
		
	}
	
	private static RAG getMinimum(final Set<RAG> rags) {
		RAG minimumRAG = null;
		// minimum
		if (rags.contains(RAG.RED)) {
			minimumRAG = RAG.RED;
		} else if (rags.contains(RAG.AMBER)) {
			minimumRAG = RAG.AMBER;
		} else if (rags.contains(RAG.GREEN)) {
			minimumRAG = RAG.GREEN;
		}

		return minimumRAG;
	}
	
	private static CompositeRAG calculateRAG(final Map<String,List<CompositeResultValue>> map, final AverageRAGValue avRagValue){
		Set<Map.Entry<String, List<CompositeResultValue>>> entries = map.entrySet();
		Set<RAG> availabilityRags = new HashSet<RAG>();
		Set<RAG> responseTimeRags = new HashSet<RAG>();
		for(Map.Entry<String, List<CompositeResultValue>> entry : entries){
			List<CompositeResultValue> values = entry.getValue();
			int innerCount = 0;
			float availabilitySum = 0.0f;
			float responseTimeSum = 0.0f;
			RAG availabilityRAG = null;
			RAG responseTimeRAG = null;
			
			float higherValue = avRagValue.getHighertValue();
			float lowerValue = avRagValue.getLowerValue();
			
			for(CompositeResultValue composite : values){
				
				//availability RAG
				RESULT result = composite.getResult();
				++innerCount;
				availabilitySum = availabilitySum + result.getValue();
				
				//response time value
				float duration = composite.getDuration();
				String testId = composite.getTestId();
				float amberValue = composite.getAmberValue();
				float encodedValue = getResponseTimeEncodedValue(duration, testId, amberValue);
				responseTimeSum = responseTimeSum + encodedValue;
				
			}
			//availability RAG
			availabilitySum = availabilitySum/innerCount;
			if(availabilitySum == 1.0){
				availabilityRAG = RAG.GREEN;
			}
			
			if(availabilitySum < 1.0 && availabilitySum > 0.0){
				availabilityRAG = RAG.AMBER;
			}
			
			if(availabilitySum == 0.0){
				availabilityRAG = RAG.RED;
			}
			availabilityRags.add(availabilityRAG);
			
			//response time 
			responseTimeSum = responseTimeSum/innerCount;
			if(responseTimeSum >= higherValue){
				responseTimeRAG = RAG.GREEN;
			}
			
			if(responseTimeSum < higherValue && responseTimeSum >= lowerValue){
				responseTimeRAG = RAG.AMBER;
			}
			
			if(responseTimeSum < lowerValue){
				responseTimeRAG = RAG.RED;
			}
			responseTimeRags.add(responseTimeRAG);
		}
		
		CompositeRAG composite = new CompositeRAG(getMinimum(availabilityRags), getMinimum(responseTimeRags));
		return composite;
	}
	
	private static float getResponseTimeEncodedValue(final float duration, String testId,final float amberValue ){
		// Response time threashold value, betterr pass this as a parameter
		Threashold threashold = THREASHOLDS.get(testId);
		float poor = threashold.getPoor();
		float degraded = threashold.getDegraded();

		float encodedValue = 0.0f;
		if (duration <= degraded) {
			encodedValue = 1.0f;
		} else if (duration > degraded && duration <= poor) {
			encodedValue = amberValue;
		} else if (duration > poor) {
			encodedValue = 0.0f;
		}
		
		return encodedValue;
	}

	

	public static void main(String... arg) {
		float value = -1;
		value = value/3;
		System.out.println(value);
		
	}

}