package com.abbeville.data.parser.dataimport;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abbeville.data.parser.dao.ExaminationJdbcDao;
import com.abbeville.data.parser.util.ExaminationDateParser;

public class ExamUpdateProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(ExamUpdateProcessor.class);
	
	private final DataFormatter dataFormatter = new DataFormatter();

	private Sheet sheet;
	// Track the request IDs
	private Set<String> processedRequestIds;
	
	public ExamUpdateProcessor(Sheet sheet) {
		this.sheet = sheet;
		this.processedRequestIds = new HashSet<>();
	}
	
	public void processUpdate() throws ParseException {
		for (Row row : sheet) {
			if (row.getRowNum() == 0) { continue; }
			
			String requestId = dataFormatter.formatCellValue(row.getCell(3));
			
			// If we've already processed a request with this ID, skip the current record
			if (processedRequestIds.contains(requestId)) {
				logger.warn("Duplicate request ID found: " + requestId + ". Skipping update for current record");
				continue;
			}
			
			String originalHospitalString = dataFormatter.formatCellValue(row.getCell(0));
			String examinationString = dataFormatter.formatCellValue(row.getCell(1));
			ExaminationDateParser dateParser = new ExaminationDateParser(row.getCell(2));
			
			if (originalHospitalString.equals("Abbeville") && (examinationString.contains("Heart") || examinationString.contains("Cardio") ||
					examinationString.equals("Cardiac"))) {
				
				logger.warn("Updating month: " + dateParser.getExamMonth() + ", year: " + dateParser.getExamYear() + " for date: " + dateParser.getDateString());
				logger.warn("Original hospital: " + originalHospitalString + "; Examination: " + examinationString);
				
				boolean updated = ExaminationJdbcDao.getInstance().incrementExaminationCountForMonth(dateParser.getExamYear(), dateParser.getExamMonth());
				
				if (!updated) {
					logger.error("Failed to update existing record!");
				} else {
					processedRequestIds.add(requestId);
				}
			}
		}
	}
	
	public static void processSheets(Sheet... sheets) throws ParseException {
		for (Sheet sheet : sheets) {
			new ExamUpdateProcessor(sheet).processUpdate();
		}
	}
}
