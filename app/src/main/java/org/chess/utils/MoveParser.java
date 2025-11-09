package org.chess.utils;

public class MoveParser {
  public static String parseMove(Move move, boolean isCapture, boolean isCheck, boolean isCheckmate,
      boolean isStalemate, PieceType coronationType) {
    StringBuilder sb = new StringBuilder();

    if (move == null)
      return "";

    if (move.piece() != null && move.piece().getType() != null) {
      char pieceChar = move.piece().getType().getNotation();
      if (pieceChar != 'P')
        sb.append(pieceChar);
    }

    if (isCapture) {
      if (move.piece().getType() == PieceType.Pawn) {
        sb.append((char) ('a' + move.start().col()));
      }
      sb.append('x');
    }

    sb.append(move.end().toString());

    if (move.sEvent() == SpecialEvent.Coronation && coronationType != null) {
      sb.append('=').append(coronationType.getNotation());
    }

    if (isCheckmate)
      sb.append('#');
    else if (isCheck)
      sb.append('+');
    else if (isStalemate)
      sb.append(" ½–½");

    return sb.toString();
  }
}