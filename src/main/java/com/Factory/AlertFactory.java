package com.Factory;

import com.alerts.Alert;

public abstract class AlertFactory {

    /**
     * Generates an Alert with the given patient ID, condition, and timestamp.
     * This method is designed to be overridden by subclasses to generate specific types of Alerts.
     *
     * @param patientId the unique identifier of the patient
     * @param condition the condition that triggered the alert
     * @param timeStamp the time at which the alert was triggered, in milliseconds
     * @return an Alert object
     */
    public Alert createAlert(String patientId, String condition, long timeStamp){
        return new Alert(patientId, condition, timeStamp);
    }
}
