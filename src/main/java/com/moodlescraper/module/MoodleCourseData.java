package com.moodlescraper.module;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.moodlescraper.utils.FileNameUtils;

import lombok.Data;

@Data
public class MoodleCourseData {

    private List<ResourceFile> resourceFiles;
    private List<ResourceFile> pastpapers;

    private String courseName;
    private int courseId;

    public MoodleCourseData(int courseId, List<ResourceFile> resourceFiles, List<ResourceFile> pastpapers, String courseName) {
        this.resourceFiles = resourceFiles;
        this.pastpapers = pastpapers;
        this.courseName = courseName;
        this.courseId = courseId;
    }

    public void addResourceFile(ResourceFile resourceFile) {
        this.resourceFiles.add(resourceFile);
    }

    public void addPastpaper(ResourceFile pastpaper) {
        this.pastpapers.add(pastpaper);
    }
}
