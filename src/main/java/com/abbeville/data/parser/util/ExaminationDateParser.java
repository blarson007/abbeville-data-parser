package com.abbeville.data.parser.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExaminationDateParser {
	
	private static final Logger logger = LoggerFactory.getLogger(ExaminationDateParser.class);

	private final SimpleDateFormat sdf1 = new SimpleDateFormat("MMM-yy");
	private final SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM, yyyy");
	private final SimpleDateFormat sdf3 = new SimpleDateFormat("M/d/yyyy");
	
	private LocalDate localDate;
	
	public ExaminationDateParser(Cell dateCell) throws ParseException {
		Date examDate = null;
		
		try {
			examDate = dateCell.getDateCellValue();
			setLocalDate(examDate);
			return;
		} catch (Exception e) {  }
		
		String dateString = new DataFormatter().formatCellValue(dateCell);
		try {
			examDate = sdf1.parse(dateString);
		} catch (ParseException e) {
			try {
				examDate = sdf2.parse(dateString);
			} catch (ParseException e1) {
				try {
					examDate = sdf3.parse(dateString);
				} catch (ParseException e3) {
					logger.error("Unable to parse date string: " + dateString, e);
					throw e3;
				}
			}
		}
		setLocalDate(examDate);
	}
	
	private void setLocalDate(Date date) {
		localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public int getExamYear() {
		return localDate.getYear();
	}
	
	public int getExamMonth() {
		return localDate.getMonthValue();
	}
	
	public String getDateString() {
		return localDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
	}
}
