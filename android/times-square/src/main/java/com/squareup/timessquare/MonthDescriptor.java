// Copyright 2012 Square, Inc.
package com.squareup.timessquare;

import java.util.Date;

class MonthDescriptor {
  private final int month;
  private final int year;
  private final Date date;
  private String label;
    private String closedDatesText;

    public MonthDescriptor(int month, int year, Date date, String label, String closedDatesText) {
    this.month = month;
    this.year = year;
    this.date = date;
    this.label = label;
        this.closedDatesText = closedDatesText;
    }

  public int getMonth() {
    return month;
  }

  public int getYear() {
    return year;
  }

  public Date getDate() {
    return date;
  }

  public String getLabel() {
    return label;
  }

  public String getClosedDatesText() {
      return closedDatesText;
  }

    void setLabel(String label) {
    this.label = label;
  }

  @Override public String toString() {
    return "MonthDescriptor{"
        + "label='"
        + label
        + '\''
        + ", month="
        + month
        + ", year="
        + year
        + '}';
  }
}
