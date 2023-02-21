package com.wittybrains.busbookingsystem.exception;


public class StationNotFoundException extends RuntimeException {
    public StationNotFoundException(String message) {
        super(message);
    }
}
