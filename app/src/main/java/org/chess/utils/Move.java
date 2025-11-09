package org.chess.utils;

import org.chess.pieces.Piece;

public record Move(Piece piece, Coords start, Coords end, SpecialEvent sEvent) {
  public Move withSpecialEvent(SpecialEvent newEvent) {
    return new Move(piece, start, end, newEvent);
  }
}
