package org.ai;

import org.chess.Game;
import org.chess.utils.Move;

public interface Agent {
  public Move decideMove(Game game);
  public String choosePromotion(Game game, Move move);
}
