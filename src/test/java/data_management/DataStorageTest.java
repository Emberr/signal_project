package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataReader;
import com.data_management.FileDataReader;
import org.junit.jupiter.api.Test;
import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.io.IOException;
import java.util.List;

class DataStorageTest {

    @Test
    void testAddAndGetRecords() throws IOException {
        // TODO Perhaps you can implement a mock data reader to mock the test data?
        //DataReader reader = new FileDataReader("data/test_data.csv");
        DataStorage storage = DataStorage.getInstance();
        //reader.readData(storage);
        storage.addPatientData(1, 100.0, "HeartRate", 1714376789050L);
        storage.addPatientData(1, 120.0, "HeartRate", 1714376789051L);
        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
    }
}
