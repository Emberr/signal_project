package com.Decorator;

import com.alerts.Alert;

public class RepeatedAlertDecorator extends AlertDecorator{
    private int repetitions;

    public RepeatedAlertDecorator(Alert alert, int repetitions) {
        super(alert);
        this.repetitions = repetitions;
    }

    @Override
    public String getCondition() {
        return alert.getCondition() +  repetitions + " times repeated";
    }

    public int getRepetitionCount() {
        return repetitions;
    }
    
}
