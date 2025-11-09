package org.chess.pieces;

import org.chess.players.Player;
import org.chess.utils.PieceType;

public class Bishop extends Piece {
  public Bishop(Player owner) {
    this.type = PieceType.Bishop;
    this.owner = owner;
  }
}
