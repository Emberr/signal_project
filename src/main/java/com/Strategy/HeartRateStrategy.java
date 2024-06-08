package com.Strategy;

import java.util.Queue;
import java.util.LinkedList;

import com.data_management.PatientRecord;

public class HeartRateStrategy implements AlertStrategy {
    private Queue<Double> ecgWindow = new LinkedList<>();
    private double ecgSum = 0;
    private static final int WINDOW_SIZE = 10;
    private static final double THRESHOLD = 1.5;

    public boolean checkAlert(PatientRecord record) {
        if (!record.getRecordType().contains("ECG")) {
            return false;
        }
        double heartRate = record.getMeasurementValue();
        ecgSum += heartRate;
        ecgWindow.add(heartRate);
        if (ecgWindow.size() > WINDOW_SIZE) {
            ecgSum -= ecgWindow.poll();
        }
        double average = ecgSum / ecgWindow.size();
        return heartRate > average * THRESHOLD;
    }
}
