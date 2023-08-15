package edu.brown.cs32.examples.moshiExample.CSV.Searching;

import java.util.List;

public class StringListCreator implements CreatorFromRow<List<String>> {

  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}
