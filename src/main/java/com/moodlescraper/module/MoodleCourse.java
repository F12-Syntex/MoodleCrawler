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
    private final String COOKIE;

    private Logger logger;

    public MoodleCourse(String moodleCourseUrl, String COOKIE) {
        this.moodleCourseUrl = moodleCourseUrl;
        this.COOKIE = COOKIE;
        this.logger = LoggerFactory.buildDefaultLogger();
    }

    public MoodleCrawler install() {
        try {
            Document document = Jsoup.connect(this.moodleCourseUrl).cookie("Cookie", this.COOKIE).get();

            System.out.println(this.COOKIE);

            System.out.println(document.title());

            //find every image/document/file/video in the page, and print it's url and all the headers/sections for it
            Elements resources = document.select("a[href*=resource]");

            for (Element resource : resources) {
                String resourceUrl = resource.attr("href");
                logger.info("Resource URL: " + resourceUrl);

                Element resourceSection = resource.parent().parent().parent().parent().parent().parent().parent().parent();
                logger.info("Resource Section: " + resourceSection.text());
            }




        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Replace with the appropriate MoodleCrawler instance
    }

}
