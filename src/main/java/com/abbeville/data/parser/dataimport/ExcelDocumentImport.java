package com.abbeville.data.parser.dataimport;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abbeville.data.parser.dao.ConditionCodeMapDao;
import com.abbeville.data.parser.dao.ExaminationJdbcDao;
import com.abbeville.data.parser.domain.ConditionCode;
import com.abbeville.data.parser.domain.Examination;

public class ExcelDocumentImport {
	
	private static final Logger logger = LoggerFactory.getLogger(ExcelDocumentImport.class);

	private Workbook workbook;
	
	public ExcelDocumentImport(String filePath) throws EncryptedDocumentException, InvalidFormatException, IOException {
		workbook = WorkbookFactory.create(new File(filePath));
	}
	
	public void performInitialImport() {
		ExaminationJdbcDao.getInstance().truncateExistingTables();
		
		logger.warn("Importing Abbeville, LA sheet");
		
		Sheet abbevilleSheet = workbook.getSheet("Abbeville, LA");
		
		for (Row row : abbevilleSheet) {
			if (row.getRowNum() == 0) { continue; }
			
			Examination examination = new Examination(row);
			ExaminationJdbcDao.getInstance().insertExamination(examination.getIncomingExaminationCount(), examination.getExaminationYear(), examination.getExaminationMonth());
		}
		
		logger.warn("Importing Heart Related Condition Codes sheet");
		
		Sheet conditionCodeSheet = workbook.getSheet("Heart-related Condition Codes");
		
		for (Row row : conditionCodeSheet) {
			if (row.getRowNum() == 0) { continue; }
			
			ConditionCode conditionCode = new ConditionCode(row);
			ConditionCodeMapDao.getInstance().addCondition(conditionCode.getConditionCodeKey(), conditionCode.getConditionString());
		}
	}
	
	public void performExamUpdates() throws ParseException {
		logger.warn("Importing exam update sheets");
		
		ExamUpdateProcessor.processSheets(
				workbook.getSheet("May-2007 Violet, LA"),
				workbook.getSheet("May-2007 New Orleans, LA"),
				workbook.getSheet("May-2007 Lafayette, LA"),
				workbook.getSheet("May-2007 Baton Rouge, LA")
		);
	}
	
	public void performDec2013Updates() {
		logger.warn("Importing 2013 December data");
		
		Sheet dec2013Sheet = workbook.getSheet("December 2013 Data");
		
		for (Row row : dec2013Sheet) {
			if (row.getRowNum() == 0) { continue; }
			
			Cell cell = row.getCell(0);
			String routingSysid = cell.getStringCellValue();
			
			if (routingSysid.startsWith("L839") && (routingSysid.endsWith("TGU3") || routingSysid.endsWith("ROV8"))) {
				if (ConditionCodeMapDao.getInstance().conditionExists(routingSysid.substring(7, 13))) {
					ExaminationJdbcDao.getInstance().incrementExaminationCountForMonth(2013, 12);
				}
			}
		}
	}
	
	public void closeWorkbook() {
		try {
			workbook.close();
		} catch (IOException e) {  }
	}
}
