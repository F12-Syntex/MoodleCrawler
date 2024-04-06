package com.moodlescraper.launcher;

import java.util.List;

import com.moodlescraper.crawler.MoodleCrawler;
import com.moodlescraper.module.MoodleCourse;

public class MoodleScraper {

    public static void main(String[] args) {
        final String MOODLE_PAGE = args[0];
        final String SESSION_KEY = args[1];

        MoodleCrawler crawler = new MoodleCrawler(SESSION_KEY, MOODLE_PAGE);
        List<MoodleCourse> courses = crawler.scrape();

        MoodleCourse firstCourse = courses.get(0);
        firstCourse.scrape();
    }

}
