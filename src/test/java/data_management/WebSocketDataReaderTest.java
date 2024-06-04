package data_management;

import com.cardiogenerator.HealthDataSimulator;
import com.data_management.DataStorage;
import com.data_management.WebSocketDataReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import static com.cardiogenerator.HealthDataSimulator.initializePatientIds;
import static com.cardiogenerator.HealthDataSimulator.scheduleTasksForPatients;
import static org.junit.jupiter.api.Assertions.*;


public class WebSocketDataReaderTest {

    @Test
    public void testWebSocketDataReader() throws IOException, URISyntaxException, InterruptedException {
        int patientCount = 10;
        HealthDataSimulator.scheduler = Executors.newScheduledThreadPool(patientCount * 4);

        List<Integer> patientIds = initializePatientIds(patientCount);
        Collections.shuffle(patientIds);
        DataStorage storage = new DataStorage();
        scheduleTasksForPatients(patientIds);
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:8080",storage);
        reader.readData(storage);
        Thread.sleep(1500);
        assertTrue(!storage.getAllPatients().isEmpty());
    }
}
