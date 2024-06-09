package com.alerts;

// Represents an alert
public class Alert {
    private String patientId;
    private String condition;
    private long timeStamp;

    /**
     * Constructs a new Alert with the specified patient ID, condition, and timestamp.
     *
     * @param patientId the unique identifier of the patient
     * @param condition the condition that triggered the alert
     * @param timeStamp the time at which the alert was triggered, in milliseconds
     */
    public Alert(String patientId, String condition, long timeStamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timeStamp = timeStamp;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public long getTimestamp() {
        return timeStamp;
    }

}
