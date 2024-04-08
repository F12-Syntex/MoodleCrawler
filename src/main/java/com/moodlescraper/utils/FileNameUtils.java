package com.moodlescraper.utils;

public class FileNameUtils {

    public static String formatCourseName(String name) {
        String folderName = name.toString().replaceAll("[^a-zA-Z0-9.-]", " ").split("\\d+")[0]
                .replace("Module", "").trim();
        return folderName;
    }

    public static String formatName(String name) {
        String folderName = name.toString().replaceAll("[^a-zA-Z0-9.-[']]", " ").replaceAll("\\s+", " ").trim();
        //replace backslashes with underscores
        folderName = folderName.replaceAll("\\\\", "/");
        return folderName;
    }

    public static String makeFolderNameSafe(String name) {
        String invalidChars = "\\/:*?\"<>|";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (invalidChars.indexOf(c) != -1) {
                sb.append('_');
            } else {
                sb.append(c);
            }
        }
        String result = sb.toString().replaceAll("^\\.", "").replaceAll("\\.$", "");
        result = result.replaceAll("\\.+", "_");
        result = result.replaceAll("_+", "_");
        result = result.replaceAll("^_+", "").replaceAll("_+$", "");

        if (result.isEmpty()) {
            result = "default_name";
        }

        return result;
    }
}
