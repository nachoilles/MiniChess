package org.ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.ui.utils.ControlListener;

public class ControlPanel extends JPanel {
  private final int width, height;
  private final ArrayList<String> moveHistory;
  private ControlListener listener;
  private JTextArea moveHistoryArea;
  private JButton undoButton;
  private JButton copyToClipboardButton;
  private JButton saveButton;
  private JButton resetButton;
  private JPanel coronationPanel;

  public ControlPanel(int boardSize, ControlListener listener) {
    height = boardSize;
    width = height / 4;
    this.moveHistory = new ArrayList<>();
    this.listener = listener;
    init();
  }

  private void init() {
    setPreferredSize(new Dimension(width, height));
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    moveHistoryArea = new JTextArea();
    moveHistoryArea.setEditable(false);
    moveHistoryArea.setLineWrap(true);
    moveHistoryArea.setWrapStyleWord(true);
    moveHistoryArea.setFocusable(false);
    moveHistoryArea.setFont(moveHistoryArea.getFont().deriveFont(16f));
    JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setPreferredSize(new Dimension(width, height - (width / 2)));
    scrollPane.setMinimumSize(new Dimension(width, height - (width / 2)));
    scrollPane.setMaximumSize(new Dimension(width, height - (width / 4)));

    JPanel historyPanel = new JPanel();
    historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.X_AXIS));
    historyPanel.setPreferredSize(new Dimension(width, 50));
    historyPanel.setMinimumSize(new Dimension(width, 50));
    historyPanel.setMaximumSize(new Dimension(width, 50));

    undoButton = new JButton("↶");
    undoButton.setMargin(new Insets(0, 0, 0, 0));
    undoButton.setFont(undoButton.getFont().deriveFont(24f));
    undoButton.setAlignmentX(1.0f);
    undoButton.setPreferredSize(new Dimension(width / 4, width / 4));
    undoButton.setMinimumSize(new Dimension(width / 4, width / 4));
    undoButton.setMaximumSize(new Dimension(width / 4, width / 4));
    undoButton.addActionListener(e -> {
      if (listener != null)
        listener.onUndo();
    });

    copyToClipboardButton = new JButton("📋");
    copyToClipboardButton.setMargin(new Insets(0, 0, 0, 0));
    copyToClipboardButton.setFont(copyToClipboardButton.getFont().deriveFont(20f));
    copyToClipboardButton.setAlignmentX(1.0f);
    copyToClipboardButton.setPreferredSize(new Dimension(width / 4, width / 4));
    copyToClipboardButton.setMinimumSize(new Dimension(width / 4, width / 4));
    copyToClipboardButton.setMaximumSize(new Dimension(width / 4, width / 4));
    copyToClipboardButton.addActionListener(e -> {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < moveHistory.size(); i++) {
        if (i % 2 == 0) {
          sb.append(i / 2 + 1).append(". ").append(moveHistory.get(i));
        } else {
          sb.append(" - ").append(moveHistory.get(i)).append("\n");
        }
      }
      StringSelection selection = new StringSelection(sb.toString());
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    });

    saveButton = new JButton("💾");
    saveButton.setMargin(new Insets(0, 0, 0, 0));
    saveButton.setFont(saveButton.getFont().deriveFont(20f));
    saveButton.setAlignmentX(1.0f);
    saveButton.setPreferredSize(new Dimension(width / 4, width / 4));
    saveButton.setMinimumSize(new Dimension(width / 4, width / 4));
    saveButton.setMaximumSize(new Dimension(width / 4, width / 4));
    saveButton.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Save Move History");
      fileChooser.setSelectedFile(new java.io.File("move_history.txt"));
      int userSelection = fileChooser.showSaveDialog(this);
      if (userSelection == JFileChooser.APPROVE_OPTION) {
        java.io.File fileToSave = fileChooser.getSelectedFile();
        try (java.io.FileWriter writer = new java.io.FileWriter(fileToSave)) {
          for (int i = 0; i < moveHistory.size(); i++) {
            if (i % 2 == 0) {
              writer.write((i / 2 + 1) + ". " + moveHistory.get(i));
            } else {
              writer.write(" - " + moveHistory.get(i) + "\n");
            }
          }
          if (moveHistory.size() % 2 != 0)
            writer.write("\n");
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });

    resetButton = new JButton("↺");
    resetButton.setMargin(new Insets(0, 0, 0, 0));
    resetButton.setFont(resetButton.getFont().deriveFont(24f));
    resetButton.setAlignmentX(1.0f);
    resetButton.setPreferredSize(new Dimension(width / 4, width / 4));
    resetButton.setMinimumSize(new Dimension(width / 4, width / 4));
    resetButton.setMaximumSize(new Dimension(width / 4, width / 4));
    resetButton.addActionListener(e -> {
      if (listener != null) {
        moveHistory.clear();
        moveHistoryArea.setText("");
        listener.onReset();
      }
    });

    historyPanel.add(undoButton);
    historyPanel.add(copyToClipboardButton);
    historyPanel.add(saveButton);
    historyPanel.add(resetButton);

    coronationPanel = new JPanel();
    coronationPanel.setLayout(new BoxLayout(coronationPanel, BoxLayout.X_AXIS));
    coronationPanel.setPreferredSize(new Dimension(width, width / 4));
    coronationPanel.setMinimumSize(new Dimension(width, width / 4));
    coronationPanel.setMaximumSize(new Dimension(width, width / 4));
    coronationPanel.setVisible(false);

    String[] coronationButtonNames = { "♛", "♜", "♞", "♝" };
    for (String string : coronationButtonNames) {
      JButton button = new JButton(string);
      button.setMargin(new Insets(0, 0, 0, 0));
      button.setFont(undoButton.getFont().deriveFont(20f));
      button.setPreferredSize(new Dimension(width / 4, width / 4));
      button.setMinimumSize(new Dimension(width / 4, width / 4));
      button.setMaximumSize(new Dimension(width / 4, width / 4));
      coronationPanel.add(button);
    }

    add(historyPanel);
    add(scrollPane);
    add(coronationPanel);
    revalidate();
    repaint();
  }

  public void setListener(ControlListener listener) {
    this.listener = listener;
  }

  public void setMoveHistory(ArrayList<String> moveHistory) {
    this.moveHistory.clear();
    this.moveHistory.addAll(moveHistory);
    refreshMoveHistory();
  }

  public ArrayList<String> getMoveHistory() {
    return new ArrayList<>(moveHistory);
  }

  public void addMove(String move) {
    moveHistory.add(move);
    refreshMoveHistory();
  }

  public void removeLastMove() {
    if (!moveHistory.isEmpty()) {
      moveHistory.remove(moveHistory.size() - 1);
      refreshMoveHistory();
    }
  }

  private void refreshMoveHistory() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < moveHistory.size(); i++) {
      if (i % 2 == 0) {
        sb.append(i / 2 + 1).append(". ").append(moveHistory.get(i));
      } else {
        sb.append(" - ").append(moveHistory.get(i)).append("\n");
      }
    }
    moveHistoryArea.setText(sb.toString());
    moveHistoryArea.setCaretPosition(moveHistoryArea.getDocument().getLength());
  }

  public void requestCoronation(java.util.function.Consumer<String> onChoose) {
    coronationPanel.setVisible(true);
    undoButton.setEnabled(false);
    copyToClipboardButton.setEnabled(false);
    saveButton.setEnabled(false);
    resetButton.setEnabled(false);
    String[] pieces = { "♛", "♜", "♞", "♝" };
    for (int i = 0; i < pieces.length; i++) {
      JButton btn = (JButton) coronationPanel.getComponent(i);
      for (var al : btn.getActionListeners())
        btn.removeActionListener(al);

      int index = i;
      btn.addActionListener(e -> {
        coronationPanel.setVisible(false);
        onChoose.accept(pieces[index]);
        undoButton.setEnabled(true);
        copyToClipboardButton.setEnabled(true);
        saveButton.setEnabled(true);
        resetButton.setEnabled(true);
      });
    }
    coronationPanel.revalidate();
    coronationPanel.repaint();
  }
}