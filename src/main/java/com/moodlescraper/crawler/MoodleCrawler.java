package com.moodlescraper.crawler;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import lombok.Data;

@Data
public class MoodleCrawler {

    private final String COOKIE;
    private final String MOODLE_PAGE;

    /**
     * @param COOKIE      The session key to scrape the moodle page with
     * @param MOODLE_PAGE the page url of the moodle pages to scrape.
     */
    public MoodleCrawler(String COOKIE, String MOODLE_PAGE) {
        this.COOKIE = COOKIE;
        this.MOODLE_PAGE = MOODLE_PAGE;
    }

    public void scrape() {
        try {
            // Include the session key in the URL
            String url = this.MOODLE_PAGE;

            Document document = Jsoup.connect(url).header("Cookie", COOKIE).get();



            //print all then links on the page
            document.select("a").forEach(link -> {
                System.out.println(link.attr("href"));
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
