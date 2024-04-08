package com.moodlescraper.module;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.moodlescraper.logger.LoggerFactory;
import com.moodlescraper.utils.FileNameUtils;

import lombok.Data;

@Data
public class MoodleCourse {

    private String moodleCourseUrl;
    private String courseName;
    private String courseCode;
    private final String COOKIE;

    private Logger logger;

    public MoodleCourse(String courseCode, String courseName, String moodleCourseUrl, String COOKIE) {
        this.moodleCourseUrl = moodleCourseUrl;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.COOKIE = COOKIE;
        this.logger = LoggerFactory.buildDefaultLogger();
    }

    public MoodleCourseData getMetaData() {

        List<ResourceFile> resourceFiles = new ArrayList<>();
        List<ResourceFile> pastpapers = new ArrayList<>();

        try {

            Document document = Jsoup.connect(this.moodleCourseUrl).cookie("Cookie", this.COOKIE).get();

            String courseName = FileNameUtils.formatCourseName(document.title());
            int id = Integer.parseInt(this.moodleCourseUrl.split("id=")[1]);
            resourceFiles.addAll(this.getAllFilesInPage(document));

            // print all the urls in the page
            Elements links = document.select("a[href]");
            for (Element link : links) {
                String linkUrl = link.attr("abs:href");
                if (linkUrl.contains("pastpaper/")) {
                    Document pastpaperDocument = Jsoup.connect(linkUrl).cookie("Cookie", this.COOKIE).get();
                    pastpapers.addAll(this.getAllFilesInPage(pastpaperDocument));

                    // set the heading for the pastpapers to be PastPapers
                    for (ResourceFile pastpaper : pastpapers) {
                        pastpaper.setHeading("PastPapers" + File.separator + pastpaper.getName());
                    }
                }
            }

            MoodleCourseData courseData = new MoodleCourseData(id, resourceFiles, pastpapers, courseName);
            return courseData;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<ResourceFile> getAllFilesInPage(Document document) {
        List<ResourceFile> files = new ArrayList<>();
        Map<String, String> headingMap = this.getHeadingMap(document);

        // print the url of every file in the page
        Elements resources = document.select("a[href*=resource]");

        // print the resource and it's heading
        for (Element resource : resources) {
            String resourceUrl = resource.attr("abs:href");

            // get the file extention from the resource
            ResourceFile resourceFile = new ResourceFile(this, resourceUrl, resource.text(),
                    headingMap.getOrDefault(resourceUrl, resource.text()));
            files.add(resourceFile);
        }

        Elements pastpapers = document.select("a[href*=pastpaper]");
        for (Element pastpaper : pastpapers) {
            String pastpaperUrl = pastpaper.attr("abs:href");
            if (pastpaperUrl.contains("course.php")) {
                continue;
            }
            ResourceFile pastpaperFile = new ResourceFile(this, pastpaperUrl, pastpaper.text(),
                    headingMap.getOrDefault(pastpaperUrl, pastpaper.text()));
            files.add(pastpaperFile);
        }

        return files;
    }

    private Map<String, String> getHeadingMap(Document document) {
        Elements links = document.select("a[href]");
        Map<String, String> headingMap = new HashMap<>();

        for (Element link : links) {
            String linkUrl = link.attr("abs:href"); // Get the absolute URL of the link

            // Find the corresponding header, section, or collapse
            Element header = link.parents().select(
                    "div.section > div.sectionname, h3.sectionname, div.content > div.contentnode, div.activityinstance")
                    .first();

            if (header != null) {
                String headerText = header.text().trim();
                headingMap.put(linkUrl, headerText);
            }
        }

        return headingMap;
    }

    @Override
    public String toString() {
        return this.courseName;
    }

    /**
     * Download the entire page and CSS-related files to the folder
     *
     * @param folder
     */
    public void download(File folder, String parentFileName) {
        File courseFolder = new File(folder, FileNameUtils.makeFolderNameSafe(parentFileName));
        courseFolder.mkdirs(); // Create the course folder if it doesn't exist

        try {
            // Download the webpage HTML
            Document doc = Jsoup.connect(this.moodleCourseUrl).cookie("Cookie", COOKIE).get();
            File htmlFile = new File(courseFolder, "index.html");

            // Create a folder to store the CSS files
            File cssFolder = new File(courseFolder, "css");
            cssFolder.mkdir();

            FileUtils.writeStringToFile(htmlFile, doc.html(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }

        LoggerFactory.buildDefaultLogger()
                .info("Downloaded course " + courseName + " to " + courseFolder.getAbsolutePath());
    }

}
