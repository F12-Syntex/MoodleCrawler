package com.moodlescraper.launcher;

import com.moodlescraper.application.MoodleScraperView;

public class MoodleScraper {

    public static void main(String[] args) {
        final String MOODLE_PAGE = args[0];
        final String SESSION_KEY = args[1];

        // MoodleCrawler crawler = new MoodleCrawler(SESSION_KEY, MOODLE_PAGE);
        // List<MoodleCourse> courses = crawler.scrape();

        // for (MoodleCourse course : courses) {
        //     String name = FileNameUtils.makeFolderNameSafe(course.getCourseName());
        // }
        

        // MoodleCourse randomCourse = new MoodleCourse("COMPXXXX", "Software Engineering", "https://moodle.kent.ac.uk/2023/course/view.php?id=897",
        //         SESSION_KEY);
        // randomCourse.getMetaData();
                
        // File isntallationFolder = new File("C:\\Users\\synte\\OneDrive - University of Kent\\Desktop\\moodle");
        // // randomCourse.install(isntallationFolder);
        // try {
        //     randomCourse.getMetaData().getResourceFiles().get(0).install(isntallationFolder);
        // } catch (IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }

        

        // randomCourse.getMetaData();
        MoodleScraperView view = new MoodleScraperView(SESSION_KEY);
        view.setVisible(true);
    }

}
