package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

/**
 * WebSocketClientTwo is a specialized WebSocketClient that manages WebSocket connections.
 * It processes incoming messages and stores them in a DataStorage instance.
 */
public class WebSocketClientTwo extends WebSocketClient {

    private DataStorage storage;

    /**
     * Constructs a WebSocketClientTwo with a specified server URI.
     * @param serverUri the URI of the WebSocket server to connect to
     */
    public WebSocketClientTwo(URI serverUri) {
        super(serverUri);
    }

    /**
     * Called when the WebSocket connection is established.
     * @param handShakeData the handshake data from the server
     */
    @Override
    public void onOpen(ServerHandshake handShakeData) {
        System.out.println("Connected");
    }

    /**
     * Called when a message is received from the WebSocket server.
     * The message is expected to be a comma-separated string containing the patient ID, timestamp, label, and value.
     * The message is processed and stored in the DataStorage instance.
     * @param message the message from the server
     */
    @Override
    public void onMessage(String message) {
        try {
            String[] data = message.split(",");
            int patientId = Integer.parseInt(data[0]);
            long timeStamp = Long.parseLong(data[1]);
            String label = data[2];
            if (data[3].endsWith("%")) {
                data[3] = data[3].substring(0, data[3].length() - 1);
            }
            double value = Double.parseDouble(data[3]);
            storage.addPatientData(patientId, value, label, timeStamp);
            System.out.println("Added data to storage: " + patientId + ", " + timeStamp + ", " + label + ", " + value);
        } catch (Exception e) {
            System.err.println("Error occurred during processing of message: " + message);
            e.printStackTrace();
        }
    }

    /**
     * Called when the WebSocket connection is closed.
     * @param code the status code
     * @param reason the reason for the closure
     * @param remote whether the closure was initiated by the remote peer
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    /**
     * Called when an error occurs on the WebSocket connection.
     * @param ex the exception representing the error
     */
    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    /**
     * Sets the DataStorage instance to be used for storing data.
     * @param storage the DataStorage instance
     */
    public void setDataSource(DataStorage storage) {
        this.storage = storage;
    }

}