package com.moodlescraper.launcher;

import com.moodlescraper.crawler.MoodleCrawler;

public class MoodleScraper {

    public static void main(String[] args) {
        final String MOODLE_PAGE = args[0];
        final String SESSION_KEY = args[1];

        MoodleCrawler crawler = new MoodleCrawler(SESSION_KEY, MOODLE_PAGE);
        crawler.scrape();
    }

}
