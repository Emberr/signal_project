package com.cardiogenerator.generators; //removed underscore from package name

import java.util.Random; //removed empty space between import lines
import com.cardiogenerator.outputs.OutputStrategy;

public class AlertGenerator implements PatientDataGenerator {

    public static final Random RANDOM_GENERATOR = new Random(); //changed name to UPPER_SNAKE_CASE
    private boolean[] alertStates; // false = resolved, true = pressed ; changed name to lowerCamelCase

    /**
     * Constructor for AlertGenerator class that creates a new instance of the AlertGenerator object
     * @param patientCount - number of patients
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Void method that outputs alert info for a patient; if the alert was triggered already, the alert will resolve 90% of the time
     * @param patientId - ID of the patient
     * @param outputStrategy - output strategy that is used to output the alert data
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency ; changed name to lowerCamelCase
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
