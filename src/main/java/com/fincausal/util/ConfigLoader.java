package com.fincausal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置加载器
 * 负责加载和管理配置信息
 */
public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    
    // 配置属性
    private static final Properties properties = new Properties();
    
    // 是否已加载配置
    private static boolean loaded = false;
    
    /**
     * 加载配置文件
     * 
     * @param configPath 配置文件路径
     */
    public void loadConfig(String configPath) {
        try {
            // 首先尝试从文件系统加载
            try (FileInputStream fis = new FileInputStream(configPath)) {
                properties.load(fis);
                loaded = true;
                logger.info("从文件系统加载配置文件: {}", configPath);
            }
        } catch (IOException e) {
            // 如果从文件系统加载失败，尝试从类路径加载
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(configPath)) {
                if (is != null) {
                    properties.load(is);
                    loaded = true;
                    logger.info("从类路径加载配置文件: {}", configPath);
                } else {
                    // 如果配置文件不存在，使用默认配置
                    logger.warn("配置文件不存在: {}，使用默认配置", configPath);
                    loadDefaultConfig();
                }
            } catch (IOException ex) {
                logger.error("加载配置文件时发生错误: {}", ex.getMessage());
                // 使用默认配置
                loadDefaultConfig();
            }
        }
    }
    
    /**
     * 加载默认配置
     */
    private void loadDefaultConfig() {
        // 默认配置
        properties.setProperty("nlp.language", "zh");
        properties.setProperty("causal.confidence.threshold", "0.5");
        properties.setProperty("preprocess.keep.punctuation", "true");
        properties.setProperty("preprocess.remove.numbers", "false");
        properties.setProperty("preprocess.remove.special.chars", "true");
        properties.setProperty("preprocess.normalize.to.chinese", "true");
        properties.setProperty("financial.dictionary.path", "dictionary/financial_terms.txt");
        
        loaded = true;
        logger.info("已加载默认配置");
    }
    
    /**
     * 获取字符串属性值
     * 
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static String getStringProperty(String key, String defaultValue) {
        if (!loaded) {
            logger.warn("配置尚未加载，使用默认值: {}", defaultValue);
            return defaultValue;
        }
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * 获取整数属性值
     * 
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static int getIntProperty(String key, int defaultValue) {
        if (!loaded) {
            logger.warn("配置尚未加载，使用默认值: {}", defaultValue);
            return defaultValue;
        }
        
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("属性值不是有效的整数: {}, 使用默认值: {}", value, defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * 获取双精度浮点数属性值
     * 
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static double getDoubleProperty(String key, double defaultValue) {
        if (!loaded) {
            logger.warn("配置尚未加载，使用默认值: {}", defaultValue);
            return defaultValue;
        }
        
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logger.warn("属性值不是有效的浮点数: {}, 使用默认值: {}", value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 获取布尔属性值
     * 
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        if (!loaded) {
            logger.warn("配置尚未加载，使用默认值: {}", defaultValue);
            return defaultValue;
        }
        
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        
        return Boolean.parseBoolean(value);
    }
}