package org.ui.utils;

import org.chess.utils.Coords;

public interface BoardListener {
  void onCellClicked(Coords coords);
}
