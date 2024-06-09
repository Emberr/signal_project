package com.Factory;

import com.alerts.Alert;
import com.alerts.ECGAlert;

public class ECGAlertFactory extends AlertFactory{

    /**
     * Generates an ECGAlert with the given patient ID, condition, and timestamp.
     *
     * @param patientId the unique identifier of the patient
     * @param condition the condition that triggered the alert
     * @param timeStamp the time at which the alert was triggered, in milliseconds
     * @return an ECGAlert object
     */
    @Override
    public Alert createAlert(String patientId, String condition, long timeStamp){
        return new ECGAlert(patientId, condition, timeStamp);
    }
}
