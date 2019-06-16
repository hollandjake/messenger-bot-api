package com.hollandjake.messengerBotAPI.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.MalformedParametersException;
import java.util.Properties;

public class Config extends Properties {

	private static final String DEFAULT_FILE = "config.properties";

	public Config() {
		loadFile(DEFAULT_FILE);
	}

	public Config(String file) {
		if (file != null) {
			System.out.println("User provided custom configuration file : " + file);
		} else {
			file = DEFAULT_FILE;
		}
		loadFile(file);
	}

	private void loadFile(String file) {
		InputStream inputStream = null;
		try {
			defaults = new Properties();
			defaults.setProperty("message_timeout", 1000 * 60 + "");
			defaults.setProperty("refresh_rate", 100 + "");

			inputStream = getClass().getClassLoader().getResourceAsStream(file);

			if (inputStream != null) {
				load(inputStream);
			} else {
				throw new FileNotFoundException("'" + file + "' is not a property file");
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean hasProperty(String key) {
		return containsKey(key) && getProperty(key).length() > 0;
	}

	public void checkForProperties(String... properties) {
		StringBuilder missingProperties = new StringBuilder();
		for (String property : properties) {
			if (!containsKey(property)) {
				missingProperties.append("'").append(property).append("',");
			}
		}
		if (!missingProperties.toString().equals("")) {
			missingProperties = new StringBuilder(missingProperties.toString().replaceFirst(",$", ""));
			throw new MalformedParametersException(missingProperties + " are required");
		}
	}
}
