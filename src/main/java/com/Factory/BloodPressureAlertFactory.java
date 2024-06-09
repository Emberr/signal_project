package com.Factory;

import com.alerts.Alert;
import com.alerts.BloodPressureAlert;
import com.alerts.ECGAlert;

public class BloodPressureAlertFactory extends AlertFactory{

    /**
     * Generates a BloodPressureAlert with the given patient ID, condition, and timestamp.
     *
     * @param patientId the unique identifier of the patient
     * @param condition the condition that triggered the alert
     * @param timeStamp the time at which the alert was triggered, in milliseconds
     * @return a BloodPressureAlert object
     */
    @Override
    public Alert createAlert(String patientId, String condition, long timeStamp){
        return new BloodPressureAlert(patientId, condition, timeStamp);
    }
}
