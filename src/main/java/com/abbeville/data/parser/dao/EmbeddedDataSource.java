package com.abbeville.data.parser.dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.hsqldb.Server;
import org.hsqldb.util.DatabaseManagerSwing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.abbeville.data.parser.config.HomeDirectoryConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class EmbeddedDataSource {
	
	private static final Logger logger = LoggerFactory.getLogger(EmbeddedDataSource.class);
	
	private DataSource dataSource;

	public EmbeddedDataSource(boolean enableDatabaseManager) {
		logger.warn("Setting up embedded datasource");
		
		boolean running = false;
		
		Server server = new Server();
		
		try {
			server.setLogWriter(new PrintWriter(new FileWriter("db.log")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		server.setDatabasePath(0, "file:" + HomeDirectoryConfig.HOME_DIRECTORY + File.separator + "db");
		server.setPort(9001);
		server.setDatabaseName(0, "abbeville");
		server.checkRunning(running);
		
		if (!running) {
			server.start();
			registerShutdownHook(server);
		}
		
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName(org.hsqldb.jdbcDriver.class.getName());
		hikariConfig.setJdbcUrl("jdbc:hsqldb:hsql://localhost:9001/abbeville");
		hikariConfig.setUsername("sa");
		hikariConfig.setMinimumIdle(5);
		hikariConfig.setMaximumPoolSize(5);
		
		HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
		buildTables(hikariDataSource);
		
		// This is a GUI database manager that can be used for development/troubleshooting
		if (enableDatabaseManager) {
			DatabaseManagerSwing.main(new String[] { "--url", "jdbc:hsqldb:hsql://localhost:9001/abbeville", "--user", "sa", "--password", "" });
		}
		
		this.dataSource = hikariDataSource;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
//	private static void registerShutdownHook(final HikariDataSource hikariDataSource) {
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//			public void run() {
//				logger.warn("Shutting down datasource");
//				hikariDataSource.close();
//			}
//		});
//	}
	
	private static void registerShutdownHook(final Server server) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.warn("Shutting down embedded server");
				server.shutdown();
			}
		});
	}
	
	private static void buildTables(DataSource dataSource) {
		// TODO: This may have to become very elaborate in the future; Basically compare the existing schema with
		// the future schema and make all 'upgrade' modifications. Is there a way to avoid this?
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<String> tables = jdbcTemplate.queryForList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.SYSTEM_TABLES where table_type = 'TABLE'", String.class); 
		
		if (tables.size() == 0) {
			logger.warn("Tables not found. Building tables.");
			try (Connection connection = dataSource.getConnection()) {
				ScriptUtils.executeSqlScript(connection, new ClassPathResource("create-db.sql"));
			} catch (Exception e) {
				logger.error("", e);
			}
		} else {
			logger.warn("Tables already exist. Skipping table build.");
		}
	}
}
