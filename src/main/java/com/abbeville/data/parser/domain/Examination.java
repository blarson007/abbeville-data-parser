package com.abbeville.data.parser.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Examination {
	
	private static final Logger logger = LoggerFactory.getLogger(Examination.class);
	
	private final DataFormatter dataFormatter = new DataFormatter();

	private int incomingExaminationCount = 0;
	private int examinationYear;
	private int examinationMonth;
	
	public Examination(Row row) {
		
		try {
			incomingExaminationCount = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(0)));
		} catch (NumberFormatException nfe) {
			logger.error("Error parsing examination count value: " + dataFormatter.formatCellValue(row.getCell(0)));
		}
		
		examinationYear = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(1)));
		examinationMonth = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(2)));
	}
	
	public Examination(ResultSet rs) throws SQLException {
		incomingExaminationCount = rs.getInt("incoming_examinations");
		examinationYear = rs.getInt("examination_year");
		examinationMonth = rs.getInt("examination_month");
	}

	public int getIncomingExaminationCount() {
		return incomingExaminationCount;
	}

	public int getExaminationYear() {
		return examinationYear;
	}

	public int getExaminationMonth() {
		return examinationMonth;
	}
}
