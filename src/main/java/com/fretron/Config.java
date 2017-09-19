package com.fretron;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Config {

    private final Properties properties = new Properties();

    public void load(String file) throws IOException {
        try {
            InputStream inputStream = new FileInputStream(file);
            properties.loadFromXML(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasKey(String key) {
        return properties.containsKey(key);
    }

    public String getString(String key) {
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }
        return null;
    }

    public String getString(String key, String defaultValue) {
        if (hasKey(key)) {
            return getString(key);
        } else {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key));
    }

    public int getInteger(String key) {
        return getInteger(key, 0);
    }

    public int getInteger(String key, int defaultValue) {
        if (hasKey(key)) {
            return Integer.parseInt(getString(key));
        } else {
            return defaultValue;
        }
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long defaultValue) {
        if (hasKey(key)) {
            return Long.parseLong(getString(key));
        } else {
            return defaultValue;
        }
    }

    public double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    public double getDouble(String key, double defaultValue) {
        if (hasKey(key)) {
            return Double.parseDouble(getString(key));
        } else {
            return defaultValue;
        }
    }


    public void put(String key, Object value) {
        properties.put(key, value);
    }
}
