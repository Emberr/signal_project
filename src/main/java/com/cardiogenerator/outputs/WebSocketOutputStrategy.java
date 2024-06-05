package com.cardiogenerator.outputs;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
/**
 * WebSocketOutputStrategy is a class that realizes the OutputStrategy interface.
 * It employs a WebSocketServer to disseminate output data to all connected clients.
 */
public class WebSocketOutputStrategy implements OutputStrategy {

    private WebSocketServer server;

    /**
     * Constructs a WebSocketOutputStrategy with a given server port.
     * @param port the port number for the WebSocket server
     */
    public WebSocketOutputStrategy(int port) {
        server = new SimpleWebSocketServer(new InetSocketAddress(port));
        System.out.println("WebSocket server created on port: " + port + ", listening for connections...");
        server.start();
    }

    /**
     * Sends data to all connected clients.
     * The data is formatted as a comma-separated string and dispatched to each client.
     * @param patientId the unique identifier of the patient
     * @param timestamp the time at which the measurement was taken, in milliseconds since the Unix epoch
     * @param label the type of record, e.g., "HeartRate", "BloodPressure"
     * @param data the value of the health metric being recorded
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
        // Broadcast the message to all connected clients
        for (WebSocket conn : server.getConnections()) {
            conn.send(message);
            System.out.println("Sent message to client: " + conn.getRemoteSocketAddress() + " - " + message);
        }
    }

    /**
     * SimpleWebSocketServer is a subclass of WebSocketServer that manages WebSocket connections.
     * It processes incoming messages and stores them in a DataStorage object.
     */
    private static class SimpleWebSocketServer extends WebSocketServer {

        /**
         * Constructs a SimpleWebSocketServer with a given server address.
         * @param address the address of the WebSocket server
         */
        public SimpleWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        /**
         * This method is called when a new WebSocket connection is established.
         * @param conn the WebSocket connection
         * @param handshake the handshake data from the client
         */
        @Override
        public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
            System.out.println("New connection: " + conn.getRemoteSocketAddress());
        }

        /**
         * This method is called when a WebSocket connection is terminated.
         * @param conn the WebSocket connection
         * @param code the status code
         * @param reason the reason for the closure
         * @param remote whether the closure was initiated by the remote peer
         */
        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
        }

        /**
         * This method is called when a message is received from a WebSocket client.
         * This method is not utilized in this context.
         * @param conn the WebSocket connection
         * @param message the message from the client
         */
        @Override
        public void onMessage(WebSocket conn, String message) {
            // Not used in this context
        }

        /**
         * This method is called when an error occurs on a WebSocket connection.
         * @param conn the WebSocket connection
         * @param ex the exception representing the error
         */
        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace();
        }

        /**
         * This method is called when the WebSocket server is initiated.
         */
        @Override
        public void onStart() {
            System.out.println("Server started successfully");
        }
    }
}
