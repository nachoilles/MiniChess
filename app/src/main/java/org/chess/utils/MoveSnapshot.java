package org.chess.utils;

import org.chess.pieces.Piece;

public record MoveSnapshot(Move move, Piece captured) {}
