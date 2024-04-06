package com.moodlescraper.logger;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Represents a message that has been logged by the system.
 * @author sk902
 
 */
@AllArgsConstructor
@Data
public class LoggedMessage {
    private Timestamp timestamp;
    private String sender;
    private String message;
}