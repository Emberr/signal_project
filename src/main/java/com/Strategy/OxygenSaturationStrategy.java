package com.Strategy;

import com.data_management.PatientRecord;

public class OxygenSaturationStrategy implements AlertStrategy {
    private static final double CRITICAL_THRESHOLD = 92;

    /**
     * Checks if the patient record meets the criteria for triggering an alert.
     *
     * @param record the patient record to check
     * @return true if the alert should be triggered, false otherwise
     */
    @Override
    public boolean checkAlert(PatientRecord record) {
        if (!record.getRecordType().contains("Saturation")) {
            return false;
        }
        double oxygenSaturation = record.getMeasurementValue();
        return oxygenSaturation < CRITICAL_THRESHOLD;
    }
}

