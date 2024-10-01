package com.bezkoder.springjwt.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueFilenameGenerator {

    private static final AtomicInteger counter = new AtomicInteger(0);



    public static String generateUniqueFilename(Long userId, String filename) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

        String timestamp = now.format(formatter);

        int uniqueNumber = counter.incrementAndGet();

        return userId + timestamp + uniqueNumber + getFileExtension(filename);
    }

    public static String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex >= 0) {
            return filename.substring(dotIndex);
        } else {
            return "";
        }
    }
}