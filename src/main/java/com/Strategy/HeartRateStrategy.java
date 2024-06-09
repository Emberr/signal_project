package com.Strategy;

import java.util.Queue;
import java.util.LinkedList;
import java.util.List;

import com.data_management.PatientRecord;

public class HeartRateStrategy implements AlertStrategy {
    private Queue<Double> ecgWindow = new LinkedList<>();
    private double ecgSum = 0;
    private static final int WINDOW_SIZE = 10;
    private static final double THRESHOLD = 1.5;
    private List<PatientRecord> records;
    private int start = 0;

    /**
     * Constructs a new HeartRateStrategy.
     */
    public HeartRateStrategy() {
        records = new LinkedList<>();
    }

    /**
     * Checks if the patient record meets the criteria for triggering an alert.
     *
     * @param record the patient record to check
     * @return true if the alert should be triggered, false otherwise
     */
    @Override
    public boolean checkAlert(PatientRecord record) {
        if (!record.getRecordType().contains("ECG")) {
            return false;
        }
        records.add(record);
        double heartRate = 0;
        for (int a = start; a < start+3 || records.get(a) != null; a++) {
            heartRate = records.get(a).getMeasurementValue();
            ecgSum += heartRate;
            ecgWindow.add(heartRate);
            if (ecgWindow.size() > WINDOW_SIZE) {
                ecgSum -= ecgWindow.poll();
            }
        }
        start++;    
        double average = ecgSum / ecgWindow.size();
        return heartRate > average * THRESHOLD;
    }
}
