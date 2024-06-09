package com.Decorator;

import com.alerts.Alert;

public abstract class AlertDecorator extends Alert{ //TODO: why is alert not an interfact
    
    protected Alert alert;
    
    public AlertDecorator(Alert alert) {
        super(alert.getPatientId(), alert.getCondition(), alert.getTimestamp());
    }

}
