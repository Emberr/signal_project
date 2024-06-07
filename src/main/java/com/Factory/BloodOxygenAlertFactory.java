package com.Factory;

import com.alerts.Alert;
import com.alerts.BloodOxygenAlert;

public class BloodOxygenAlertFactory extends AlertFactory{

    @Override
    public Alert createAlert(String patientId, String condition, long timeStamp){
        return new BloodOxygenAlert(patientId, condition, timeStamp);
    }
}
