package com.data_management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileDataReader implements DataReader {
    private String filePath;

    public FileDataReader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");
                int patientId = Integer.parseInt(parts[0]);
                String measurementType = parts[1];
                double measurementValue = Double.parseDouble(parts[2]);
                long timestamp = Long.parseLong(parts[3]);
                PatientRecord record = new PatientRecord(patientId, measurementValue, measurementType, timestamp);
                dataStorage.addPatientData(patientId, measurementValue, measurementType, timestamp);
            }
        }
    }
}
