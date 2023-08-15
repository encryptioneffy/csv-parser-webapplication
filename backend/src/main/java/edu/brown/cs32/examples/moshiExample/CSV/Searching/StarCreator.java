package edu.brown.cs32.examples.moshiExample.CSV.Searching;

import java.util.List;

public class StarCreator implements CreatorFromRow<Star> {
  @Override
  public Star create(List<String> row) {
    return new Star(row.get(0), row.get(1), row.get(2), row.get(3), row.get(4));
  }
}
