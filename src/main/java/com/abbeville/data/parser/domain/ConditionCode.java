package com.abbeville.data.parser.domain;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ConditionCode {

	private String conditionCodeKey;
	private String conditionString;
	
	public ConditionCode(Row row) {
		for (Cell cell : row) {
			switch (cell.getColumnIndex()) {
				case 0:
					conditionString = cell.getStringCellValue();
				case 1:
					conditionCodeKey = cell.getStringCellValue();
			}
		}
	}

	public String getConditionCodeKey() {
		return conditionCodeKey;
	}

	public String getConditionString() {
		return conditionString;
	}
}
