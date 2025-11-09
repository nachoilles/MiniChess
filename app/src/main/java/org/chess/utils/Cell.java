package org.chess.utils;

public class Cell {
  public final Coords coords;

  public Cell(int row, int col) {
    this(new Coords(row, col));
  }

  public Cell(Coords coords) {
    this.coords = coords;
  }
}
