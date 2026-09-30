package com.scales.dev.game;

import com.scales.dev.Main;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.imageio.ImageIO;

public enum PieceTypes {
  King {
    @Override
    public HashMap<Square, Move> getValidSquares(Piece piece) {
      HashMap<Square, Move> moves = new HashMap<>();

      for (Direction direction : Direction.values()) {
        addPossibleMove(piece, piece.position.offset(direction), moves);
      }

      return moves;
    }
  },
  Queen {
    @Override
    public HashMap<Square, Move> getValidSquares(Piece piece) {
      HashMap<Square, Move> squares = new HashMap<>();

      for (Direction direction : Direction.values()) {
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
  Pawn {
    @Override
    public HashMap<Square, Move> getValidSquares(Piece piece) {
      HashMap<Square, Move> moves = new HashMap<>();

      Direction direction = Move.getPawnDirection(piece);
      Board board = Main.getBoard();

      if (addPossibleMove(piece, piece.position.offset(direction), null, moves, false)
          && piece.timesMoved == 0) {
        addPossibleMove(piece, piece.position.offset(direction, 2), null, moves, false);
      }

      // en passant
      for (Direction d : new Direction[] {Direction.EAST, Direction.WEST}) {
        Square square = piece.position.offset(d);
        if (notContainsEnemy(piece, square)) continue;

        Piece enPassantPiece = board.getPiece(square);
        if (board.getLastMove(!piece.WHITE) != enPassantPiece
            || enPassantPiece.timesMoved != 1
            || enPassantPiece.type != PieceTypes.Pawn
            || enPassantPiece.lastDistance.sum() != 2) continue;

        addPossibleMove(piece, square.offset(direction), enPassantPiece, moves, true);
      }

      // taking normally
      for (Direction d :
          new Direction[] {direction.rotateClockwise(), direction.rotateCounterClockwise()}) {
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

  private static final RescaleOp INVERT_IMAGE =
      new RescaleOp(new float[] {-1f, -1f, -1f, 1f}, new float[] {255f, 255f, 255f, 0f}, null);
  private static final BufferedImage[] ICONS = new BufferedImage[PieceTypes.values().length * 2];

  public static void loadIcons() {
    PieceTypes[] values = PieceTypes.values();
    for (int i = 0; i < values.length; i++) {
      try {
        InputStream iconStream = Main.class.getResourceAsStream(values[i].getImagePath());
        // i dont like things being highlighted in yellow
        // iconStream WILL never be null, hopefully.
        assert iconStream != null;

        ICONS[i] = ImageIO.read(iconStream);
        ICONS[i + values.length] = INVERT_IMAGE.filter(ICONS[i], null);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  protected boolean addPossibleMove(
      Piece piece, Square finalPosition, HashMap<Square, Move> possibleMoves) {
    return this.addPossibleMove(piece, finalPosition, null, possibleMoves, true);
  }

  // the "canCapture" param is ONLY used for pawns.
  // but having a lot of variables will help later surely :pray:
  protected boolean addPossibleMove(
      Piece piece,
      Square finalPosition,
      Piece capturedPiece,
      HashMap<Square, Move> possibleMoves,
      boolean canCapture) {
    if (isInvalidSquare(finalPosition) || containsFriend(piece, finalPosition)) return false;

    if (capturedPiece == null) capturedPiece = Main.getBoard().getPiece(finalPosition);
    if (capturedPiece != null && !canCapture) return false;

    possibleMoves.put(finalPosition, new Move(piece, capturedPiece));

    return true;
  }

  private String getImagePath() {
    return "/pieces/" + this.name().toLowerCase() + ".png";
  }

  private BufferedImage getIcon(boolean white) {
    return ICONS[white ? this.ordinal() : this.ordinal() + PieceTypes.values().length];
  }

  public void draw(Graphics2D g, int size, boolean white) {
    g.drawImage(getIcon(white), 0, 0, size, size, null);
  }
}
