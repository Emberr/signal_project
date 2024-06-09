package com.Decorator;

import com.alerts.Alert;

public abstract class AlertDecorator extends Alert{
    
    protected Alert alert;

/**
     * Creates a new AlertDecorator with the given patient ID, condition, and timestamp.
     *
     * @param alert the alert to be decorated
     */
    public AlertDecorator(Alert alert) {
        super(alert.getPatientId(), alert.getCondition(), alert.getTimestamp());
    }

}
