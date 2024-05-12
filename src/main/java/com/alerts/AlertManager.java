package com.alerts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertManager {
    private Map<String, List<Alert>> alertMap;

    public AlertManager() {
        this.alertMap = new HashMap<>();
    }

    public void logAlert(Alert alert) {
        String patientId = alert.getPatientId();
        if (!alertMap.containsKey(patientId)) {
            alertMap.put(patientId, new ArrayList<>());
        }
        alertMap.get(patientId).add(alert);
    }

    public List<Alert> getAlertsForPatient(String patientId) {
        if (!alertMap.containsKey(patientId)) {
            return null;
        }
        return alertMap.get(patientId);
    }
}