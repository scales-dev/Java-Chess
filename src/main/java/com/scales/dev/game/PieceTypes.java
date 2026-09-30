package com.scales.dev.game;

import com.scales.dev.Main;
import java.util.HashMap;

public enum PieceTypes {
  King {
    @Override
    public HashMap<Square, Move> getValidSquares(Piece piece) {
      HashMap<Square, Move> moves = new HashMap<>();

      for (Directions direction : Directions.values()) {
        addPossibleMove(piece, piece.position.offset(direction), moves);
      }

      return moves;
    }
  },
  Queen {
    @Override
    public HashMap<Square, Move> getValidSquares(Piece piece) {
      HashMap<Square, Move> squares = new HashMap<>();

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
    public HashMap<Square, Move> getValidSquares(Piece piece) {
      HashMap<Square, Move> moves = new HashMap<>();

      return moves;
    }
  },
  Castle {
    @Override
    public HashMap<Square, Move> getValidSquares(Piece piece) {
      HashMap<Square, Move> moves = new HashMap<>();

      return moves;
    }
  },
  Bishop {
    @Override
    public HashMap<Square, Move> getValidSquares(Piece piece) {
      HashMap<Square, Move> moves = new HashMap<>();

      return moves;
    }
  },
  // honestly the pawn should be handled completely separately, hes so weird
  // todo: make en passant take the piece
  //  promotion
  Pawn {
    @Override
    public HashMap<Square, Move> getValidSquares(Piece piece) {
      HashMap<Square, Move> moves = new HashMap<>();

      Directions direction = piece.WHITE ? Directions.SOUTH : Directions.NORTH;
      Board board = Main.getBoard();

      if (addPossibleMove(null, piece.position.offset(direction), moves) && piece.timesMoved == 0) {
        addPossibleMove(null, piece.position.offset(direction, 2), moves);
      }

      // en passant
      for (Directions d : new Directions[] {Directions.EAST, Directions.WEST}) {
        Square square = piece.position.offset(d);
        if (notContainsEnemy(piece, square)) continue;

        Piece enPassantPiece = board.getPiece(square);
        if (board.getLastMove(!piece.WHITE) != enPassantPiece
            || enPassantPiece.timesMoved != 1
            || enPassantPiece.TYPE != PieceTypes.Pawn
            || enPassantPiece.lastDistance.sum() != 2) continue;

        addPossibleMove(piece, square.offset(direction), enPassantPiece, moves);
      }

      // taking normally
      for (Directions d :
          new Directions[] {direction.rotateClockwise(), direction.rotateCounterClockwise()}) {
        Square square = piece.position.offset(d);
        if (notContainsEnemy(piece, square)) continue;
        addPossibleMove(piece, square, moves);
      }

      return moves;
    }
  };

  public abstract HashMap<Square, Move> getValidSquares(Piece piece);

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

    // pawn moves pass "piece" as null, because they cant take while walking, cause they weak n
    // scrawny
    return pieceAtPosition != null && (piece == null || piece.WHITE == pieceAtPosition.WHITE);
  }

  protected boolean notContainsEnemy(Piece piece, Square position) {
    if (isInvalidSquare(position)) return true;

    Piece pieceAtPosition = Main.getBoard().getPiece(position);

    return pieceAtPosition == null || piece.WHITE == pieceAtPosition.WHITE;
  }

  protected boolean addPossibleMove(
      Piece piece, Square finalPosition, HashMap<Square, Move> possibleMoves) {
    return this.addPossibleMove(piece, finalPosition, null, possibleMoves);
  }

  protected boolean addPossibleMove(
      Piece piece, Square finalPosition, Piece capturedPiece, HashMap<Square, Move> possibleMoves) {
    if (isInvalidSquare(finalPosition) || containsFriend(piece, finalPosition)) return false;

    if (capturedPiece == null) capturedPiece = Main.getBoard().getPiece(finalPosition);
    possibleMoves.put(finalPosition, new Move(piece, capturedPiece));

    return true;
  }
}
