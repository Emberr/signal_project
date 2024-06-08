package com.Strategy;

import com.data_management.PatientRecord;

public class BloodPressureStrategy implements AlertStrategy {
    private static final double SYSTOLIC_THRESHOLD_HIGH = 180;
    private static final double SYSTOLIC_THRESHOLD_LOW = 90;
    private static final double DIASTOLIC_THRESHOLD_HIGH = 120;
    private static final double DIASTOLIC_THRESHOLD_LOW = 60;

    @Override
    public boolean checkAlert(PatientRecord record) {
        if (!record.getRecordType().contains("Pressure")) {
            return false;
        }
        double pressure = record.getMeasurementValue();
        return (record.getRecordType().contains("Systolic") && (pressure > SYSTOLIC_THRESHOLD_HIGH || pressure < SYSTOLIC_THRESHOLD_LOW)) ||
               (record.getRecordType().contains("Diastolic") && (pressure > DIASTOLIC_THRESHOLD_HIGH || pressure < DIASTOLIC_THRESHOLD_LOW));
    }
}

