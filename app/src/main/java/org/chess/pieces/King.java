package org.chess.pieces;

import org.chess.players.Player;
import org.chess.utils.PieceType;

public class King extends Piece {
  public King(Player owner) {
    this.type = PieceType.King;
    this.owner = owner;
  }
}
