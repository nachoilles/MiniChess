package org.ui;

import javax.swing.JPanel;

import org.chess.utils.BoardSnapshot;
import org.chess.utils.Coords;
import org.chess.utils.Move;
import org.chess.utils.PieceSnapshot;
import org.ui.utils.BoardListener;
import org.ui.utils.ImageLoader;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class BoardPanel extends JPanel {
  private BoardSnapshot board;
  private BoardListener listener;

  public BoardPanel(BoardSnapshot boardSnapshot, BoardListener listener) {
    board = boardSnapshot;
    this.listener = listener;

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        int cellSize = getWidth() / 8;
        int col = e.getX() / cellSize;
        int row = 7 - (e.getY() / cellSize);
        if (BoardPanel.this.listener != null)
          BoardPanel.this.listener.onCellClicked(new Coords(row, col));
      }
    });
    addKeyListener(null);
  }

  public void setBoard(BoardSnapshot boardSnapshot) {
    board = boardSnapshot;
    revalidate();
    repaint();
  }

  public void setListener(BoardListener boardListener) {
    listener = boardListener;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;

    int cellSize = getWidth() / 8;

    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {

        int x = col * cellSize;
        int y = (7 - row) * cellSize;

        g2d.setColor((row + col) % 2 == 0
            ? new java.awt.Color(240, 217, 181)
            : new java.awt.Color(181, 136, 99));
        g2d.fillRect(x, y, cellSize, cellSize);
      }
    }

    if (board.getSelectedCell() != null) {
      g2d.setStroke(new BasicStroke(4));
      g2d.setColor(new java.awt.Color(252, 232, 3));
      g2d.drawRect(board.getSelectedCell().col() * cellSize,
          (7 - board.getSelectedCell().row()) * cellSize, cellSize, cellSize);
    }

    if (board.getPieces() != null) {
      for (PieceSnapshot piece : board.getPieces()) {
        int x = piece.coords().col() * cellSize;
        int y = (7 - piece.coords().row()) * cellSize;

        BufferedImage img = ImageLoader.getPieceImage(piece.type().name(), piece.player(), cellSize);
        if (img != null) {
          g2d.drawImage(img, x, y, cellSize, cellSize, null);
        }
      }
    }

    if (board.getLegalMoves() != null) {
      for (Move move : board.getLegalMoves()) {
        int x = move.end().col() * cellSize;
        int y = (7 - move.end().row()) * cellSize;
        int padding = cellSize / 6;
        int size = cellSize - 2 * padding;

        boolean isCapture = board.getPieces().stream()
            .anyMatch(p -> p.coords().equals(move.end()) && p.player() != move.piece().getOwner().id);

        if (isCapture) {
          g2d.setColor(new java.awt.Color(255, 0, 0));
          g2d.setStroke(new BasicStroke(3));
          g2d.drawLine(x + padding, y + padding, x + padding + size, y + padding + size);
          g2d.drawLine(x + padding + size, y + padding, x + padding, y + padding + size);
        } else {
          g2d.setColor(new java.awt.Color(0, 255, 0, 180));
          g2d.fillOval(x + padding, y + padding, size, size);
        }
      }
    }
  }
}
