package com.hollandjake.messenger_bot_api.util;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;

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
		loadFile(file != null ? file : DEFAULT_FILE);
	}

	private void loadFile(String file) {
		InputStream inputStream = null;
		try {
			defaults = new Properties();
			defaults.setProperty("message_timeout", 1000 * 60 + "");
			defaults.setProperty("refresh_rate", 100 + "");
			defaults.setProperty("image_size", 200000 + "");

			inputStream = getClass().getClassLoader().getResourceAsStream(file);

			if (inputStream != null) {
				load(inputStream);
				System.out.println("Loaded configuration file : " + file);
			} else {
				throw new FileNotFoundException("'" + file + "' is not a property file");
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			if (!hasProperty(property)) {
				missingProperties.append("'").append(property).append("',");
			}
		}
		if (!missingProperties.toString().equals("")) {
			missingProperties = new StringBuilder(missingProperties.toString().replaceFirst(",$", ""));
			throw new MalformedParametersException(missingProperties + " are required");
		}
	}

	public Object get(String name) {
		String value = super.getProperty(name);
		if (value == null) {
			return null;
		} else if (name.equals("log_level")) {
			return LOG_LEVEL.values()[Integer.parseInt(value)];
		} else {
			try {
				return NumberUtils.createNumber(value);
			} catch (NumberFormatException ignored) {
			}
			//Boolean
			try {
				Boolean parsed = BooleanUtils.toBooleanObject(value);
				if (parsed != null) {
					return parsed;
				}
			} catch (Exception ignored) {
			}
		}
		return value;
	}
}
