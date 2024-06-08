package com.Strategy;

import com.data_management.PatientRecord;

public interface AlertStrategy {
    public boolean checkAlert(PatientRecord patientRecord);
}
