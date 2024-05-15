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
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage, AlertManager alertManager) {
        this.dataStorage = dataStorage;
        this.alertManager = alertManager;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient, long startTime, long endTime) {
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(), startTime, endTime);
        PriorityQueue<PatientRecord> lastThreeSystolicReadings = new PriorityQueue<>(Comparator.comparingLong(PatientRecord::getTimestamp).reversed());
        PriorityQueue<PatientRecord> lastThreeDiastolicReadings = new PriorityQueue<>(Comparator.comparingLong(PatientRecord::getTimestamp).reversed());
        PriorityQueue<PatientRecord> lastSaturationReadings = new PriorityQueue<>(Comparator.comparingLong(PatientRecord::getTimestamp).reversed());
        PriorityQueue<PatientRecord> lastSystolicReading = new PriorityQueue<>(Comparator.comparingLong(PatientRecord::getTimestamp).reversed());
        PriorityQueue<PatientRecord> lastSaturationReading = new PriorityQueue<>(Comparator.comparingLong(PatientRecord::getTimestamp).reversed());

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
                lastSaturationReadings.add(record);
                lastSaturationReading.add(record);
                if (lastSaturationReadings.size() > 2) {
                    lastSaturationReadings.poll();
                }
                if (lastSaturationReading.size() > 1) {
                    lastSaturationReading.poll();
                }
                if (!lastSaturationReadings.isEmpty()) {
                    if (checkLowSaturationAlert(lastSaturationReadings.peek())) {
                        Alert alert = new Alert(patient.getPatientIdString(), "Low Oxygen Saturation", System.currentTimeMillis());
                        triggerAlert(alert);
                    }
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

            if (record.getRecordType().contains("Rate")) {
                if (checkAbnormalHeartRateAlert(record.getMeasurementValue())) {
                    Alert alert = new Alert(patient.getPatientIdString(), "Abnormal Heart Rate", System.currentTimeMillis());
                    triggerAlert(alert);
                }
            }

            if (record.getRecordType().contains("ECG")) {
                if (checkIrregularBeatAlert(record)) {
                    Alert alert = new Alert(patient.getPatientIdString(), "Irregular Heart Beat", System.currentTimeMillis());
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

    public boolean checkTrendAlert(List<PatientRecord> records) {
        Collections.sort(records, Comparator.comparingLong(PatientRecord::getTimestamp));
        double rec1 = records.get(0).getMeasurementValue();
        double rec2 = records.get(1).getMeasurementValue();
        double rec3 = records.get(2).getMeasurementValue();
        return (rec1 - rec2 > 10 && rec2 - rec3 > 10) || (rec1 - rec2 < -10 && rec2 - rec3 < -10);
    }

    public boolean checkCriticalThresholdAlert(PatientRecord record) {
        if (!record.getRecordType().contains("Pressure")) {
            return false;
        }
        double pressure = record.getMeasurementValue();
        return (record.getRecordType().contains("Systolic") && (pressure > 180 || pressure < 90)) ||
                (record.getRecordType().contains("Diastolic") && (pressure > 120 || pressure < 60));
    }

    public boolean checkLowSaturationAlert(PatientRecord record) {
        if (!record.getRecordType().contains("Saturation")) {
            return false;
        }
        double saturation = record.getMeasurementValue();
        return saturation < 92;
    }

    public boolean checkRapidDropAlert(List<PatientRecord> records) {
        Collections.sort(records, Comparator.comparingLong(PatientRecord::getTimestamp));
        double saturation1 = records.get(0).getMeasurementValue();
        double saturation2 = records.get(1).getMeasurementValue();
        return saturation1 - saturation2 > 5 && records.get(1).getTimestamp() - records.get(0).getTimestamp() <= 600000; // 10 minutes in milliseconds
    }

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

    public boolean checkAbnormalHeartRateAlert(double heartRate) {
        return heartRate < 50 || heartRate > 100;
    }

    public boolean checkIrregularBeatAlert(PatientRecord record) {
        if (!record.getRecordType().contains("ECG")) {
            return false;
        }
        double heartRate = record.getMeasurementValue();
        return heartRate < 50 || heartRate > 100;
    }
}

