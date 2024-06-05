package com.data_management;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;

public interface DataReader {
    /**
     * Reads data from a specified source and stores it in the data storage.
     * 
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException, URISyntaxException, ConnectException;
}
