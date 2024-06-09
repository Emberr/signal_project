package com.Decorator;

import com.alerts.Alert;

public class PriorityAlertDecorator extends AlertDecorator{
    private int priorityLevel;

    public PriorityAlertDecorator(Alert alert, int priorityLevel){
        super(alert);
        this.priorityLevel = priorityLevel;
    }

    @Override
    public String getCondition() {
        return alert.getCondition() + "priority level : " + priorityLevel ;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }
}
