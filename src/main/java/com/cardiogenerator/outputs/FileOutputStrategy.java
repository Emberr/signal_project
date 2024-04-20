package com.cardiogenerator.outputs; //changed package name to not include underscore

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

public class FileOutputStrategy implements OutputStrategy {

    private String baseDirectory; //changed name to lowerCamelCase

    public final ConcurrentHashMap<String, String> FILE_MAP = new ConcurrentHashMap<>(); //changed name to UPPER_SNAKE_CASE

    public FileOutputStrategy(String baseDirectory) { //changed class name and constructor to UpperCamelCase

        this.baseDirectory = baseDirectory;
    }

    @Override
    public void output(int patientId, long timeStamp, String label, String data) { //changed name timestamp to lowerCamelCase timeStamp
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        String filePath = FILE_MAP.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString()); //changed name to lowerCamelCase

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timeStamp, label, data);
        } catch (IOException e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}