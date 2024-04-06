package com.moodlescraper.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.moodlescraper.logger.LoggerFactory;

import lombok.Data;

@Data
public class MoodleCrawler {

    private final String COOKIE;
    private final String MOODLE_PAGE;

    private final Logger logger;

    /**
     * @param COOKIE      The session key to scrape the moodle page with
     * @param MOODLE_PAGE the page url of the moodle pages to scrape.
     */
    public MoodleCrawler(String COOKIE, String MOODLE_PAGE) {
        this.COOKIE = COOKIE;
        this.MOODLE_PAGE = MOODLE_PAGE;

        this.logger = LoggerFactory.buildDefaultLogger(this.getClass().getName());
    }

    public List<MoodleCourse> scrape() {
        List<MoodleCourse> courses = new ArrayList<>();
        try {
            String url = this.MOODLE_PAGE;
            this.logger.info("Crawling \"" + this.MOODLE_PAGE + "\"");

            Document document = Jsoup.connect(url).header("Cookie", COOKIE).get();

            document.select("a").forEach(link -> {
                String pageUrl = link.attr("href");
                if (pageUrl.contains("/view.php?id=")) {
                    MoodleCourse course = new MoodleCourse(pageUrl);
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
