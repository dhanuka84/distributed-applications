package org.reactor.monitoring.model.entity;

import java.util.Comparator;

public class VersionComparator implements Comparator<Long>{

	@Override
	public int compare(Long o1, Long o2) {
		return o1.compareTo(o2);
	}

}
