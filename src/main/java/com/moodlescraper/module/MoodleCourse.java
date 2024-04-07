package com.moodlescraper.module;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
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

    public MoodleCrawler install(File folder) {
        try {
            Document document = Jsoup.connect(this.moodleCourseUrl).cookie("Cookie", this.COOKIE).get();

            System.out.println(this.COOKIE);

            System.out.println(document.title());

            // find every image/document/file/video in the page, and print it's url and all
            // the headers/sections for it
            Elements resources = document.select("a[href*=resource]");

            String folderName = document.title().toString().replaceAll("[^a-zA-Z0-9.-]", " ").split("\\d+")[0]
                    .replace("Module", "").trim();

            // create the folder called document.title in the folder
            File courseFolder = new File(folder, folderName);

            // create the folder
            boolean folderCreated = courseFolder.mkdir();

            System.out.println("Folder created: " + folderCreated);

            // for every reasource, print it's url and save it in the folder, where the file
            // should be organised in the proper folders, using the headers/sections
            for (Element resource : resources) {
                String resourceUrl = resource.attr("href");
                String resourceTitle = resource.text();

                System.out.println("Resource URL: " + resourceUrl);
                System.out.println("Resource Title: " + resourceTitle);

                // create the folder for the resource
                File resourceFolder = new File(courseFolder, resourceTitle);

                // create the folder
                boolean resourceFolderCreated = resourceFolder.mkdir();

                System.out.println("Resource Folder created: " + resourceFolderCreated);

                // download the Resource
                Document resourceDocument = Jsoup.connect(resourceUrl).cookie("Cookie", this.COOKIE).get();

                // save the resource in the resource folder
                File resourceFile = new File(resourceFolder, resourceUrl.substring(resourceUrl.lastIndexOf("/") + 1));
                FileUtils.copyURLToFile(new URL(resourceUrl), resourceFile);

                System.out.println("Resource File created: " + resourceFile.exists());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Replace with the appropriate MoodleCrawler instance
    }

}
