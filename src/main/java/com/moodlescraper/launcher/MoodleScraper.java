package com.moodlescraper.launcher;

import com.moodlescraper.module.MoodleCourse;

public class MoodleScraper {

    public static void main(String[] args) {
        final String MOODLE_PAGE = args[0];
        final String SESSION_KEY = args[1];

        // MoodleCrawler crawler = new MoodleCrawler(SESSION_KEY, MOODLE_PAGE);
        // List<MoodleCourse> courses = crawler.scrape();

        MoodleCourse randomCourse = new MoodleCourse("https://moodle.kent.ac.uk/2023/course/view.php?id=897",
                SESSION_KEY);
                
        randomCourse.install();
    }

}
