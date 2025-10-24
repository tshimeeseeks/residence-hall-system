package com.rhs.backend.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "visitors")
public class Visitor {
    private String name;
    private String idNumber;
    private byte[] idScan; // Assuming the ID scan is stored as a byte array

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public byte[] getIdScan() {
        return idScan;
    }

    public void setIdScan(byte[] idScan) {
        this.idScan = idScan;
    }
}
