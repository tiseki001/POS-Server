package org.ofbiz.report;

import java.util.List;
import java.util.Set;

import javolution.util.FastSet;

import org.ofbiz.entity.GenericValue;

public class UtilReport {

	public static Set<String> toReportList (Object obj ) {
		List<GenericValue> gvs = (List<GenericValue>) obj;
		Set<String> reportIds = FastSet.newInstance();
		for (GenericValue gv : gvs) {
			reportIds.add((String) gv.get("reportId"));
		}
		return reportIds;
	}
	
}
