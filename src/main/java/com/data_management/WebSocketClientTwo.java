package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocketClientTwo extends WebSocketClient {

    private DataStorage storage;

    public WebSocketClientTwo(URI serverUri, DataStorage storage) {
        super(serverUri);
        this.storage = storage;
    }

    @Override
    public void onOpen(ServerHandshake handShakeData) {
        System.out.println("Connected");
    }

    @Override
    public void onMessage(String message) {
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
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
    public void closeConnection() {
        this.close();
    }
}