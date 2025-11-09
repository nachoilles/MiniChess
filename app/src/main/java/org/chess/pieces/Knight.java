package org.chess.pieces;

import org.chess.players.Player;
import org.chess.utils.PieceType;

public class Knight extends Piece {
  public Knight(Player owner) {
    this.type = PieceType.Knight;
    this.owner = owner;
  }
}
