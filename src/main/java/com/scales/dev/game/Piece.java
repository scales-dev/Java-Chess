package com.scales.dev.game;

import java.awt.*;
import java.util.HashMap;

public class Piece {

  public PieceType type;
  public final ChessColour colour;

  public long lastMoved = -1;
  public int timesMoved = 0;
  public Square lastDistance = new Square(0, 0);

  public Square position;

  public Piece(PieceType piece, ChessColour colour, Square position) {
    this.type = piece;
    this.colour = colour;

    this.position = position;
  }

  public void draw(Graphics2D g, int size) {
    type.draw(g, size, colour);
  }

  public boolean wouldCheck(Square targetSquare, Board board, boolean verifyingCheck) {
    if (verifyingCheck || targetSquare.isInvalidSquare(board.pieces)) return false;

    Piece pieceAtTarget = board.getPiece(targetSquare);
    // requires simulating the board at the new position
    // because some pieces *cough pawns* gain new options
    // also the king could be body blocking a square himself
    board.pieces[this.position.x()][this.position.y()] = null;
    board.pieces[targetSquare.x()][targetSquare.y()] = this;

    boolean isChecked = this.isChecked(targetSquare, board);

    // undo!
    board.pieces[this.position.x()][this.position.y()] = this;
    board.pieces[targetSquare.x()][targetSquare.y()] = pieceAtTarget;

    return isChecked;
  }

  public boolean isChecked(Square targetSquare, Board board) {
    for (Piece[] row : board.pieces) {
      for (Piece piece : row) {
        if (piece == null || piece.colour == this.colour) continue;

        HashMap<Square, Move> moves = piece.type.getValidSquares(board, piece, true);

        for (Square square : moves.keySet()) {
          Move move = moves.get(square);
          if (!move.moveAction().isCapture() || !targetSquare.equals(square)) continue;

          return true;
        }
      }
    }

    return false;
  }
}
