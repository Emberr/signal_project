package data_management;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.alerts.AlertManager;
import com.cardiogenerator.outputs.OutputStrategy;
import com.cardiogenerator.outputs.WebSocketOutputStrategy;
import com.data_management.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class WebSocketDataReaderTest {

    OutputStrategy outputStrategy;
    AlertGenerator alertGenerator;
    AlertManager alertManager;

    @Test
    @DisplayName("Test WebSocketDataReader")
    public void testWebSocketDataReader() throws IOException, URISyntaxException, InterruptedException {

        DataStorage storage = DataStorage.getInstance();
        outputStrategy = new WebSocketOutputStrategy(8080);
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:8080");
        reader.readData(storage);
        Thread.sleep(1000);
        outputStrategy.output(1, 0, "SystolicPressure", "180");
        Thread.sleep(1000);
        assertEquals(1,storage.getRecords(1,0,10).size());
    }

    @Test
    @DisplayName("Test Data Evaluation from Port")
    public void testDataEvaluation() throws IOException, URISyntaxException, InterruptedException {
        DataStorage storage = DataStorage.getInstance();
        outputStrategy = new WebSocketOutputStrategy(8888);
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:8888");
        reader.readData(storage);
        Thread.sleep(1000);
        outputStrategy.output(1, 0, "SystolicPressure", "181");
        Thread.sleep(1000);
        alertManager = new AlertManager();
        alertGenerator = new AlertGenerator(storage,alertManager);
        Patient mock = storage.getPatient(1);
        alertGenerator.evaluateData(mock,0,10);
        List<Alert> patientAlerts = alertManager.getAlertsForPatient("1");
        assertTrue(patientAlerts.get(0).getCondition().equals("Critical Blood Pressure"));
    }

    @Test
    @DisplayName("Test Invalid URL")
    public void testInvalidSyntax() {
        outputStrategy = new WebSocketOutputStrategy(88);
        assertThrows(URISyntaxException.class, () -> {
            WebSocketDataReader reader = new WebSocketDataReader("ws://invalid url");
            Thread.sleep(1000);
            reader.readData(DataStorage.getInstance());
        });
    }

    @Test
    @DisplayName("Test Invalid Port")
    public void testInvalidPort() {
        assertThrows(IllegalArgumentException.class, () -> {
            outputStrategy = new WebSocketOutputStrategy(888888);
        });
    }

}
