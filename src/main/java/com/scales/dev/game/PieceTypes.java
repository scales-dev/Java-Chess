package com.scales.dev.game;

import com.scales.dev.Main;
import java.util.ArrayList;

public enum PieceTypes {
  King {
    @Override
    public ArrayList<Square> getValidSquares(Piece piece) {
      ArrayList<Square> squares = new ArrayList<>();

      for (Directions direction : Directions.values()) {
        addPossibleMove(piece, piece.position.offset(direction), squares);
      }

      return squares;
    }
  },
  Queen {
    @Override
    public ArrayList<Square> getValidSquares(Piece piece) {
      ArrayList<Square> squares = new ArrayList<>();

      for (Directions direction : Directions.values()) {
        Square nextPosition = piece.position;
        while (this.addPossibleMove(
            piece, nextPosition = nextPosition.offset(direction), squares)) {
          // we can take 1 piece, but not the piece behind.
          if (Main.getBoard().pieces[nextPosition.x()][nextPosition.y()] != null) break;
        }
      }

      return squares;
    }
  },
  Horse {
    @Override
    public ArrayList<Square> getValidSquares(Piece piece) {
      ArrayList<Square> squares = new ArrayList<>();

      return squares;
    }
  },
  Castle {
    @Override
    public ArrayList<Square> getValidSquares(Piece piece) {
      ArrayList<Square> squares = new ArrayList<>();

      return squares;
    }
  },
  Bishop {
    @Override
    public ArrayList<Square> getValidSquares(Piece piece) {
      ArrayList<Square> squares = new ArrayList<>();

      return squares;
    }
  },
  // honestly the pawn should be handled completely separately, hes so weird
  // todo: make en passant take the piece
  //  promotion
  Pawn {
    @Override
    public ArrayList<Square> getValidSquares(Piece piece) {
      ArrayList<Square> squares = new ArrayList<>();

      Directions direction = piece.WHITE ? Directions.SOUTH : Directions.NORTH;

      if (addPossibleMove(piece, piece.position.offset(direction), squares)
          && piece.timesMoved == 0) {
        addPossibleMove(piece, piece.position.offset(direction, 2), squares);
      }

      Board board = Main.getBoard();

      // en passant
      for (Directions d : new Directions[] {Directions.EAST, Directions.WEST}) {
        Square square = piece.position.offset(d);
        if (notContainsEnemy(piece, square)) continue;

        Piece enPassantPiece = board.getPiece(square);
        if (board.getLastMove(!piece.WHITE) != enPassantPiece
            || enPassantPiece.timesMoved != 1
            || enPassantPiece.TYPE != PieceTypes.Pawn
            || enPassantPiece.lastDistance.sum() != 2) continue;

        addPossibleMove(piece, square.offset(direction), squares);
      }

      // taking normally
      for (Directions d :
          new Directions[] {direction.rotateClockwise(), direction.rotateCounterClockwise()}) {
        Square square = piece.position.offset(d);
        if (notContainsEnemy(piece, square)) continue;
        addPossibleMove(piece, square, squares);
      }

      return squares;
    }
  };

  public abstract ArrayList<Square> getValidSquares(Piece piece);

  protected boolean isInvalidSquare(Square position) {
    Board board = Main.getBoard();

    return position.x() < 0
        || position.x() >= board.pieces.length
        || position.y() < 0
        || position.y() >= board.pieces[0].length;
  }

  protected boolean containsFriend(Piece piece, Square position) {
    if (isInvalidSquare(position)) return false;

    Piece pieceAtPosition = Main.getBoard().getPiece(position);

    return pieceAtPosition != null && piece.WHITE == pieceAtPosition.WHITE;
  }

  protected boolean notContainsEnemy(Piece piece, Square position) {
    if (isInvalidSquare(position)) return true;

    Piece pieceAtPosition = Main.getBoard().getPiece(position);

    return pieceAtPosition == null || piece.WHITE == pieceAtPosition.WHITE;
  }

  protected boolean addPossibleMove(
      Piece piece, Square finalPosition, ArrayList<Square> possibleMoves) {
    if (isInvalidSquare(finalPosition) || containsFriend(piece, finalPosition)) return false;
    possibleMoves.add(finalPosition);

    return true;
  }
}
