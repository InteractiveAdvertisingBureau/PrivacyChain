package com.acxiom.utils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;
import java.util.Properties;

public class PropertiesUtils {

	private final Properties properties;

	private InputStream inputFile;

	public PropertiesUtils(String filename) {
		properties = new Properties();
		try {
			inputFile = PropertiesUtils.class.getClassLoader().getResourceAsStream(filename);
			properties.load(inputFile);
			inputFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Properties getProperties() {
		return properties;
	}

	/**
	 * get Property。
	 */
	private String getValue(String key) {
		String systemProperty = System.getProperty(key);
		if (systemProperty != null) {
			return systemProperty;
		}
		String value = properties.getProperty(key);
		if (value != null && !"".equals(value)) {
			try {
				return new String(value.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	/**
	 * get the String value of the key.
	 */
	public String getProperty(String key) {
		return getValue(key);
	}

	/**
	 * get the String value of the key, if the value does not exists, then use the
	 * default value.
	 */
	public String getProperty(String key, String defaultValue) {
		String value = getValue(key);
		return value != null ? value : defaultValue;
	}

	/**
	 * get the Integer value of the key.
	 */
	public Integer getInteger(String key) {
		return Integer.valueOf(getValue(key));
	}

	/**
	 * get the Integer value of the key, if the value does not exists, then use the
	 * default value.
	 */
	public Integer getInteger(String key, Integer defaultValue) {
		String value = getValue(key);
		return value != null ? Integer.valueOf(value) : defaultValue;
	}

	/**
	 * get the Double value of the key.
	 */
	public Double getDouble(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Double.valueOf(value);
	}

	/**
	 * get the Double value of the key, if the value does not exists, then use the
	 * default value.
	 */
	public Double getDouble(String key, Integer defaultValue) {
		String value = getValue(key);
		return value != null ? Double.valueOf(value) : defaultValue;
	}

	/**
	 * get the boolean value of the key.
	 */
	public Boolean getBoolean(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Boolean.valueOf(value);
	}

	/**
	 * get the boolean value of the key, if the value does not exists, then use the
	 * default value.
	 */
	public Boolean getBoolean(String key, boolean defaultValue) {
		String value = getValue(key);
		return value != null ? Boolean.valueOf(value) : defaultValue;
	}
}
