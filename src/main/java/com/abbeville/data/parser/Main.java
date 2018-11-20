package com.abbeville.data.parser;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abbeville.data.parser.dataexport.ExamWriter;
import com.abbeville.data.parser.dataimport.ExcelDocumentImport;

public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		
		Options options = createOptions();
		CommandLineParser commandLineParser = new DefaultParser();
		CommandLine commandLine = null;
		
		try {
			commandLine = commandLineParser.parse(options, args);
			
			logger.warn("Action: " + commandLine.getOptionValue("action"));
			logger.warn("File: " + commandLine.getOptionValue("file"));
			
			if (commandLine.getOptionValue("action").equals("import")) {
				ExcelDocumentImport documentImport = new ExcelDocumentImport(commandLine.getOptionValue("file"));
				
				documentImport.performInitialImport();
				documentImport.performExamUpdates();
				documentImport.performDec2013Updates();
				documentImport.closeWorkbook();
				
			} else if (commandLine.getOptionValue("action").equals("export")) {
				new ExamWriter(commandLine.getOptionValue("file")).writeDataToExcel();
			}
			
		} catch (Exception e) {
			logger.error("Fatal error while starting up the application.", e);
			System.exit(1);
		}	
	}

	private static Options createOptions() {
		Options options = new Options();

		options.addOption(Option.builder("action").hasArg().desc("The action to perform. Options are 'import' and 'export'").type(String.class).required().build());
		options.addOption(Option.builder("file").hasArg().desc("The file to read or write to, respectively").type(String.class).required().build());

		return options;
	}
}
