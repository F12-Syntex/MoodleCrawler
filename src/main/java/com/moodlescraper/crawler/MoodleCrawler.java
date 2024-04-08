package com.moodlescraper.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.moodlescraper.logger.LoggerFactory;
import com.moodlescraper.module.MoodleCourse;

import lombok.Data;

@Data
public class MoodleCrawler {

    private final String COOKIE;
    private String moodlePageUrl;

    private final Logger logger;

    /**
     * @param COOKIE      The session key to scrape the moodle page with
     * @param moodlePageUrl the page url of the moodle pages to scrape.
     */
    public MoodleCrawler(String COOKIE, String moodlePageUrl) {
        this.COOKIE = COOKIE;
        this.moodlePageUrl = moodlePageUrl;

        this.logger = LoggerFactory.buildDefaultLogger();
    }

    public List<MoodleCourse> scrape() {
        List<MoodleCourse> courses = new ArrayList<>();
        try {
            String url = this.moodlePageUrl;
            this.logger.info("Crawling \"" + this.moodlePageUrl + "\"");

            Document document = Jsoup.connect(url).header("Cookie", COOKIE).get();

            document.select("a").forEach(link -> {
                String pageUrl = link.attr("href");
                if (pageUrl.contains("/view.php?id=")) {

                    String courseName = link.text();

                    MoodleCourse course = new MoodleCourse(courseName, pageUrl, COOKIE);
                    courses.add(course);
                }
            });

            this.logger.info("Found " + courses.size() + " courses");

            return courses;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return courses;
    }

}
