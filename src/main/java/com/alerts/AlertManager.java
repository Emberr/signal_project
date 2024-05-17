package com.alerts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertManager {
    private Map<String, List<Alert>> alertMap;

    /**
     * Constructs a new AlertManager with an empty alert map.
     */
    public AlertManager() {
        this.alertMap = new HashMap<>();
    }

    /**
     * Logs an alert for a specific patient.
     * If the patient does not have any alerts logged, a new list is created to store
     * alerts for that patient.
     *
     * @param alert the alert to log
     */
    public void logAlert(Alert alert) {
        String patientId = alert.getPatientId();
        if (!alertMap.containsKey(patientId)) {
            alertMap.put(patientId, new ArrayList<>());
        }
        alertMap.get(patientId).add(alert);
    }

    /**
     * Retrieves a list of alerts for a specific patient.
     * If the patient does not have any alerts logged, null is returned.
     *
     * @param patientId the unique identifier of the patient
     * @return a list of alerts for the specified patient, or null if no alerts are
     *         logged
     */
    public List<Alert> getAlertsForPatient(String patientId) {
        if (!alertMap.containsKey(patientId)) {
            return null;
        }
        return alertMap.get(patientId);
    }
}