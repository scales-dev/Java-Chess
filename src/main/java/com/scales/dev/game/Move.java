package com.scales.dev.game;

import com.scales.dev.Main;
import java.util.HashMap;

public record Move(Piece piece, MoveAction moveAction) {
  public static boolean movePiece(Board board, Piece piece, Square position) {
    if (piece == null) return false;
    HashMap<Square, Move> possibleMoves = piece.type.getValidSquares(board, piece);
    Move move = possibleMoves.get(position);

    if (move == null) return false;

    Square startPos = piece.position;
    updateBoard(board, move, position);
    updatePiece(piece, startPos, position);

    return true;
  }

  private static void updatePiece(Piece piece, Square oldPosition, Square newPosition) {
    piece.timesMoved++;
    piece.lastMoved = System.currentTimeMillis();
    piece.lastDistance = oldPosition.difference(newPosition);
    piece.position = newPosition;

    if (shouldPromote(piece)) {
      promote(piece);
    }
  }

  private static void updateBoard(Board board, Move move, Square to) {
    // move from old position
    board.pieces[move.piece.position.x()][move.piece.position.y()] = null;

    // generally killing a piece, sometimes switching
    move.moveAction.action().run();
    if (shouldPromote(move.piece)) promote(move.piece);

    // move to new position, replacing whatever was there,
    // although there is likely nothing, unless the "captured piece" is smth random
    board.pieces[to.x()][to.y()] = move.piece;

    board.getTeam(move.piece.colour).lastMovedPiece = move.piece;

    ChessColour[] values = ChessColour.values();
    board.currentTurn = values[(board.currentTurn.ordinal() + 1) % values.length];
  }

  public static void killPiece(Board board, Piece piece) {
    if (piece == null) return;

    // kill the opps
    board.pieces[piece.position.x()][piece.position.y()] = null;
  }

  public static void promote(Piece piece) {
    piece.type = PieceType.QUEEN;
  }

  public static boolean shouldPromote(Piece piece) {
    int nextY = piece.position.offset(piece.colour.pawnDirection).y();
    return piece.type == PieceType.PAWN && (nextY < 0 || nextY >= Main.getBoard().pieces[0].length);
  }
}
