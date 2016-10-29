package xyz.aadev.aalib.common.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import xyz.aadev.aalib.common.util.ModContainerHelper;

public class Logger {
    public static void log(Level logLevel, String message) {
        String modName = ModContainerHelper.getModNameFromActiveContainer();
        org.apache.logging.log4j.Logger logger = LogManager.getLogger(modName);
        logger.log(logLevel, message);
    }

    public static void all(String message) {
        log(Level.ALL, message);
    }

    public static void debug(String message) {
        log(Level.DEBUG, message);
    }

    public static void trace(String message) {
        log(Level.TRACE, message);
    }

    public static void fatal(String message) {
        log(Level.FATAL, message);
    }

    public static void error(String message) {
        log(Level.ERROR, message);
    }

    public static void warn(String message) {
        log(Level.WARN, message);
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void off(String message) {
        log(Level.OFF, message);
    }
}
