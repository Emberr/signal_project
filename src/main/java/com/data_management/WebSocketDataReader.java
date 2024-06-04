package com.data_management;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketDataReader implements DataReader {
    private WebSocketClientTwo client;
    private DataStorage storage;

    public WebSocketDataReader(String serverUri, DataStorage storage) throws IOException, URISyntaxException {
        this.client = new WebSocketClientTwo(new URI(serverUri), storage);
        this.storage = storage;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        client.connect();
        client.closeConnection();


    }
}