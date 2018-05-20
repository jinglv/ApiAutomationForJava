package com.api.utils;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class PropertiesUtils {

    private static Configuration config;

    static {
        if(null == config){
            try {
                config = new PropertiesConfiguration("config.properties");
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    private PropertiesUtils(){}

    public static String getString(String key){
        return config.getString(key,"");
    }

    public static String[] getStringArray(String key){
        return config.getStringArray(key);
    }
}
