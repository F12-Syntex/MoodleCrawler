package com.moodlescraper.launcher;

import java.util.List;

import com.moodlescraper.application.MoodleScraperView;
import com.moodlescraper.crawler.MoodleCrawler;
import com.moodlescraper.module.MoodleCourse;
import com.moodlescraper.utils.FileNameUtils;

public class MoodleScraper {

    public static void main(String[] args) {
        final String MOODLE_PAGE = args[0];
        final String SESSION_KEY = args[1];

        // MoodleCrawler crawler = new MoodleCrawler(SESSION_KEY, MOODLE_PAGE);
        // List<MoodleCourse> courses = crawler.scrape();

        // for (MoodleCourse course : courses) {
        //     String name = FileNameUtils.makeFolderNameSafe(course.getCourseName());
        // }
        

        // MoodleCourse randomCourse = new MoodleCourse("Software Engineering", "https://moodle.kent.ac.uk/2023/course/view.php?id=897",
        //         SESSION_KEY);
                
        // File isntallationFolder = new File("C:\\Users\\synte\\OneDrive - University of Kent\\Desktop\\moodle");
        // randomCourse.install(isntallationFolder);

        // randomCourse.getMetaData();
        MoodleScraperView view = new MoodleScraperView(SESSION_KEY);
        view.setVisible(true);
    }

}
