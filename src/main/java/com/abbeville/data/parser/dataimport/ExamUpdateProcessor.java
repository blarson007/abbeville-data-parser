package com.abbeville.data.parser.dataimport;

import java.text.ParseException;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.abbeville.data.parser.dao.ExaminationJdbcDao;
import com.abbeville.data.parser.util.ExaminationDateParser;

public class ExamUpdateProcessor {
	
	private final DataFormatter dataFormatter = new DataFormatter();

	private Sheet sheet;
	
	public ExamUpdateProcessor(Sheet sheet) {
		this.sheet = sheet;
	}
	
	public void processUpdate() throws ParseException {
		for (Row row : sheet) {
			if (row.getRowNum() == 0) { continue; }
			
			String originalHospitalString = dataFormatter.formatCellValue(row.getCell(0));
			String examinationString = dataFormatter.formatCellValue(row.getCell(1));
//			String dateString = dataFormatter.formatCellValue(row.getCell(2));
//			Date date = row.getCell(2).getDateCellValue();
			ExaminationDateParser dateParser = new ExaminationDateParser(row.getCell(2));
			
			System.out.println("Processing value: " + dateParser.getDateString());
			
			if (originalHospitalString.equals("Abbeville") && (examinationString.contains("Heart") || examinationString.contains("Cardio") ||
					examinationString.equals("Cardiac"))) {
				
				
				
				ExaminationJdbcDao.getInstance().incrementExaminationCountForMonth(dateParser.getExamYear(), dateParser.getExamMonth());
			}
		}
	}
	
	public static void processSheets(Sheet... sheets) throws ParseException {
		for (Sheet sheet : sheets) {
			new ExamUpdateProcessor(sheet).processUpdate();
		}
	}
}
