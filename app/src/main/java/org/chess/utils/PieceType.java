package org.chess.utils;

public enum PieceType {
  Pawn, Rook, Knight, Bishop, Queen, King;

  public char getNotation() {
    switch (this) {
      case Pawn: return 'P';
      case Rook: return 'R';
      case Knight: return 'N';
      case Bishop: return 'B';
      case Queen: return 'Q';
      case King: return 'K';
      default: return ' ';
    }
  }
}