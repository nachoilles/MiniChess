package org.ai;

import java.util.ArrayList;
import java.util.Random;

import org.chess.Game;
import org.chess.players.Player;
import org.chess.players.PlayerType;
import org.chess.utils.Move;

public class RandomAI extends Player implements Agent {
  private final Random random = new Random();

  public RandomAI() {
    super(PlayerType.RandomAI);
  }

  @Override
  public Move decideMove(Game game) {
    ArrayList<Move> allMoves = game.getAllLegalMoves(id);
    if (allMoves.isEmpty())
      return null;
    return allMoves.get(random.nextInt(allMoves.size()));
  }

  @Override
  public String choosePromotion(Game game, Move move) {
    String[] promotionOptions = { "♛", "♜", "♝", "♞" };
    return promotionOptions[random.nextInt(promotionOptions.length)];
  }
}
