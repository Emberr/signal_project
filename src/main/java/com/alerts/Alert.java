package com.alerts;

// Represents an alert
public class Alert {
    private String patientId;
    private String condition;
    private long timestamp;

    /**
     * Constructs a new Alert with the specified patient ID, condition, and timestamp.
     *
     * @param patientId the unique identifier of the patient
     * @param condition the condition that triggered the alert
     * @param timestamp the time at which the alert was triggered, in milliseconds since UNIX epoch
     */
    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
