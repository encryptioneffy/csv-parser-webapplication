package edu.brown.cs32.examples.moshiExample.CSV.Searching;

import java.util.List;

public interface CreatorFromRow<T> {
  T create(List<String> row) throws FactoryFailureException;
}
