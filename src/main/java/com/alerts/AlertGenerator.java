package com.alerts;

import com.cardiogenerator.outputs.ConsoleOutputStrategy;
import com.cardiogenerator.outputs.OutputStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.*;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */

public class AlertGenerator {
    private DataStorage dataStorage;
    private AlertManager alertManager;

    /**
     * Constructs a new AlertGenerator with the specified data storage and alert manager.
     *
     * @param dataStorage the data storage instance to use for accessing patient data
     * @param alertManager the alert manager instance to use for logging alerts
     */
    public AlertGenerator(DataStorage dataStorage, AlertManager alertManager) {
        this.dataStorage = dataStorage;
        this.alertManager = alertManager;
    }

    /**
     * Evaluates patient data for the specified patient within the given time range.
     * This method checks for various health conditions and triggers alerts when
     * certain criteria are met.
     *
     * @param patient   the patient for whom data will be evaluated
     * @param startTime the start of the time range, in milliseconds
     * @param endTime   the end of the time range, in milliseconds
     */
    public void evaluateData(Patient patient, long startTime, long endTime) {
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(), startTime, endTime);
        PriorityQueue<PatientRecord> lastThreeSystolicReadings = new PriorityQueue<>(Comparator.comparingLong(PatientRecord::getTimestamp).reversed());
        PriorityQueue<PatientRecord> lastThreeDiastolicReadings = new PriorityQueue<>(Comparator.comparingLong(PatientRecord::getTimestamp).reversed());
        PriorityQueue<PatientRecord> lastSaturationReadings = new PriorityQueue<>(Comparator.comparingLong(PatientRecord::getTimestamp).reversed());
        PriorityQueue<PatientRecord> lastSystolicReading = new PriorityQueue<>(Comparator.comparingLong(PatientRecord::getTimestamp).reversed());
        PriorityQueue<PatientRecord> lastSaturationReading = new PriorityQueue<>(Comparator.comparingLong(PatientRecord::getTimestamp).reversed());
        Queue<Double> ecgWindow = new LinkedList<>();
        double ecgSum = 0;
        final int WINDOW_SIZE = 10;
        final double THRESHOLD = 1.5;

