package data_management;

import com.data_management.Patient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetRecordsTest {

    @Test
    @DisplayName("Test getRecords method")
    public void testGetRecords() {
        Patient mock = new Patient(1);
        mock.addRecord(100, "HeartRate", 1000);
        mock.addRecord(120, "HeartRate", 2000);
        mock.addRecord(80, "HeartRate", 3000);
        mock.addRecord(120, "SystolicPressure", 4000);
        assertEquals(3, mock.getRecords(0, 3000).size());
    }

    @Test
    @DisplayName("Test getRecordType method")
    public void testGetRecordType() {
        Patient mock = new Patient(1);
        mock.addRecord(100, "HeartRate", 1000);
        mock.addRecord(120, "HeartRate", 2000);
        mock.addRecord(80, "HeartRate", 3000);
        mock.addRecord(120, "SystolicPressure", 4000);
        assertEquals("SystolicPressure", mock.getRecords(0, 4000).get(3).getRecordType());
    }

    @Test
    @DisplayName("Test getRecordValue method")
    public void testGetRecordValue() {
        Patient mock = new Patient(1);
        mock.addRecord(100, "HeartRate", 1000);
        mock.addRecord(120, "HeartRate", 2000);
        mock.addRecord(80, "HeartRate", 3000);
        mock.addRecord(120, "SystolicPressure", 4000);
        assertEquals(120, mock.getRecords(0, 4000).get(3).getMeasurementValue());
    }

    @Test
    @DisplayName("Test getRecordTimestamp method")
    public void testGetRecordTimestamp() {
        Patient mock = new Patient(1);
        mock.addRecord(100, "HeartRate", 1000);
        mock.addRecord(120, "HeartRate", 2000);
        mock.addRecord(80, "HeartRate", 3000);
        mock.addRecord(120, "SystolicPressure", 4000);
        assertEquals(4000, mock.getRecords(0, 4000).get(3).getTimestamp());
    }

    @Test
    @DisplayName("Test getRecordPatientId method")
    public void testGetRecordPatientId() {
        Patient mock = new Patient(1);
        mock.addRecord(100, "HeartRate", 1000);
        mock.addRecord(120, "HeartRate", 2000);
        mock.addRecord(80, "HeartRate", 3000);
        mock.addRecord(120, "SystolicPressure", 4000);
        assertEquals(1, mock.getRecords(0, 4000).get(3).getPatientId());
    }
}
