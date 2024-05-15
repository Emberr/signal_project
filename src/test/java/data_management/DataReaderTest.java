package data_management;

import com.data_management.DataReader;
import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataReaderTest {

    @Test
    void testReadData() throws IOException {

        DataReader reader = new FileDataReader("test_data");
        DataStorage storage = new DataStorage();

        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(2, 0, 1200);
        for (PatientRecord record : records) {
            System.out.println("Record for Patient ID: " + record.getPatientId() +
                    ", Type: " + record.getRecordType() +
                    ", Data: " + record.getMeasurementValue() +
                    ", Timestamp: " + record.getTimestamp());
        }
        assertEquals(12, records.size());
    }
}
