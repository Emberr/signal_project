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
    private long previousTime = 0;

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
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(),1600000000000L, 1700000000000L);

        PriorityQueue<PatientRecord> lastThreeSystolicReadings = new PriorityQueue<>(Comparator.comparingLong(PatientRecord::getTimestamp).reversed());
        PriorityQueue<PatientRecord> lastThreeDiastolicReadings = new PriorityQueue<>(Comparator.comparingLong(PatientRecord::getTimestamp).reversed());
        PriorityQueue<PatientRecord> lastSaturationReadings = new PriorityQueue<>(Comparator.comparingLong(PatientRecord::getTimestamp).reversed());

        for (PatientRecord record : records) {
            if (record.getRecordType().contains("Systolic")) {
                lastThreeSystolicReadings.add(record);
                if (lastThreeSystolicReadings.size() > 3) {
                    lastThreeSystolicReadings.poll();
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
                if (lastSaturationReadings.size() > 2) {
                    lastSaturationReadings.poll();
                }
                if (lastSaturationReadings.size() == 2) {
                    if (checkRapidDropAlert(new ArrayList<>(lastSaturationReadings))) {
                        Alert alert = new Alert(patient.getPatientIdString(), "Rapid Drop in Oxygen Saturation", System.currentTimeMillis());
                        triggerAlert(alert);
                    }
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

            if (checkHypotensiveHypoxemiaAlert(record)) {
                Alert alert = new Alert(patient.getPatientIdString(), "Hypotensive Hypoxemia", System.currentTimeMillis());
                triggerAlert(alert);
            }
        }
    }

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

    public boolean checkHypotensiveHypoxemiaAlert(PatientRecord record) {
        if (!record.getRecordType().contains("Pressure") && !record.getRecordType().contains("Saturation")) {
            return false;
        }
        double systolic = record.getRecordType().contains("Pressure") ? record.getMeasurementValue() : 0;
        double saturation = record.getRecordType().contains("Saturation") ? record.getMeasurementValue() : 0;
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
//    public void evaluateData(Patient patient) {
//        long time = System.currentTimeMillis() + previousTime;
//        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(),1600000000000L, 1700000000000L);
//        previousTime = time;
//        PatientRecord saturation = null;
//        PatientRecord previousSaturation = null;
//        PatientRecord previousSPressure = null;
//        PatientRecord previousDPressure = null;
//        PatientRecord sPressure = null;
//        PatientRecord dPressure = null;
//        PatientRecord previousHeartRate = null;
//        PatientRecord heartRate = null;
//        for (int i = records.size()-1; i >= 0; i--) {
//            if (records.get(i).getRecordType().contains("Pressure")) {
//                if (records.get(i).getRecordType().contains("Systolic")) {
//                    if (sPressure == null) {
//                        sPressure = records.get(i);
//                    } else if (previousSPressure == null ) previousSPressure = records.get(i);
//                    if (previousSPressure != null && checkTrendAlert(records.get(i), previousSPressure, "Systolic")) {
//                        Alert alert = new Alert(patient.getPatientIdString(), "Trend Alert: Systolic", System.currentTimeMillis());
//                        triggerAlert(alert);
//                    }
//                    previousSPressure = records.get(i);
//                } else {
//                    if (dPressure == null) {
//                        dPressure = records.get(i);
//                    } else if (previousDPressure == null) previousDPressure = records.get(i);
//                    if (previousDPressure != null && checkTrendAlert(records.get(i), previousDPressure, "Diastolic")) {
//                        Alert alert = new Alert(patient.getPatientIdString(), "Trend Alert: Diastolic", System.currentTimeMillis());
//                        triggerAlert(alert);
//                    }
//                }
//            }
//            if (records.get(i).getRecordType().contains("ECG") && heartRate != null && previousHeartRate == null) {
//                previousHeartRate = records.get(i);
//                if (checkIrregularBeatAlert(records.get(i), previousHeartRate)) {
//                    Alert alert = new Alert(patient.getPatientIdString(), "Irregular Heart Beat", System.currentTimeMillis());
//                    triggerAlert(alert);
//                }
//            } else if (records.get(i).getRecordType().contains("ECG")&& heartRate == null) {
//                heartRate = records.get(i);
//                if (checkAbnormalHeartRateAlert(records.get(i).getMeasurementValue())) {
//                    Alert alert = new Alert(patient.getPatientIdString(), "Abnormal Heart Rate", System.currentTimeMillis());
//                    triggerAlert(alert);
//                }
//            }
//            if (records.get(i).getRecordType().contains("Saturation") && saturation != null) {
//                previousSaturation = records.get(i);
//                if (checkRapidDropAlert(previousSaturation,saturation)){
//                    Alert alert = new Alert(patient.getPatientIdString(), "Rapid Drop in Oxygen Saturation", System.currentTimeMillis());
//                    triggerAlert(alert);
//                }
//            } else if (records.get(i).getRecordType().contains("Saturation") && saturation == null) {
//                saturation = records.get(i);
//                if (checkLowSaturationAlert(records.get(i))) {
//                    Alert alert = new Alert(patient.getPatientIdString(), "Low Oxygen Saturation", System.currentTimeMillis());
//                    triggerAlert(alert);
//                }
//            } if (sPressure != null && dPressure != null && checkCriticalThresholdAlert(sPressure, dPressure)) {
//                Alert alert = new Alert(patient.getPatientIdString(), "Critical Blood Pressure", System.currentTimeMillis());
//                triggerAlert(alert);
//            } if (sPressure != null && saturation != null && checkHypotensiveHypoxemiaAlert(sPressure, saturation)) {
//                Alert alert = new Alert(patient.getPatientIdString(), "Hypotensive Hypoxemia", System.currentTimeMillis());
//                triggerAlert(alert);
//            }
//
//        }
//    }
//
//    /**
//     * Triggers an alert for the monitoring system. This method can be extended to
//     * notify medical staff, log the alert, or perform other actions. The method
//     * currently assumes that the alert information is fully formed when passed as
//     * an argument.
//     *
//     * @param alert the alert object containing details about the alert condition
//     */
//    private void triggerAlert(Alert alert) {
//        OutputStrategy outputStrategy = new ConsoleOutputStrategy();
//        outputStrategy.output(Integer.parseInt(alert.getPatientId()), alert.getTimestamp(), alert.getCondition(), "ALERT");
//        alertManager.logAlert(alert);
//
//    }
//    public boolean checkTrendAlert(PatientRecord record1, PatientRecord record2, String recordType) {
//        if (!record1.getRecordType().contains(recordType) && !record2.getRecordType().contains(recordType)) {
//            return false;
//        }
//        double rec1 = record1.getMeasurementValue();
//        double rec2 = record2.getMeasurementValue();
//        return Math.abs(rec1 - rec2) > 10;
//    }
//    public boolean checkCriticalThresholdAlert(PatientRecord record1, PatientRecord record2) {
//        if (!record1.getRecordType().contains("Pressure") && !record2.getRecordType().contains("Pressure")) {
//            return false;
//        }
//        double systolic;
//        double diastolic;
//        if (record1.getRecordType().contains("Systolic")) {
//            systolic = record1.getMeasurementValue();
//            diastolic = record2.getMeasurementValue();
//        } else {
//            systolic = record2.getMeasurementValue();
//            diastolic = record1.getMeasurementValue();
//        }
//        return systolic > 180 || systolic < 90 || diastolic > 120 || diastolic < 60;
//
//    }
//    public boolean checkLowSaturationAlert(PatientRecord record) {
//        if (!record.getRecordType().equals("Oxygen Saturation")) {
//            return false;
//        }
//        double saturation = record.getMeasurementValue();
//        return saturation < 92;
//    }
//    public boolean checkRapidDropAlert(PatientRecord record1, PatientRecord record2) {
//        if (!record1.getRecordType().contains("Saturation") && !record2.getRecordType().contains("Saturation")) {
//            return false;
//        }
//        double saturation1;
//        double saturation2;
//        if (record1.getRecordType().contains("Saturation")) {
//            saturation1 = record1.getMeasurementValue();
//            saturation2 = record2.getMeasurementValue();
//        } else {
//            saturation1 = record2.getMeasurementValue();
//            saturation2 = record1.getMeasurementValue();
//        }
//        return saturation1 - saturation2 > 5;
//    }
//    public boolean checkHypotensiveHypoxemiaAlert(PatientRecord record1, PatientRecord record2) {
//        if ((!record1.getRecordType().contains("Pressure") && !record2.getRecordType().contains("Saturation"))&& (!record1.getRecordType().contains("Saturation") && !record2.getRecordType().contains("Pressure"))) {
//            return false;
//        }
//        double systolic;
//        double saturation;
//        if (record1.getRecordType().contains("Pressure")) {
//            systolic = record1.getMeasurementValue();
//            saturation = record2.getMeasurementValue();
//        } else {
//            systolic = record2.getMeasurementValue();
//            saturation = record1.getMeasurementValue();
//        }
//        return systolic < 90 && saturation < 92;
//    }
//    public boolean checkAbnormalHeartRateAlert(double heartRate) {
//        return heartRate < 50 || heartRate > 100;
//    }
//
//    public boolean checkIrregularBeatAlert(PatientRecord record1, PatientRecord record2) {
//        if (!record1.getRecordType().contains("HeartRate") && !record2.getRecordType().contains("HeartRate")) {
//            return false;
//        }
//        double heartRate1;
//        double heartRate2;
//        if (record1.getRecordType().contains("HeartRate")) {
//            heartRate1 = record1.getMeasurementValue();
//            heartRate2 = record2.getMeasurementValue();
//        } else {
//            heartRate1 = record2.getMeasurementValue();
//            heartRate2 = record1.getMeasurementValue();
//        }
//        return Math.abs(heartRate1 - heartRate2) > 10;
//    }
}

