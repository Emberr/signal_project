package com.data_management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileDataReader implements DataReader {
    private String filePath;

    /**
     * Constructs a new FileDataReader with the specified file path.
     *
     * @param filePath the path to the file containing the data
     */
    public FileDataReader(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads patient data from the file specified in the constructor and adds it to
     * the provided DataStorage object.
     *
     * @param dataStorage the DataStorage object to which the data will be added
     * @throws IOException if an error occurs while reading the file
     */
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
