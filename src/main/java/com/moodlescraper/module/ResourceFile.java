package com.moodlescraper.module;

import java.io.File;

import com.moodlescraper.utils.FileNameUtils;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResourceFile {
    private String url;
    private String name;
    private String heading;
    private String extention;

    public ResourceFile(String url, String name, String heading) {
        this.url = url;
        this.name = FileNameUtils.formatName(name);
        this.heading = FileNameUtils.formatName(heading);
        this.extention = "pdf";
    }

    public void install(File folder) {}
}
