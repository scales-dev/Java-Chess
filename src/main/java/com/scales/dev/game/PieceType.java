package com.scales.dev.game;

import com.scales.dev.Main;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.imageio.ImageIO;

public enum PieceType {
  KING {
    @Override
    public HashMap<Square, Move> getValidSquares(Board board, Piece piece, boolean verifyingCheck) {
      HashMap<Square, Move> moves = new HashMap<>();

      for (Direction direction : Direction.values()) {
        Square newPosition = piece.position.offset(direction);
        // moving to a position containing check is ILLEGAL
        // although my father never taught me this
        // games often ended by me not realising i was in check.
        if (piece.wouldCheck(newPosition, board, verifyingCheck)) continue;
        addPossibleMove(board, piece, piece.position.offset(direction), moves);
      }

      if (piece.timesMoved > 0 || piece.wouldCheck(piece.position, board, verifyingCheck))
        return moves;

      Square kingPos = piece.position;

      for (int x = 0; x < board.pieces.length; x++) {
        Piece castle = board.pieces[x][kingPos.y()];
        if (castle == null
            || castle.WHITE != piece.WHITE
            || castle.type != CASTLE
            || castle.timesMoved > 0) continue;

        // in case of chess960, because u wouldnt be able
        // to choose between castling and moving 1 square.
        if (Math.abs(x - piece.position.x()) < 2) continue;

        boolean invalidCastle = false;
        int start = Math.min(kingPos.x(), x) + 1;
        int end = Math.max(kingPos.x(), x);

        for (int i = start; i < end; i++) {
          Square square = new Square(i, kingPos.y());
          boolean willKingPass = Math.abs(i - kingPos.x()) <= 2;

          invalidCastle =
              square.isInvalidSquare(board.pieces)
                  || board.containsFriend(piece, square)
                  || (willKingPass && piece.wouldCheck(square, board, verifyingCheck));
          if (invalidCastle) break;
        }

        if (invalidCastle) continue;

        int castleDirection = (int) Math.signum(x - piece.position.x());
        Square newKingSquare = new Square(piece.position.x() + castleDirection * 2, kingPos.y());
        Square newCastleSquare = newKingSquare.add(new Square(castleDirection * -1, 0));
        Runnable moveCastle = () -> Move.movePiece(board, castle, newCastleSquare);

        this.addPossibleMove(board, piece, newKingSquare, moveCastle, moves, false);
      }

      return moves;
    }
  },
  QUEEN {
    @Override
    public HashMap<Square, Move> getValidSquares(Board board, Piece piece, boolean verifyingCheck) {
      HashMap<Square, Move> moves = new HashMap<>();

      for (Direction direction : Direction.values()) {
        this.addDirection(board, piece, direction, moves);
      }

      return moves;
    }
  },
  HORSE {
    @Override
    public HashMap<Square, Move> getValidSquares(Board board, Piece piece, boolean verifyingCheck) {
      HashMap<Square, Move> moves = new HashMap<>();

      for (Direction direction : Direction.values()) {
        if (direction.isDiagonal()) continue;

        // bad name, idk what to name this
        Square up = piece.position.offset(direction);

        // u make 8 L shapes by offsetting 1 in every cardinal direction,
        // then diagonal clockwise/counterclockwise
        this.addPossibleMove(board, piece, up.offset(direction.rotateClockwise()), moves);
        this.addPossibleMove(board, piece, up.offset(direction.rotateCounterClockwise()), moves);
      }

      return moves;
    }
  },
  CASTLE {
    @Override
    public HashMap<Square, Move> getValidSquares(Board board, Piece piece, boolean verifyingCheck) {
      HashMap<Square, Move> moves = new HashMap<>();

      for (Direction direction : Direction.values()) {
        if (direction.isDiagonal()) continue;

        this.addDirection(board, piece, direction, moves);
      }

      return moves;
    }
  },
  BISHOP {
    @Override
    public HashMap<Square, Move> getValidSquares(Board board, Piece piece, boolean verifyingCheck) {
      HashMap<Square, Move> moves = new HashMap<>();

      for (Direction direction : Direction.values()) {
        if (!direction.isDiagonal()) continue;

        this.addDirection(board, piece, direction, moves);
      }

      return moves;
    }
  },
  // honestly the pawn should be handled completely separately, hes so weird
  PAWN {
    @Override
    public HashMap<Square, Move> getValidSquares(Board board, Piece piece, boolean verifyingCheck) {
      HashMap<Square, Move> moves = new HashMap<>();

      Direction direction = Move.getPawnDirection(piece);

      int pawnDistance = piece.timesMoved == 0 ? 2 : 1;
      // pawns move 1 forward!
      // because they blow all their strength on the first move
      for (int i = 1; i <= pawnDistance; i++) {
        this.addPossibleMove(board, piece, piece.position.offset(direction, i), null, moves, false);
      }

      // en passant, when a pawns last move was 2 forward,
      // it can be taken sideways by another pawn
      // after taking, the pawn moves diagonal
      for (Direction d : new Direction[] {Direction.EAST, Direction.WEST}) {
        Square square = piece.position.offset(d);
        if (board.notContainsEnemy(piece, square)) continue;

        Piece enPassantPiece = board.getPiece(square);
        if (board.getLastMove(!piece.WHITE) != enPassantPiece
            || enPassantPiece.timesMoved != 1
            || enPassantPiece.type != PieceType.PAWN
            || enPassantPiece.lastDistance.y() != 2) continue;

        this.addPossibleMove(
            board,
            piece,
            square.offset(direction),
            () -> Move.killPiece(board, enPassantPiece),
            moves,
            true);
      }

      // taking normally
      for (Direction d :
          new Direction[] {direction.rotateClockwise(), direction.rotateCounterClockwise()}) {
        Square square = piece.position.offset(d);
        if (board.notContainsEnemy(piece, square)) continue;
        this.addPossibleMove(board, piece, square, moves);
      }

      return moves;
    }
  };

