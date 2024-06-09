package com.Decorator;

import com.alerts.Alert;

public class RepeatedAlertDecorator extends AlertDecorator{
    private int repetitions;

    /**
     * Creates a new RepeatedAlertDecorator with the given alert and repetition count.
     *
     * @param alert the alert to be decorated
     * @param repetitions the number of times the alert has been repeated
     */
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
