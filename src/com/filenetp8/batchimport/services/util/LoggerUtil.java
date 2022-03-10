package com.filenetp8.batchimport.services.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerUtil {
	private static Properties appenderConfig = null;

	static {
		try {

			appenderConfig = new Properties();
			InputStream is = LoggerUtil.class.getClassLoader().getResourceAsStream("logj.properties");
			FileInputStream fis = new FileInputStream(new File("logj.properties"));
			appenderConfig.load(fis);
			PropertyConfigurator.configure(appenderConfig);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Logger getLogger(Class className) {
		Logger logger = Logger.getLogger(className);
		// configAppender(logger);
		return logger;
	}

	public static Logger getLogger(String className) {
		Logger logger = Logger.getLogger(className);
		// configAppender(logger);
		return logger;
	}

}