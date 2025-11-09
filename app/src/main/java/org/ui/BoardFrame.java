package org.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.function.Consumer;

import javax.swing.JFrame;

import org.chess.utils.BoardSnapshot;
import org.ui.utils.BoardListener;
import org.ui.utils.ControlListener;

public class BoardFrame extends JFrame {
  private static int DEFAULT_BOARD_SIZE = 800;
  private BoardPanel boardPanel;
  private ControlPanel controlPanel;

  public BoardFrame(BoardSnapshot boardSnapshot, BoardListener boardListener, ControlListener controlListener) {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("Chess");
    setLayout(new BorderLayout());
    setResizable(false);


    boardPanel = new BoardPanel(boardSnapshot, boardListener);
    boardPanel.setPreferredSize(new Dimension(DEFAULT_BOARD_SIZE, DEFAULT_BOARD_SIZE));
    add(boardPanel, BorderLayout.CENTER);

    controlPanel = new ControlPanel(DEFAULT_BOARD_SIZE, controlListener);
    add(controlPanel, BorderLayout.EAST);

    pack();
    setLocationRelativeTo(null);

    setVisible(true);
  }

  public void updateBoard(BoardSnapshot boardSnapshot) {
    boardPanel.setBoard(boardSnapshot);
  }

  public void onMove(String move) {
    controlPanel.addMove(move);
  }

  public void onUndo() {
    controlPanel.removeLastMove();
  }

  public void onCoronation(Consumer<String> choiceCallback) {
    controlPanel.requestCoronation(choiceCallback);
  }
}
