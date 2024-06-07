package com.Factory;

import com.alerts.Alert;
import com.alerts.BloodPressureAlert;
import com.alerts.ECGAlert;

public class BloodPressureAlertFactory extends AlertFactory{

    @Override
    public Alert createAlert(String patientId, String condition, long timeStamp){
        return new BloodPressureAlert(patientId, condition, timeStamp);
    }
}
