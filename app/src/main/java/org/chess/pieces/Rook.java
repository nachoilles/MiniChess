package org.chess.pieces;

import org.chess.players.Player;
import org.chess.utils.PieceType;

public class Rook extends Piece {
  public Rook(Player owner) {
    this.type = PieceType.Rook;
    this.owner = owner;
  }
}
