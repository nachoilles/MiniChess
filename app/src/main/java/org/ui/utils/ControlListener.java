package org.ui.utils;

import org.chess.utils.Move;

public interface ControlListener {
  public void onMove(Move move, boolean isCheck, boolean isCheckmate, boolean isStalemate);
  public void onUndo();
  public void onReset();
}
