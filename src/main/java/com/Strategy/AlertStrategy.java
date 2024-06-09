package com.Strategy;

import com.data_management.PatientRecord;

public interface AlertStrategy {

    /**
     * Checks if the patient record meets the criteria for triggering an alert.
     *
     * @param patientRecord the patient record to check
     * @return true if the alert should be triggered, false otherwise
     */
    public boolean checkAlert(PatientRecord patientRecord);
}
