package com.data_management;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The WebSocketDataReader class is an implementation of the DataReader interface.
 * It leverages a WebSocketClientTwo instance to fetch data from a WebSocket server and populate a DataStorage instance.
 */
public class WebSocketDataReader implements DataReader {
    private WebSocketClientTwo client;

    /**
     * Constructs a WebSocketDataReader with a given server URI.
     * @param serverUri the URI of the WebSocket server to establish a connection with
     * @throws URISyntaxException if the provided serverUri is not a valid URI
     */
    public WebSocketDataReader(String serverUri) throws URISyntaxException {
        this.client = new WebSocketClientTwo(new URI(serverUri));
    }

    /**
     * Fetches data from the WebSocket server and populates the provided DataStorage instance.
     * @param dataStorage the DataStorage instance to populate with the fetched data
     * @throws IOException if an I/O error occurs during the connection to the WebSocket server
     * @throws URISyntaxException if the serverUri provided during the construction of this WebSocketDataReader is not a valid URI
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException, URISyntaxException {
        client.setDataSource(dataStorage);
        client.connect();
    }
}