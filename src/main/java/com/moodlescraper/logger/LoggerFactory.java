package com.moodlescraper.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for building loggers
 * 
 * @author sk902
 */
public class LoggerFactory {

    /**
     * Build a logger with the given name and log entries if logEntries is true
     * 
     * @param name       the name of the logger
     * @param logEntries whether to log entries
     * @return
     */
    public static Logger buildDefaultLogger(String name, boolean logEntries) {
        Logger logger = Logger.getLogger(name);
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new LoggingFormatter());
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);

        if (!logEntries) {
            logger.setLevel(Level.OFF);
        }

        return logger;
    }

    /**
     * Build a logger with the given name and log entries if logEntries is true
     * 
     * @param name the name of the logger
     * @return
     */
    public static Logger buildDefaultLogger(String name) {
        Logger logger = Logger.getLogger(name);
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new LoggingFormatter());
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);

        return logger;
    }

    /**
     * Build a logger with the given name and log entries if logEntries is true
     * 
     * @param name the name of the logger
     * @return
     */
    public static Logger buildDefaultLogger() {
        Logger logger = Logger.getAnonymousLogger();
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new LoggingFormatter());
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);

        return logger;
    }

}
