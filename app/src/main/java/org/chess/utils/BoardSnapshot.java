package org.chess.utils;

import java.util.ArrayList;

public class BoardSnapshot {
  private ArrayList<PieceSnapshot> pieces;
  private Coords selectedCell;
  private ArrayList<Move> legalMoves;

  public BoardSnapshot(ArrayList<PieceSnapshot> pieces, Coords selectedCell, ArrayList<Move> legalMoves) {
    this.pieces = pieces;
    this.selectedCell = selectedCell;
    this.legalMoves = legalMoves;
  }

  public ArrayList<PieceSnapshot> getPieces() {
    return pieces;
  }

  public Coords getSelectedCell() {
    return selectedCell;
  }

  public ArrayList<Move> getLegalMoves() {
    return legalMoves;
  }
}
