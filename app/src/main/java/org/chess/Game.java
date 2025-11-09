package org.chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.chess.pieces.Bishop;
import org.chess.pieces.King;
import org.chess.pieces.Knight;
import org.chess.pieces.Pawn;
import org.chess.pieces.Piece;
import org.chess.pieces.Queen;
import org.chess.pieces.Rook;
import org.chess.players.Player;
import org.chess.utils.BoardSnapshot;
import org.chess.utils.Coords;
import org.chess.utils.Move;
import org.chess.utils.MoveParser;
import org.chess.utils.MoveSnapshot;
import org.chess.utils.PieceSnapshot;
import org.chess.utils.PieceType;
import org.chess.utils.SpecialEvent;
import org.ui.utils.BoardListener;
import org.ui.utils.ControlListener;

public class Game implements BoardListener, ControlListener {
  private final int BOARD_SIZE = 8;
  private Player p1, p2;
  private int activePlayer;
  private HashMap<Coords, Piece> pieces;
  private Coords selectedCell;
  private Piece selectedPiece;
  private ArrayList<MoveSnapshot> moveHistory;
  private boolean waitingForCoronation = false;

  private Runnable onBoardUpdated;
  private Runnable onMove;
  private Runnable onUndo;
  private Consumer<Consumer<String>> onCoronation;

  public Game() {
    init();
    initWithPieces();
  }

  public Game(HashMap<Coords, Piece> pieces) {
    init();
    this.pieces = new HashMap<>(pieces);
  }

  private void init() {
    p1 = new Player();
    p2 = new Player();
    activePlayer = 1;
    pieces = new HashMap<>();
    moveHistory = new ArrayList<>();
  }

  public void setOnBoardUpdated(Runnable callback) {
    onBoardUpdated = callback;
  }

  public void setOnMove(Runnable callback) {
    onMove = callback;
  }

  public void setOnUndo(Runnable callback) {
    onUndo = callback;
  }

  public void setOnCoronation(Consumer<Consumer<String>> callback) {
    this.onCoronation = callback;
  }

  @Override
  public void onCellClicked(Coords coords) {
    if (waitingForCoronation)
      return;
    if (selectedPiece == null) {
      Piece piece = pieces.get(coords);
      if (piece != null && piece.getOwner().id == activePlayer) {
        selectedPiece = piece;
        selectedCell = coords;
      }
    } else {
      ArrayList<Move> legalMoves = getLegalMovesOf(selectedCell);
      Move chosenMove = null;
      for (Move move : legalMoves) {
        if (move.end().equals(coords)) {
          chosenMove = move;
          break;
        }
      }

      if (chosenMove != null) {
        move(chosenMove);
        selectedPiece = null;
        selectedCell = null;
      } else {
        Piece piece = pieces.get(coords);
        if (piece != null && piece.getOwner().id == activePlayer) {
          selectedPiece = piece;
          selectedCell = coords;
        } else {
          selectedPiece = null;
          selectedCell = null;
        }
      }
    }

    if (onBoardUpdated != null)
      onBoardUpdated.run();
  }

  @Override
  public void onMove(Move move, boolean isCheck, boolean isCheckmate, boolean isStalemate) {
    if (onMove != null) {
      onMove.run();
    }
  }

  @Override
  public void onUndo() {
    undo();
    if (onBoardUpdated != null) {
      onBoardUpdated.run();
    }
  }

  @Override
  public void onReset() {
    activePlayer = 1;
    pieces.clear();
    selectedCell = null;
    selectedPiece = null;
    moveHistory.clear();
    waitingForCoronation = false;
    initWithPieces();
    if (onBoardUpdated != null) {
      onBoardUpdated.run();
    }
  }