        for (PatientRecord record : records) {
            if (record.getRecordType().contains("Systolic")) {
                lastThreeSystolicReadings.add(record);
                lastSystolicReading.add(record);
                if (lastThreeSystolicReadings.size() > 3) {
                    lastThreeSystolicReadings.poll();
                }
                if (lastSystolicReading.size() > 1) {
                    lastSystolicReading.poll();
                }
                if (lastThreeSystolicReadings.size() == 3) {
                    if (checkTrendAlert(new ArrayList<>(lastThreeSystolicReadings))) {
                        Alert alert = new Alert(patient.getPatientIdString(), "Trend Alert: Systolic Blood Pressure", System.currentTimeMillis());
                        triggerAlert(alert);
                    }
                }
            }

            if (record.getRecordType().contains("Diastolic")) {
                lastThreeDiastolicReadings.add(record);
                if (lastThreeDiastolicReadings.size() > 3) {
                    lastThreeDiastolicReadings.poll();
                }
                if (lastThreeDiastolicReadings.size() == 3) {
                    if (checkTrendAlert(new ArrayList<>(lastThreeDiastolicReadings))) {
                        Alert alert = new Alert(patient.getPatientIdString(), "Trend Alert: Diastolic Blood Pressure", System.currentTimeMillis());
                        triggerAlert(alert);
                    }
                }
            }

            if (record.getRecordType().contains("Saturation")) {
                if (checkLowSaturationAlert(record)) {
                    Alert alert = new Alert(patient.getPatientIdString(), "Low Oxygen Saturation", System.currentTimeMillis());
                    triggerAlert(alert);
                }
                lastSaturationReadings.add(record);
                lastSaturationReading.add(record);
                if (lastSaturationReadings.size() > 2) {
                    lastSaturationReadings.poll();
                }
                if (lastSaturationReading.size() > 1) {
                    lastSaturationReading.poll();
                }
                if (lastSaturationReadings.size() == 2) {
                    if (checkRapidDropAlert(new ArrayList<>(lastSaturationReadings))) {
                        Alert alert = new Alert(patient.getPatientIdString(), "Rapid Drop in Oxygen Saturation", System.currentTimeMillis());
                        triggerAlert(alert);
                    }
                }
            }

            if (!lastSystolicReading.isEmpty() && !lastSaturationReading.isEmpty()) {
                if (checkHypotensiveHypoxemiaAlert(lastSystolicReading.peek(), lastSaturationReading.peek())) {
                    Alert alert = new Alert(patient.getPatientIdString(), "Hypotensive Hypoxemia", System.currentTimeMillis());
                    triggerAlert(alert);
                }
            }

            if (record.getRecordType().contains("ECG")) {
                if (evaluateECGData(record, ecgWindow, ecgSum, WINDOW_SIZE, THRESHOLD)) {
                    Alert alert = new Alert(patient.getPatientIdString(), "Abnormal ECG Peak", System.currentTimeMillis());
                    triggerAlert(alert);
                }
            }

            if (checkCriticalThresholdAlert(record)) {
                Alert alert = new Alert(patient.getPatientIdString(), "Critical Blood Pressure", System.currentTimeMillis());
                triggerAlert(alert);
            }
        }
    }
    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        OutputStrategy outputStrategy = new ConsoleOutputStrategy();
        outputStrategy.output(Integer.parseInt(alert.getPatientId()), alert.getTimestamp(), alert.getCondition(), "ALERT");
        alertManager.logAlert(alert);
    }

    /**
     * Checks if the trend alert condition is met for the given list of patient records.
     *
     * @param records the list of patient records to check
     * @return true if the trend alert condition is met, false otherwise
     */
    public boolean checkTrendAlert(List<PatientRecord> records) {
        Collections.sort(records, Comparator.comparingLong(PatientRecord::getTimestamp));
        double rec1 = records.get(0).getMeasurementValue();
        double rec2 = records.get(1).getMeasurementValue();
        double rec3 = records.get(2).getMeasurementValue();
        return (rec1 - rec2 > 10 && rec2 - rec3 > 10) || (rec1 - rec2 < -10 && rec2 - rec3 < -10);
    }

    /**
     * Checks if the low saturation alert condition is met for the given patient record.
     *
     * @param record the patient record to check
     * @return true if the low saturation alert condition is met, false otherwise
     */
    public boolean checkCriticalThresholdAlert(PatientRecord record) {
        if (!record.getRecordType().contains("Pressure")) {
            return false;
        }
        double pressure = record.getMeasurementValue();
        return (record.getRecordType().contains("Systolic") && (pressure > 180 || pressure < 90)) ||
                (record.getRecordType().contains("Diastolic") && (pressure > 120 || pressure < 60));
    }

    /**
     * Checks if the low saturation alert condition is met for the given patient record.
     *
     * @param record the patient record to check
     * @return true if the low saturation alert condition is met, false otherwise
     */
    public boolean checkLowSaturationAlert(PatientRecord record) {
        if (!record.getRecordType().contains("Saturation")) {
            return false;
        }
        double saturation = record.getMeasurementValue();
        return saturation < 92;
    }

    /**
     * Checks if the rapid drop alert condition is met for the given list of patient records.
     *
     * @param records the list of patient records to check
     * @return true if the rapid drop alert condition is met, false otherwise
     */
    public boolean checkRapidDropAlert(List<PatientRecord> records) {
        Collections.sort(records, Comparator.comparingLong(PatientRecord::getTimestamp));
        double saturation1 = records.get(0).getMeasurementValue();
        double saturation2 = records.get(1).getMeasurementValue();
        return saturation1 - saturation2 > 5 && records.get(1).getTimestamp() - records.get(0).getTimestamp() <= 600000; // 10 minutes in milliseconds
    }

    /**
     * Checks if the hypotensive hypoxemia alert condition is met for the given systolic and saturation records.
     *
     * @param systolicRecord the systolic record to check
     * @param saturationRecord the saturation record to check
     * @return true if the hypotensive hypoxemia alert condition is met, false otherwise
     */
    public boolean checkHypotensiveHypoxemiaAlert(PatientRecord systolicRecord, PatientRecord saturationRecord) {
        if (systolicRecord == null || saturationRecord == null) {
            return false;
        }
        if (!systolicRecord.getRecordType().contains("Pressure") || !saturationRecord.getRecordType().contains("Saturation")) {
            return false;
        }
        double systolic = systolicRecord.getMeasurementValue();
        double saturation = saturationRecord.getMeasurementValue();
        return systolic < 90 && saturation < 92;
    }

    /**
     * Evaluates the ECG data for the given patient record and checks if the abnormal ECG peak condition is met.
     *
     * @param record the patient record to evaluate
     * @param ecgWindow the queue of recent ECG readings
     * @param ecgSum the sum of the ECG readings in the window
     * @param windowSize the size of the sliding window
     * @param threshold the threshold for abnormal peak
     * @return true if the abnormal ECG peak condition is met, false otherwise
     */
    public boolean evaluateECGData(PatientRecord record, Queue<Double> ecgWindow, double ecgSum, int windowSize, double threshold) {
        double heartRate = record.getMeasurementValue();
        ecgSum += heartRate;
        ecgWindow.add(heartRate);
        if (ecgWindow.size() > windowSize) {
            ecgSum -= ecgWindow.poll();
        }
        double average = ecgSum / ecgWindow.size();
        return heartRate > average * threshold;
    }

}

