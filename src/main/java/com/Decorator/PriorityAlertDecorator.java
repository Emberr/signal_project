package com.Decorator;

import com.alerts.Alert;

public class PriorityAlertDecorator extends AlertDecorator{
    private int priorityLevel;

    /**
     * Creates a new PriorityAlertDecorator with the given alert and priority level.
     *
     * @param alert the alert to be decorated
     * @param priorityLevel the priority level of the alert
     */
    public PriorityAlertDecorator(Alert alert, int priorityLevel){
        super(alert);
        this.priorityLevel = priorityLevel;
    }

    @Override
    public String getCondition() {
        return alert.getCondition() + ", priority level : " + priorityLevel ;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }
}
