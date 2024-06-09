package com.alerts;

public class BloodOxygenAlert extends Alert{
    /**
     * Constructs a new Alert with the specified patient ID, condition, and timestamp.
     *
     * @param patientId the unique identifier of the patient
     * @param condition the condition that triggered the alert
     * @param timestamp the time at which the alert was triggered, in milliseconds
     */
    public BloodOxygenAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
