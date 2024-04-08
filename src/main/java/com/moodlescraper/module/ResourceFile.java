package com.moodlescraper.module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.moodlescraper.utils.FileNameUtils;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResourceFile {
    private String url;
    private String name;
    private String heading;
    private MoodleCourse parent;
    // private String extention;

    public ResourceFile(MoodleCourse parent, String url, String name, String heading) {
        this.url = url;
        this.name = FileNameUtils.formatName(name);
        this.heading = FileNameUtils.formatName(heading);
        this.parent = parent;
        // this.extention = "pdf";
    }

    public File install(File folder) throws IOException {

        File courseFolder = new File(folder, FileNameUtils.makeFolderNameSafe(parent.getCourseName()));

        if (!courseFolder.exists()) {
            // System.out.println(courseFolder.getAbsolutePath() + " - " +
            // courseFolder.mkdir());
            courseFolder.mkdir();
        }

        File sectionFolder = new File(courseFolder, heading);
        if (!sectionFolder.exists()) {
            // System.out.println(sectionFolder.getAbsolutePath() + " - " +
            // sectionFolder.mkdir());
            sectionFolder.mkdir();
        }

        String cookies = parent.getCOOKIE();

        URL url = new URL(this.url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Cookie", cookies);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = this.name;
            String contentDisposition = connection.getHeaderField("Content-Disposition");

            if (contentDisposition != null && contentDisposition.indexOf("filename=") > -1) {
                int index = contentDisposition.indexOf("filename=");
                fileName = contentDisposition.substring(index + 10, contentDisposition.length() - 1);
            } else {
                fileName = this.url.substring(this.url.lastIndexOf("/") + 1);
            }

            File downloadedFile = new File(sectionFolder, fileName);
            InputStream inputStream = connection.getInputStream();
            FileUtils.copyInputStreamToFile(inputStream, downloadedFile);
            connection.disconnect();

            return downloadedFile;

        } else {
            System.out.println("Failed to download file. Response code: " + responseCode);
        }

        connection.disconnect();
        return null;
    }
}
