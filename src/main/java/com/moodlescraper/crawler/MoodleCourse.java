package com.moodlescraper.crawler;

import lombok.Data;

@Data
public class MoodleCourse {

    private final String MOODLE_COURSE_URL;

    public MoodleCourse(String MOODLE_COURSE_URL) {
        this.MOODLE_COURSE_URL = MOODLE_COURSE_URL;
    }

}
