package org.crypto.util;

import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;

public class PropertyConfig {
	private static PropertyConfig instance;
	private Properties configure;
	public final static String CONFIG_FILE_PATH = "..\\CryptoCompare\\src\\main\\resources\\config.properties"; 

	private PropertyConfig() throws ConfigurationException {
        configure = new Properties();
    }

	public static PropertyConfig getInstance() {
		if (null == instance) {
			try {
				instance = new PropertyConfig();
			} catch (ConfigurationException ex) {
				throw new RuntimeException(ex);
			}
		}
		return instance;
	}

	public Properties getConfigure() {
		return configure;
	}

	public void setConfig(Properties configure) {
		this.configure = configure;
	}
}