  public void initWithPieces() {
    for (int i = 0; i < BOARD_SIZE; i++) {
      addPiece(new Coords(1, i), new Pawn(p1));
      addPiece(new Coords(6, i), new Pawn(p2));
    }
    addPiece(new Coords(0, 0), new Rook(p1));
    addPiece(new Coords(0, 1), new Knight(p1));
    addPiece(new Coords(0, 2), new Bishop(p1));
    addPiece(new Coords(0, 3), new Queen(p1));
    addPiece(new Coords(0, 4), new King(p1));
    addPiece(new Coords(0, 5), new Bishop(p1));
    addPiece(new Coords(0, 6), new Knight(p1));
    addPiece(new Coords(0, 7), new Rook(p1));
    addPiece(new Coords(7, 0), new Rook(p2));
    addPiece(new Coords(7, 1), new Knight(p2));
    addPiece(new Coords(7, 2), new Bishop(p2));
    addPiece(new Coords(7, 3), new Queen(p2));
    addPiece(new Coords(7, 4), new King(p2));
    addPiece(new Coords(7, 5), new Bishop(p2));
    addPiece(new Coords(7, 6), new Knight(p2));
    addPiece(new Coords(7, 7), new Rook(p2));
  }

  public BoardSnapshot getBoardSnapshot() {
    ArrayList<PieceSnapshot> pieceSnapshots = new ArrayList<>();
    for (Map.Entry<Coords, Piece> e : pieces.entrySet()) {
      pieceSnapshots.add(new PieceSnapshot(e.getKey(), e.getValue().getType(), e.getValue().getOwner().id));
    }
    ArrayList<Move> legalMoves = new ArrayList<>();
    if (selectedCell != null) {
      legalMoves = getLegalMovesOf(selectedCell);
    }
    return new BoardSnapshot(pieceSnapshots, selectedCell, legalMoves);
  }

  public String getLastMove() {
    int opponentId = getOpponent(activePlayer);
    boolean isCheck = isKingInCheck(opponentId);
    boolean isCheckmate = isCheckmate(opponentId);
    boolean isStalemate = isStalemate(opponentId);
    boolean isCapture = moveHistory.getLast().captured() != null;
    PieceType coronationType = pieces.get(moveHistory.getLast().move().end()).getType();
    return MoveParser.parseMove(moveHistory.getLast().move(), isCapture, isCheck, isCheckmate, isStalemate, coronationType);
  }

  public HashMap<Coords, Piece> getPieces() {
    return pieces;
  }

