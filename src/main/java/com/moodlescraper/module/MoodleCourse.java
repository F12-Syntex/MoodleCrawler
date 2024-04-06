package com.moodlescraper.module;

import java.io.IOException;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.moodlescraper.crawler.MoodleCrawler;
import com.moodlescraper.logger.LoggerFactory;

import lombok.Data;

@Data
public class MoodleCourse {

    private String moodleCourseUrl;
    private String sessionKey;

    private Logger logger;

    public MoodleCourse(String moodleCourseUrl, String sessionKey) {
        this.moodleCourseUrl = moodleCourseUrl;
        this.sessionKey = sessionKey;
        this.logger = LoggerFactory.buildDefaultLogger();
    }

    public MoodleCrawler scrape() {
        try {
            Document document = Jsoup.connect(this.moodleCourseUrl).get();

            // Get all headers
            Elements headers = document.select("h1, h2, h3, h4, h5, h6");
            for (Element header : headers) {
                System.out.println("Header: " + header.text());
            }

            // Get all links
            Elements links = document.select("a[href]");
            for (Element link : links) {
                System.out.println("Link: " + link.attr("href"));
            }

            // Get all documents (assuming documents are represented by <a> tags with a
            // specific class)
            Elements documents = document.select("a.document");
            for (Element doc : documents) {
                System.out.println("Document: " + doc.attr("href"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Replace with the appropriate MoodleCrawler instance
    }

}
