package org.chess.pieces;

import org.chess.players.Player;
import org.chess.utils.PieceType;

public class Queen extends Piece {
  public Queen(Player owner) {
    this.type = PieceType.Queen;
    this.owner = owner;
  }
}