  private static final BufferedImage[] ICONS = new BufferedImage[PieceType.values().length * 2];

  public static void loadIcons() {
    PieceType[] values = PieceType.values();
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

  private static final RescaleOp INVERT_IMAGE =
      new RescaleOp(new float[] {-1f, -1f, -1f, 1f}, new float[] {255f, 255f, 255f, 0f}, null);

  public HashMap<Square, Move> getValidSquares(Board board, Piece piece) {
    return this.getValidSquares(board, piece, false);
  }

  public abstract HashMap<Square, Move> getValidSquares(
      Board board, Piece piece, boolean verifyingCheck);

  protected boolean addPossibleMove(
      Board board, Piece piece, Square finalPosition, HashMap<Square, Move> possibleMoves) {
    return this.addPossibleMove(board, piece, finalPosition, null, possibleMoves, true);
  }

  // the "canCapture" param is ONLY used for pawns.
  // but having a lot of variables will help later surely :pray:
  protected boolean addPossibleMove(
      Board board,
      Piece piece,
      Square finalPosition,
      Runnable targetAction,
      HashMap<Square, Move> possibleMoves,
      boolean canCapture) {
    if (finalPosition.isInvalidSquare(board.pieces) || board.containsFriend(piece, finalPosition))
      return false;

    Piece capturedPiece = board.getPiece(finalPosition);
    if (capturedPiece != null && !canCapture) return false;

    if (targetAction == null) {
      targetAction = () -> Move.killPiece(board, board.getPiece(finalPosition));
    }

    possibleMoves.put(finalPosition, new Move(piece, new MoveAction(targetAction, canCapture)));

    return true;
  }

  /// adds a direction with INFINITE range, cause queens, horses, and bishops are just built
  /// different
  protected void addDirection(
      Board board, Piece piece, Direction direction, HashMap<Square, Move> moves) {
    Square nextPosition = piece.position;
    while (this.addPossibleMove(
        board, piece, nextPosition = nextPosition.offset(direction), moves)) {
      // we can take 1 piece, but not the piece behind.
      if (board.pieces[nextPosition.x()][nextPosition.y()] != null) break;
    }
  }

  private String getImagePath() {
    return "/pieces/" + this.name().toLowerCase() + ".png";
  }

  private BufferedImage getIcon(boolean white) {
    return ICONS[white ? this.ordinal() : this.ordinal() + PieceType.values().length];
  }

  public void draw(Graphics2D g, int size, boolean white) {
    g.drawImage(getIcon(white), 0, 0, size, size, null);
  }
}