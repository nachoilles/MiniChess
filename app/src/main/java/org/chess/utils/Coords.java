package org.chess.utils;

public record Coords(int row, int col) {
  @Override
  public String toString() {
    char file = (char) ('a' + col);
    int rank = row + 1;
    return "" + file + rank;
  }
}