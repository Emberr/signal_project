package com.Factory;

import com.alerts.Alert;

public abstract class AlertFactory {

    public Alert createAlert(String patientId, String condition, long timeStamp){
        return new Alert(patientId, condition, timeStamp);
    }
}