  public Map<Coords, Piece> getPlayerPieces(int playerId) {
    if (playerId != 1 && playerId != 2)
      return Map.of();
    return pieces.entrySet().stream()
        .filter(e -> e.getValue().getOwner().id == playerId)
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue));
  }

  public Coords getSelectedCell() {
    return selectedCell;
  }

  public void setSelectedCell(Coords coords) {
    if (coords == null) {
      selectedCell = null;
      selectedPiece = null;
      return;
    }

    selectedCell = coords;
    selectedPiece = pieces.get(coords);
  }

  public Piece getSelectedPiece() {
    return selectedPiece;
  }

  public void addPiece(Coords coords, Piece piece) {
    if (coords != null && piece != null)
      pieces.putIfAbsent(coords, piece);
  }

  public void addPiece(Map.Entry<Coords, Piece> entry) {
    pieces.putIfAbsent(entry.getKey(), entry.getValue());
  }

  public void addPieces(HashMap<Coords, Piece> pieces) {
    pieces.entrySet().forEach(this::addPiece);
  }

  public void deletePiece(Map.Entry<Coords, Piece> entry) {
    if (entry != null) {
      deletePiece(entry.getKey());
    }
  }

  public void deletePiece(Coords coords) {
    pieces.remove(coords);

    if (coords != null && coords.equals(selectedCell)) {
      selectedCell = null;
      selectedPiece = null;
    }
  }

  public void move(Move move) {
    selectedCell = null;
    selectedPiece = null;

    Piece captured = pieces.remove(move.end());
    final Piece capturedPiece = captured;
    Piece moved = pieces.remove(move.start());
    moved.setMoved(true);
    addPiece(move.end(), moved);

    if (move.sEvent() == SpecialEvent.Coronation) {
      waitingForCoronation = true;
      pieces.remove(move.end());

      if (onCoronation != null) {
        onCoronation.accept(chosenPieceType -> {
          Piece promoted = switch (chosenPieceType) {
            case "♛" -> new Queen(move.piece().getOwner());
            case "♜" -> new Rook(move.piece().getOwner());
            case "♝" -> new Bishop(move.piece().getOwner());
            case "♞" -> new Knight(move.piece().getOwner());
            default -> move.piece();
          };
          addPiece(move.end(), promoted);
          moveHistory.add(new MoveSnapshot(move, capturedPiece));
          if (onMove != null)
            onMove.run();
          passTurn();

          if (onBoardUpdated != null)
            onBoardUpdated.run();
          waitingForCoronation = false;
        });
      }
      return;
    }

    if (move.sEvent() == SpecialEvent.Castle) {
      int row = move.start().row();
      if (move.end().col() == 6) {
        Piece rook = pieces.remove(new Coords(row, 7));
        rook.setMoved(true);
        addPiece(new Coords(row, 5), rook);
      } else if (move.end().col() == 2) {
        Piece rook = pieces.remove(new Coords(row, 0));
        rook.setMoved(true);
        addPiece(new Coords(row, 3), rook);
      }
    }

    if (move.sEvent() == SpecialEvent.EnPassant) {
      int row = move.start().row();
      captured = pieces.remove(new Coords(row, move.end().col()));
    }

    moveHistory.add(new MoveSnapshot(move, captured));
    if (onMove != null) {
      onMove.run();
    }
    passTurn();
  }

  public void undo() {
    if (moveHistory.isEmpty())
      return;
    selectedCell = null;
    selectedPiece = null;

    MoveSnapshot lastMove = moveHistory.removeLast();
    Move move = lastMove.move();
    pieces.remove(move.end());
    Piece piece = move.piece();
    if (move.sEvent() == SpecialEvent.DoublePawnMove || move.sEvent() == SpecialEvent.Castle)
      piece.setMoved(false);
    addPiece(move.start(), piece);

    if (move.sEvent() == SpecialEvent.Coronation) {
      Piece originalPawn = new Pawn(piece.getOwner());
      addPiece(move.start(), originalPawn);
    } else {
      addPiece(move.start(), piece);
    }

    if (lastMove.captured() != null) {
      int row = move.sEvent() == SpecialEvent.EnPassant ? move.start().row() : move.end().row();
      int col = move.end().col();
      addPiece(new Coords(row, col), lastMove.captured());
    }

    if (move.sEvent() == SpecialEvent.Castle) {
      int row = move.start().row();
      if (move.end().col() == 6) {
        Piece rook = pieces.remove(new Coords(row, 5));
        rook.setMoved(false);
        // also for the king
        addPiece(new Coords(row, 7), rook);
      } else if (move.end().col() == 2) {
        Piece rook = pieces.remove(new Coords(row, 3));
        rook.setMoved(false);
        // also for the king
        addPiece(new Coords(row, 0), rook);
      }
    }

    activePlayer = move.piece().getOwner().id;

    if (onUndo != null) {
      onUndo.run();
    }
  }

  public void passTurn() {
    activePlayer = getOpponent(activePlayer);
  }

  private ArrayList<Move> getValidMovesOf(Coords pieceCoords) {
    ArrayList<Move> validMoves = new ArrayList<>();
    if (pieceCoords == null)
      return validMoves;
    Map<Coords, Piece> allyPieces = getPlayerPieces(pieces.get(pieceCoords).getOwner().id);
    Piece piece = allyPieces.remove(pieceCoords);
    Map<Coords, Piece> enemyPieces = getPlayerPieces(getOpponent(piece.getOwner().id));

    switch (piece.getType()) {
      case King:
        int[][] kingDirs = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };
        for (int[] dir : kingDirs) {
          int newRow = pieceCoords.row() + dir[0];
          int newCol = pieceCoords.col() + dir[1];
          if (newRow < 0 || newRow >= BOARD_SIZE || newCol < 0 || newCol >= BOARD_SIZE)
            continue;
          Coords next = new Coords(newRow, newCol);
          if (!allyPieces.containsKey(next))
            validMoves.add(new Move(piece, pieceCoords, next, null));
        }
        if (!piece.getMoved()) {
          int[][] castleOptions = {
              { 0, 2, 1, 2, 3 },
              { 7, 6, 5, 6 }
          };
          for (int[] option : castleOptions) {
            Piece rook = pieces.get(new Coords(pieceCoords.row(), option[0]));
            if (rook != null && !rook.getMoved()) {
              boolean cleared = true;
              for (int i = 2; i < option.length; i++) {
                if (pieces.containsKey(new Coords(pieceCoords.row(), option[i]))) {
                  cleared = false;
                  break;
                }
              }
              if (cleared)
                validMoves
                    .add(new Move(piece, pieceCoords, new Coords(pieceCoords.row(), option[1]), SpecialEvent.Castle));
            }
          }
        }
        break;
      case Queen:
        int[][] queenDirections = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 },
            { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };
        validMoves = getDirectionalMoves(queenDirections, pieceCoords, allyPieces, enemyPieces);
        break;
      case Pawn:
        int dir = piece.getOwner().id == 1 ? 1 : -1;
        int plusRow = pieceCoords.row() + dir;
        SpecialEvent sEvent = (dir == 1 && plusRow == 7) || (dir == -1 && plusRow == 0) ? SpecialEvent.Coronation
            : null;
        Coords oneUp = new Coords(plusRow, pieceCoords.col());
        if (!pieces.containsKey(oneUp)) {
          validMoves.add(new Move(piece, pieceCoords, oneUp, sEvent));
          Coords twoUp = new Coords(plusRow + dir, pieceCoords.col());
          if (!piece.getMoved() && !pieces.containsKey(twoUp)) {
            validMoves.add(new Move(piece, pieceCoords, twoUp, SpecialEvent.DoublePawnMove));
          }
        }
        int leftCol = pieceCoords.col() - 1;
        int rightCol = pieceCoords.col() + 1;
        Coords takeLeft = new Coords(plusRow, leftCol);
        Coords takeRight = new Coords(plusRow, rightCol);
        if (enemyPieces.containsKey(takeLeft))
          validMoves.add(new Move(piece, pieceCoords, takeLeft, sEvent));
        if (enemyPieces.containsKey(takeRight))
          validMoves.add(new Move(piece, pieceCoords, takeRight, sEvent));
        if (!moveHistory.isEmpty() && moveHistory.getLast().move().sEvent() != null
            && moveHistory.getLast().move().sEvent().equals(SpecialEvent.DoublePawnMove)
            && moveHistory.getLast().move().end().row() == pieceCoords.row()) {
          if (moveHistory.getLast().move().end().col() == leftCol)
            validMoves.add(new Move(piece, pieceCoords, takeLeft, SpecialEvent.EnPassant));
          if (moveHistory.getLast().move().end().col() == rightCol)
            validMoves.add(new Move(piece, pieceCoords, takeRight, SpecialEvent.EnPassant));
        }
        break;
      case Rook:
        int[][] rookDirections = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };
        validMoves = getDirectionalMoves(rookDirections, pieceCoords, allyPieces, enemyPieces);
        break;
      case Knight:
        int[][] jumps = { { 2, 1 }, { -2, 1 }, { 2, -1 }, { -2, -1 }, { 1, 2 }, { -1, 2 }, { 1, -2 }, { -1, -2 } };
        for (int[] jump : jumps) {
          int newRow = pieceCoords.row() + jump[0];
          int newCol = pieceCoords.col() + jump[1];
          if (newRow < 0 || newRow >= BOARD_SIZE || newCol < 0 || newCol >= BOARD_SIZE)
            continue;
          Coords newCoords = new Coords(newRow, newCol);
          if (allyPieces.containsKey(newCoords))
            continue;
          validMoves.add(new Move(piece, pieceCoords, newCoords, null));
        }
        break;
      case Bishop:
        int[][] bishopDirections = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
        validMoves = getDirectionalMoves(bishopDirections, pieceCoords, allyPieces, enemyPieces);
        break;
    }
    return validMoves;
  }

  private ArrayList<Move> getDirectionalMoves(
      int[][] directions,
      Coords pieceCoords,
      Map<Coords, Piece> allyPieces,
      Map<Coords, Piece> enemyPieces) {
    ArrayList<Move> validMoves = new ArrayList<>();
    for (int[] dir : directions) {
      int row = pieceCoords.row() + dir[0];
      int col = pieceCoords.col() + dir[1];
      while (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
        Coords next = new Coords(row, col);
        if (allyPieces.containsKey(next))
          break;
        validMoves.add(new Move(pieces.get(pieceCoords), pieceCoords, next, null));
        if (enemyPieces.containsKey(next))
          break;
        row += dir[0];
        col += dir[1];
      }
    }
    return validMoves;
  }

  private boolean isKingInCheck(int playerId) {
    Coords kingCoords = null;

    for (Map.Entry<Coords, Piece> entry : pieces.entrySet()) {
      Piece piece = entry.getValue();
      if (piece.getOwner().id == playerId && piece.getType() == PieceType.King) {
        kingCoords = entry.getKey();
        break;
      }
    }
    if (kingCoords == null)
      throw new IllegalStateException("King not found for player " + playerId);
    int enemyId = getOpponent(playerId);
    Map<Coords, Piece> enemyPieces = getPlayerPieces(enemyId);
    for (Map.Entry<Coords, Piece> entry : enemyPieces.entrySet()) {
      Coords pieceCoords = entry.getKey();
      ArrayList<Move> enemyMoves = getValidMovesOf(pieceCoords);

      for (Move move : enemyMoves) {
        if (move.end().equals(kingCoords)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isCheckmate(int playerId) {
    return getAllLegalMoves(playerId).isEmpty() && isKingInCheck(playerId);
  }

  public boolean isStalemate(int playerId) {
    return getAllLegalMoves(playerId).isEmpty() && !isKingInCheck(playerId);
  }

  public ArrayList<Move> getAllLegalMoves(int playerId) {
    ArrayList<Move> allLegalMoves = new ArrayList<>();

    Map<Coords, Piece> playerPieces = getPlayerPieces(playerId);

    for (Coords coords : playerPieces.keySet()) {
      ArrayList<Move> legalMoves = getLegalMovesOf(coords);
      allLegalMoves.addAll(legalMoves);
    }

    return allLegalMoves;
  }

  private ArrayList<Move> getLegalMovesOf(Coords pieceCoords) {
    ArrayList<Move> validMoves = getValidMovesOf(pieceCoords);
    ArrayList<Move> legalMoves = new ArrayList<>();
    for (Move move : validMoves) {
      Piece captured = pieces.remove(move.end());
      Piece moved = pieces.remove(move.start());
      pieces.put(move.end(), moved);

      boolean kingInCheck = isKingInCheck(moved.getOwner().id);

      pieces.remove(move.end());
      pieces.put(move.start(), moved);
      if (captured != null)
        pieces.put(move.end(), captured);

      if (!kingInCheck) {
        if (move.sEvent() == SpecialEvent.Castle) {
          Coords start = move.start();
          Coords end = move.end();
          int row = start.row();
          int step = (end.col() > start.col()) ? 1 : -1;
          boolean pathSafe = true;
          for (int col = start.col(); col != end.col() + step; col += step) {
            if (isSquareAttacked(new Coords(row, col), getOpponent(moved.getOwner().id))) {
              pathSafe = false;
              break;
            }
          }
          if (pathSafe)
            legalMoves.add(move);
        } else {
          legalMoves.add(move);
        }
      }
      // check if the move puts (or leaves) the own king in check position
      if (move.sEvent() == SpecialEvent.Castle) {
        // check if the own king is already in check
        // check if any cell in the path of the castle is attacked
      }
    }
    return legalMoves;
  }

  private boolean isSquareAttacked(Coords square, int enemyId) {
    Map<Coords, Piece> enemyPieces = getPlayerPieces(enemyId);
    for (Map.Entry<Coords, Piece> entry : enemyPieces.entrySet()) {
      ArrayList<Move> moves = getValidMovesOf(entry.getKey());
      for (Move move : moves) {
        if (move.end().equals(square))
          return true;
      }
    }
    return false;
  }

  private int getOpponent(int playerId) {
    return playerId == 1 ? 2 : 1;
  }
}
