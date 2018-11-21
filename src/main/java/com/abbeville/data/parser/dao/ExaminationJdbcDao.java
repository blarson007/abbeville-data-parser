package com.abbeville.data.parser.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.abbeville.data.parser.domain.Examination;

public class ExaminationJdbcDao {
	
	private static final Logger logger = LoggerFactory.getLogger(ExaminationJdbcDao.class);

	private static class InstanceHolder {
		private static final ExaminationJdbcDao instance = new ExaminationJdbcDao();
	}
	
	public static ExaminationJdbcDao getInstance() {
		return InstanceHolder.instance;
	}
	
	
	private JdbcTemplate jdbcTemplate;
	
	private ExaminationJdbcDao() {
		jdbcTemplate = new JdbcTemplate(new EmbeddedDataSource(true).getDataSource());
	}
	
	public void truncateExistingTables() {
		jdbcTemplate.update("truncate table examinations_by_month");
	}
	
	public void insertExamination(int incomingExaminationCount, int examinationYear, int examinationMonth) {
		jdbcTemplate.update(
				"INSERT INTO examinations_by_month (incoming_examinations, examination_year, examination_month) VALUES (?, ?, ?)", 
				incomingExaminationCount, examinationYear, examinationMonth);
	}
	
	public boolean incrementExaminationCountForMonth(int examinationYear, int examinationMonth) {
		logger.debug("Performing examination update for year: " + examinationYear + ", month: " + examinationMonth);
		
		return jdbcTemplate.update(
				"UPDATE examinations_by_month " +
				"SET incoming_examinations = incoming_examinations + 1 " +
				"WHERE examination_year = ? AND examination_month = ?", examinationYear, examinationMonth) > 0;
	}
	
	public Collection<Examination> getAllExaminationData() {
		return jdbcTemplate.query("SELECT * FROM examinations_by_month", new ExaminationRowMapper());
	}
	
	public class ExaminationRowMapper implements RowMapper<Examination> {

		@Override
		public Examination mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Examination(rs);
		}
	}
}
