package org.chess.pieces;

import org.chess.players.Player;
import org.chess.utils.PieceType;

public class Pawn extends Piece {

  public Pawn(Player owner) {
    this.type = PieceType.Pawn;
    this.owner = owner;
  }
}
