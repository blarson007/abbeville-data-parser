package com.abbeville.data.parser.dataexport;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.abbeville.data.parser.dao.ExaminationJdbcDao;
import com.abbeville.data.parser.domain.Examination;

public class ExamWriter {
	
	private String fileName;
	
	public ExamWriter(String fileName) {
		this.fileName = fileName;
	}

	public void writeDataToExcel() throws IOException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Abbeville Final");
		
		Row headerRow = sheet.createRow(0);
		
		Cell cell = headerRow.createCell(0);
        cell.setCellValue("Incoming Examinations");
        
        Cell cell1 = headerRow.createCell(1);
        cell1.setCellValue("Year");
        
        Cell cell2 = headerRow.createCell(2);
        cell2.setCellValue("Month");
        
        int rowNum = 1;
        for (Examination examination : ExaminationJdbcDao.getInstance().getAllExaminationData()) {
        	Row row = sheet.createRow(rowNum++);
        	
        	row.createCell(0).setCellValue(examination.getIncomingExaminationCount());
        	row.createCell(1).setCellValue(examination.getExaminationYear());
        	row.createCell(2).setCellValue(examination.getExaminationMonth());
        }
        
        FileOutputStream fileOut = new FileOutputStream(fileName);
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
	}
}
