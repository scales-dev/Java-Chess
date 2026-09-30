package com.scales.dev.window;

import com.scales.dev.Main;
import com.scales.dev.game.Board;
import com.scales.dev.game.Move;
import com.scales.dev.game.Piece;
import com.scales.dev.game.Square;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

public class MouseListener extends MouseAdapter {
  @Override
  public void mousePressed(MouseEvent e) {
    Board board = Main.getBoard();

    Rectangle boardRectangle = board.getBoardRectangle();
    if (!boardRectangle.contains(e.getX(), e.getY())) return;

    float xRatio = (float) (e.getX() - boardRectangle.x) / boardRectangle.width;
    float yRatio = (float) (e.getY() - boardRectangle.y) / boardRectangle.height;

    int x = (int) (xRatio * Board.BOARD_SIZE);
    int y = (int) (yRatio * Board.BOARD_SIZE);

    if (!movePiece(board, board.selectedPiece, new Square(x, y))) {
      board.selectedPiece = board.pieces[x][y];
    }

    board.draw();
  }

  // todo: move this
  private boolean movePiece(Board board, Piece piece, Square position) {
    if (piece == null) return false;
    HashMap<Square, Move> possibleMoves = piece.TYPE.getValidSquares(piece);
    Move move = possibleMoves.get(position);

    if (move == null) return false;

    // move from old position
    board.pieces[piece.position.x()][piece.position.y()] = null;

    // update piece data
    piece.timesMoved++;
    piece.lastMoved = System.currentTimeMillis();
    piece.lastDistance = piece.position.difference(position);
    piece.position = position;

    if (move.capturedPiece() != null) {
      // kill the opps
      board.pieces[move.capturedPiece().position.x()][move.capturedPiece().position.y()] = null;
    }

    // move to new position, replacing whatever was there, although there is likely nothing, unless
    // the "captured piece" is smth random
    board.pieces[position.x()][position.y()] = piece;

    board.setLastMove(piece);

    // we dont wanna stay selecting the same piece </3
    board.selectedPiece = null;

    return true;
  }
}