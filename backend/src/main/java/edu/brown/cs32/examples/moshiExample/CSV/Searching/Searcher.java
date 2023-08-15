package edu.brown.cs32.examples.moshiExample.CSV.Searching;

import edu.brown.cs32.examples.moshiExample.CSV.Parsing.Parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//
public class Searcher {

  private Parser<List<String>> parsed;

  /**
   * Constructor for Searcher class
   *
   * @param fileName - the name of the file that is being searched through
   * @param headers - boolean indicating if the provided CSV file has headers
   * @param stringCreate - a CreatorFromRow object that parses CSV rows into String lists
   */
  public Searcher(String fileName, boolean headers, StringListCreator stringCreate) throws FileNotFoundException {
      this.parsed = new Parser<>(new FileReader(fileName), stringCreate, headers);
      this.parsed.parse();
  }

  public Searcher(StringReader reader, boolean headers, StringListCreator s) {
    this.parsed = new Parser<>(reader, s, headers);
    this.parsed.parse();
  }

  public List<List<String>> search(String search) {
    List<List<String>> toReturn = new ArrayList<>();
    for (List<String> row : this.parsed.getParsedCsv()) {
      int i = 0;
      boolean inList = false;
      while (i < row.size() && !inList) {
        String attr = row.get(i).toLowerCase();
        i++;
        if (attr.contains(search.toLowerCase())) {
          toReturn.add(row);
          inList = true;
        }
      }
    }
    return toReturn;
  }

//  public List<List<String>> search(String search, String colName) throws SearchException {
//    int i;
//    List<List<String>> toReturn = new ArrayList<>();
//    if (this.parsed.hasHeaders()) {
//      if ((i = Arrays.asList(this.parsed.getHeaders()).indexOf(colName)) != -1) {
//        toReturn = search(search, i);
//      } else {
//        throw new SearchException("Column name " + colName + " not found");
//      }
//    } else {
//      throw new SearchException("This csv does not have headers");
//    }
//    return toReturn;
//  }

  public List<List<String>> search(String search, String colIndex) throws IndexOutOfBoundsException,
          ColumnNotFoundException, NoHeaderException {
    List<List<String>> toReturn = new ArrayList<>();
    Integer index;
    try {
      index = Integer.parseInt(colIndex);
    } catch (NumberFormatException e) {
      if (this.parsed.hasHeaders()) {
        if ((index = Arrays.asList(this.parsed.getHeaders()).indexOf(colIndex.toLowerCase())) == -1) {
          throw new ColumnNotFoundException("Column name " + colIndex + " not found");
        }
      } else {
        throw new NoHeaderException("This csv does not have headers");
      }
    }
    for (List<String> row : this.parsed.getParsedCsv()) {
      if (row.get(index).toLowerCase().contains(search.toLowerCase())) {
        toReturn.add(row);
      }
    }
    return toReturn;
  }

  public Parser<List<String>> getCSV() {
    return this.parsed;
  }
}
