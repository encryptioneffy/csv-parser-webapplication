package edu.brown.cs32.examples.moshiExample.CSV.Parsing;

import edu.brown.cs32.examples.moshiExample.CSV.Searching.CreatorFromRow;
import edu.brown.cs32.examples.moshiExample.CSV.Searching.FactoryFailureException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser<T> {
  private BufferedReader br;
  private CreatorFromRow<T> creatorObject;
  private boolean hasHeaders;
  private String[] headers;
  private List<T> csv;

  /**
   * Constructor for Parser class
   *
   * @param csvReader - the reader that is parsing the CSV data
   * @param creatorObject - the object of type CreatorFromRow that defines the instructions for
   *     parsing each row
   * @param headers - boolean indicating if the provided CSV file has headers
   */
  public Parser(Reader csvReader, CreatorFromRow<T> creatorObject, boolean headers) {
    this.br = new BufferedReader(csvReader);
    this.creatorObject = creatorObject;
    this.hasHeaders = headers;
    String[] empty = {};
    this.headers = empty;
    this.csv = new ArrayList<>();
  }

  /**
   * Takes csv data row by row from the inputted reader and parses each row according to the
   * instructions from the CreatorFromRow object
   *
   * @return - a List containing all these parsed rows.
   */
  public List<T> parse() {
    String ln;
    try {
      if (this.hasHeaders && ((ln = this.br.readLine()) != null)) {
        this.headers = ln.split(",");
        for(int i = 0; i < this.headers.length; i++) {
          this.headers[i] = this.headers[i].toLowerCase();
        }
      }
      while ((ln = this.br.readLine()) != null) {
        List<String> row = Arrays.asList(ln.split(","));
        try {
          this.csv.add(this.creatorObject.create(row));
        } catch (FactoryFailureException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return this.csv;
  }

  /**
   * Accessor method to get the parsed csv data.
   *
   * @return - the List that contains all the parsed csv data
   */
  public List<T> getParsedCsv() {
    return this.csv;
  }

  /**
   * Accessor method to get the headers of this csv file.
   *
   * @return - the String array that contains the headers of this csv file
   */
  public String[] getHeaders() {
    return this.headers;
  }

  /**
   * Accessor method to get the boolean that indicates if the csv file has headers.
   *
   * @return - the boolean that indicates if the csv file has headers.
   */
  public boolean hasHeaders() {
    return this.hasHeaders;
  }
}
