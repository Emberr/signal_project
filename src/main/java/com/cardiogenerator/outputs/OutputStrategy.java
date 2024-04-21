package com.cardiogenerator.outputs;

/**
 * Interface for data output strategy
 * Standard definition for outputting data
 */
public interface OutputStrategy {

    /**
     * Outputs the given data to the output stream.
     *
     * @param patientId The ID of the patient
     * @param timestamp The timestamp of the data
     * @param label The label of the data
     * @param data The data itself
     */
    void output(int patientId, long timestamp, String label, String data);
}
