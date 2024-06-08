package com.Strategy;

import com.data_management.PatientRecord;

public class OxygenSaturationStrategy implements AlertStrategy {
    private static final double CRITICAL_THRESHOLD = 92;

    @Override
    public boolean checkAlert(PatientRecord record) {
        if (!record.getRecordType().contains("Saturation")) {
            return false;
        }
        double oxygenSaturation = record.getMeasurementValue();
        return oxygenSaturation < CRITICAL_THRESHOLD;
    }
}

