package org.chess.pieces;

import org.chess.players.Player;
import org.chess.utils.PieceType;

public class Piece {
  PieceType type;
  Player owner;
  boolean moved;

  public Piece() {
    this(false);
  }

  public Piece(boolean moved) {
    this.moved = moved;
  }

  public PieceType getType() {
    return this.type;
  }

  public void setType(PieceType type) {
    this.type = type;
  }

  public Player getOwner() {
    return this.owner;
  }

  public void setOwner(Player owner) {
    this.owner = owner;
  }

  public void setMoved(boolean moved) {
    this.moved = moved;
  }

  public boolean getMoved() {
    return this.moved;
  }
}
