package org.main;

import javax.swing.SwingUtilities;

import org.chess.Game;
import org.chess.players.PlayerType;
import org.ui.BoardFrame;

public class Main {
  public static void main(String[] args) {
    Game game = new Game(PlayerType.RandomAI, PlayerType.RandomAI);
    SwingUtilities.invokeLater(() -> {
      BoardFrame window = new BoardFrame(game.getBoardSnapshot(), game, game);

      game.setOnBoardUpdated(() -> {
        window.updateBoard(game.getBoardSnapshot());
      });

      game.setOnMove(() -> {
        window.onMove(game.getLastMove());
      });

      game.setOnUndo(() -> {
        window.onUndo();
      });

      game.setOnCoronation(choiceCallback -> {
        SwingUtilities.invokeLater(() -> {
          window.onCoronation(choiceCallback);
        });
      });
      window.updateBoard(game.getBoardSnapshot());
      window.repaint();
    });
  }
}
