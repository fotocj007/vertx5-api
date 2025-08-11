package com.game.core.config;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.Level;

/**
 * 日志配置管理器
 * 负责根据外部配置动态设置日志级别
 */
public class LoggingConfigManager {
    private static final Logger logger = LogManager.getLogger(LoggingConfigManager.class);
    
    /**
     * 应用日志配置
     * @param config 包含日志配置的JsonObject
     */
    public static void applyLoggingConfig(JsonObject config) {
        JsonObject loggingConfig = config.getJsonObject("logging");
        if (loggingConfig == null) {
            logger.warn("No logging configuration found, using default settings");
            return;
        }
        
        try {
            // 设置系统属性，供log4j2.xml使用
            String rootLevel = loggingConfig.getString("level", "info");
            System.setProperty("app.log.level", rootLevel);
            
            JsonObject consoleConfig = loggingConfig.getJsonObject("console");
            if (consoleConfig != null) {
                String consoleLevel = consoleConfig.getString("level", rootLevel);
                System.setProperty("app.log.console.level", consoleLevel);
                System.setProperty("app.log.console.enabled", String.valueOf(consoleConfig.getBoolean("enabled", true)));
            }
            
            JsonObject fileConfig = loggingConfig.getJsonObject("file");
            if (fileConfig != null) {
                String fileLevel = fileConfig.getString("level", rootLevel);
                System.setProperty("app.log.file.level", fileLevel);
                System.setProperty("app.log.file.enabled", String.valueOf(fileConfig.getBoolean("enabled", true)));
                System.setProperty("app.log.file.maxSize", fileConfig.getString("maxSize", "300MB"));
                System.setProperty("app.log.file.maxFiles", String.valueOf(fileConfig.getInteger("maxFiles", 10)));
            }
            
            JsonObject errorConfig = loggingConfig.getJsonObject("error");
            if (errorConfig != null) {
                String errorLevel = errorConfig.getString("level", "error");
                System.setProperty("app.log.error.level", errorLevel);
                System.setProperty("app.log.error.enabled", String.valueOf(errorConfig.getBoolean("enabled", true)));
            }
            
            // 动态更新Log4j2配置
            updateLog4j2Configuration(rootLevel);
            
            logger.info("Logging configuration applied successfully. Root level: {}", rootLevel);
            
        } catch (Exception e) {
            logger.error("Failed to apply logging configuration", e);
        }
    }
    
    /**
     * 动态更新Log4j2配置
     * @param level 新的日志级别
     */
    private static void updateLog4j2Configuration(String level) {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();
            
            // 更新根日志器级别
            LoggerConfig rootLoggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
            rootLoggerConfig.setLevel(Level.toLevel(level.toUpperCase()));
            
            // 更新self日志器级别
            LoggerConfig selfLoggerConfig = config.getLoggerConfig("self");
            if (selfLoggerConfig != null) {
                selfLoggerConfig.setLevel(Level.toLevel(level.toUpperCase()));
            }
            
            // 重新配置
            context.updateLoggers();
            
            logger.info("Log4j2 configuration updated dynamically to level: {}", level);
            
        } catch (Exception e) {
            logger.warn("Failed to update Log4j2 configuration dynamically: {}", e.getMessage());
        }
    }
    
    /**
     * 获取当前日志级别
     * @return 当前根日志器的级别
     */
    public static String getCurrentLogLevel() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig rootLoggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        return rootLoggerConfig.getLevel().toString().toLowerCase();
    }
}