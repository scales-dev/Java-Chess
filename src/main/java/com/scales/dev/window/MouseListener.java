package com.scales.dev.window;

import com.scales.dev.Main;
import com.scales.dev.game.Board;
import com.scales.dev.game.Piece;
import com.scales.dev.game.Square;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

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

  private boolean movePiece(Board board, Piece piece, Square position) {
    if (piece != null) {
      ArrayList<Square> possibleMoves = piece.TYPE.getValidSquares(piece);
      if (possibleMoves.contains(position)) {
        Square currentPosition = piece.position;

        // move from old position
        board.pieces[currentPosition.x()][currentPosition.y()] = null;

        // move to new position, replacing whatever was there
        board.pieces[position.x()][position.y()] = piece;
        piece.position = position;

        // we dont wanna stay selecting the same piece </3
        board.selectedPiece = null;

        return true;
      }
    }

    return false;
  }
}