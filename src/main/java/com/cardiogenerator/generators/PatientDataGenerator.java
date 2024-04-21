package com.cardiogenerator.generators;

import com.cardiogenerator.outputs.OutputStrategy;

/**
 * Interface for generating patient data
 * Standard definition for generating patient data
 */
public interface PatientDataGenerator {
    /**
     * Generates patient data for the given patient ID
     * @param patientId The ID of the patient
     * @param outputStrategy The output strategy to use
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
