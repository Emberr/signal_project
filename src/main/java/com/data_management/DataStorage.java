package com.data_management;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alerts.AlertGenerator;
import com.alerts.AlertManager;
import com.cardiogenerator.outputs.WebSocketOutputStrategy;

/**
 * Manages storage and retrieval of patient data within a healthcare monitoring
 * system.
 * This class serves as a repository for all patient records, organized by
 * patient IDs.
 */
public class DataStorage {
    private Map<Integer, Patient> patientMap; // Stores patient objects indexed by their unique patient ID.

    private static DataStorage instance;

    /**
     * Constructs a new instance of DataStorage, initializing the underlying storage
     * structure.
     */
    private DataStorage() {
        this.patientMap = new HashMap<>();
    }

    /**
     * Adds or updates patient data in the storage.
     * If the patient does not exist, a new Patient object is created and added to
     * the storage.
     * Otherwise, the new data is added to the existing patient's records.
     *
     * @param patientId        the unique identifier of the patient
     * @param measurementValue the value of the health metric being recorded
     * @param recordType       the type of record, e.g., "HeartRate", "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in milliseconds
     */
    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        Patient patient = patientMap.get(patientId);
        if (patient == null) {
            patient = new Patient(patientId);
            patientMap.put(patientId, patient);
        }
        patient.addRecord(measurementValue, recordType, timestamp);
    }

    /**
     * Retrieves a list of PatientRecord objects for a specific patient, filtered by
     * a time range.
     *
     * @param patientId the unique identifier of the patient whose records are to be retrieved
     * @param startTime the start of the time range, in milliseconds
     * @param endTime   the end of the time range, in milliseconds
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        if (patient != null) {
            return patient.getRecords(startTime, endTime);
        }
        return new ArrayList<>(); // return an empty list if no patient is found
    }

    /**
     * Retrieves a collection of all patients stored in the data storage.
     *
     * @return a list of all patients
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * Retrieves a Patient object for a specific patient.
     *
     * @param patientId the unique identifier of the patient to be retrieved
     * @return the Patient object associated with the given patientId, or null if no such patient exists
     */
    public Patient getPatient(int patientId) {
        return patientMap.get(patientId);
    }

    public static DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    /**
     * The main method for the DataStorage class.
     * Initializes the system, reads data into storage, and continuously monitors
     * and evaluates patient data.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
//        DataStorage storage = new DataStorage();
//
//        DataReader reader = new WebSocketDataReader("ws://localhost:8080",storage);
//
//
//        reader.readData(storage);
//
//        // Example of using DataStorage to retrieve and print records for a patient
//        List<PatientRecord> records = storage.getRecords(2, 0, 1200);
//        for (PatientRecord record : records) {
//            System.out.println("Record for Patient ID: " + record.getPatientId() +
//                    ", Type: " + record.getRecordType() +
//                    ", Data: " + record.getMeasurementValue() +
//                    ", Timestamp: " + record.getTimestamp());
//        }
//
//        AlertManager alertManager = new AlertManager();
//        AlertGenerator alertGenerator = new AlertGenerator(storage, alertManager);
//
//        for (Patient patient : storage.getAllPatients()) {
//            alertGenerator.evaluateData(patient, 0, 1200);
//        }

    }
}
