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
  Pawn {
    @Override
    public ArrayList<Square> getValidSquares(Piece piece) {
      ArrayList<Square> squares = new ArrayList<>();

      Directions direction = piece.WHITE ? Directions.SOUTH : Directions.NORTH;

      if (addPossibleMove(piece, piece.position.offset(direction), squares)
          && piece.timesMoved == 0) {
        addPossibleMove(piece, piece.position.offset(direction, 2), squares);
      }

      return squares;
    }
  };

  public abstract ArrayList<Square> getValidSquares(Piece piece);

  protected boolean isValidSquare(Piece piece, Square position) {
    Piece[][] boardSquare = Main.getBoard().pieces;

    if (position.x() < 0
        || position.x() >= boardSquare.length
        || position.y() < 0
        || position.y() >= boardSquare[0].length) return false;

    Piece pieceAtPosition = boardSquare[position.x()][position.y()];

    return pieceAtPosition == null || piece.WHITE != pieceAtPosition.WHITE;
  }

  protected boolean addPossibleMove(
      Piece piece, Square finalPosition, ArrayList<Square> possibleMoves) {
    if (!isValidSquare(piece, finalPosition)) return false;
    possibleMoves.add(finalPosition);

    return true;
  }
}
