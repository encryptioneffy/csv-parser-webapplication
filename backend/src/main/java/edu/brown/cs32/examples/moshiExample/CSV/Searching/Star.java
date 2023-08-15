package edu.brown.cs32.examples.moshiExample.CSV.Searching;

public class Star extends Object {
  private String id;
  private String name;
  private String x;
  private String y;
  private String z;

  public Star(String id, String name, String x, String y, String z) {
    this.id = id;
    this.name = name;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public boolean equals(Star s) {
    return s.id.equals(this.id)
        && s.name.equals(this.name)
        && s.x.equals(this.x)
        && s.y.equals(this.y)
        && s.z.equals(this.z);
  }

  public String getId () {
    return this.id;
  }
  public String getX () {
    return this.x;
  }
  public String getY() {
    return this.y;
  }
  public String getZ () {
    return this.z;
  }
  public String getName () {
    return this.name;
  }
}
