package edu.brown.cs32.examples.moshiExample.CSV.Searching;

public class ColumnNotFoundException extends Exception{
    public ColumnNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
