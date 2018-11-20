package com.abbeville.data.parser.dao;

import java.util.HashMap;
import java.util.Map;

public class ConditionCodeMapDao {
	
	private static class InstanceHolder {
		private static final ConditionCodeMapDao instance = new ConditionCodeMapDao();
	}
	
	public static ConditionCodeMapDao getInstance() {
		return InstanceHolder.instance;
	}
	

	private Map<String, String> conditionCodeMap = new HashMap<>();
	
	public void addCondition(String conditionCode, String condition) {
		conditionCodeMap.put(conditionCode, condition);
	}
	
	public boolean conditionExists(String conditionCode) {
		return conditionCodeMap.get(conditionCode) != null;
	}
}
