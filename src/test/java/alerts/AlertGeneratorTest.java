package alerts;

import com.alerts.AlertGenerator;
import com.alerts.AlertManager;
import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.data_management.Patient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlertGeneratorTest {

    public Patient mock = new Patient(1);
    public DataStorage storage = new DataStorage();
    public AlertManager alertManager = new AlertManager();
    public AlertGenerator alertGenerator = new AlertGenerator(storage, alertManager);

    @Test
    @DisplayName("Test checkTrendAlert method")
    public void testCheckTrendAlert() {
        mock.addRecord(100, "SystolicPressure", 1000);
        mock.addRecord(120, "SystolicPressure", 2000);
        mock.addRecord(140, "SystolicPressure", 3000);
        List<PatientRecord> records = mock.getRecords(1000, 3000);
        assertTrue(alertGenerator.checkTrendAlert(records));
    }

    @Test
    @DisplayName("Test checkCriticalThresholdAlert method")
    public void testCheckCriticalThresholdAlert() {
        mock.addRecord(181, "SystolicPressure", 1000);
        List<PatientRecord> records = mock.getRecords(1000, 1000);
        assertTrue(alertGenerator.checkCriticalThresholdAlert(records.get(0)));
    }

    @Test
    @DisplayName("Test HypotensiveHypoxemiaAlert method")
    public void testHypotensiveHypoxemiaAlert() {
        mock.addRecord(89, "SystolicPressure", 1000);
        mock.addRecord(91, "Saturation", 2000);
        List<PatientRecord> records = mock.getRecords(1000, 2000);
        assertTrue(alertGenerator.checkHypotensiveHypoxemiaAlert(records.get(0), records.get(1)));
    }


    @Test
    @DisplayName("Test checkLowSaturationAlert method")
    public void testCheckLowSaturationAlert() {
        mock.addRecord(89, "Saturation", 1000);
        List<PatientRecord> records = mock.getRecords(1000, 1000);
        assertTrue(alertGenerator.checkLowSaturationAlert(records.get(0)));
    }

    @Test
    @DisplayName("Test RapidDropAlert method")
    public void testRapidDropAlert() {
        mock.addRecord(110, "Saturation", 1000);
        mock.addRecord(100, "Saturation", 2000);
        List<PatientRecord> records = mock.getRecords(1000, 3000);
        assertTrue(alertGenerator.checkRapidDropAlert(records));
    }

    @Test
    @DisplayName("Test checkAbnormalHeartRateAlert method")
    public void testCheckAbnormalHeartRateAlert() {
        assertTrue(alertGenerator.checkAbnormalHeartRateAlert(49));
    }

    @Test
    @DisplayName("Test checkIrregularBeatAlert method")
    public void testCheckIrregularBeatAlert() {
        mock.addRecord(49, "ECG", 1000);
        List<PatientRecord> records = mock.getRecords(1000, 1000);
        assertTrue(alertGenerator.checkIrregularBeatAlert(records.get(0)));
    }
}
