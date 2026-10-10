package com.scales.dev.game;

import com.scales.dev.Main;
import com.scales.dev.window.Window;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Board {
  public static final int BOARD_SIZE = 8;
  public static final int RENDER_SIZE = 512;
  private static final int SQUARE_SIZE = RENDER_SIZE / BOARD_SIZE;
  private static final int VALID_MOVES_SIZE = (int) (SQUARE_SIZE / 2.5f);
  private static final int VALID_MOVE_OFFSET = (SQUARE_SIZE / 2) - (VALID_MOVES_SIZE / 2);

  private static final Color WHITE_SQUARE = new Color(238, 238, 210);
  private static final Color BLACK_SQUARE = new Color(118, 150, 86);
  private static final Color SELECTED_SQUARE = new Color(186, 202, 68);
  private static final Color CHECKED_SQUARE_TINT = new Color(255, 50, 50, 170);

  private static final Color VALID_SQUARE_COLOUR = new Color(22, 22, 22, 100);
  private static final int takesIndicatorThickness = 3;

  public final Piece[][] pieces = new Piece[BOARD_SIZE][BOARD_SIZE];
  public Piece selectedPiece;

  private static final String DEFAULT_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

  private final Piece[] lastMovePerTeam = new Piece[2];

  public Piece getLastMove(boolean white) {
    return lastMovePerTeam[white ? 0 : 1];
  }

  public void setLastMove(Piece piece) {
    lastMovePerTeam[piece.WHITE ? 0 : 1] = piece;
  }

  public Board() {
    loadFenNotation(DEFAULT_POSITION);
  }

  // https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
  public void loadFenNotation(String position) {
    String[] rows = position.split("/");

    for (int y = 0; y < BOARD_SIZE; y++) {
      int x = 0;

      for (char c : rows[y].toCharArray()) {
        if (Character.isDigit(c)) {
          x += Character.digit(c, 10);
          continue;
        }

        boolean white = Character.isUpperCase(c);

        PieceType type =
            switch (Character.toLowerCase(c)) {
              case 'k' -> PieceType.KING;
              case 'q' -> PieceType.QUEEN;
              case 'r' -> PieceType.CASTLE;
              case 'b' -> PieceType.BISHOP;
              case 'n' -> PieceType.HORSE;
              case 'p' -> PieceType.PAWN;
              default -> throw new RuntimeException("Invalid piece: " + c);
            };

        pieces[x][y] = new Piece(type, white, new Square(x, y));
        x++;
      }
    }
  }

  public void draw() {
    Window frame = Main.getFrame();
    Graphics2D g = frame.start();

    Rectangle boardRectangle = getBoardRectangle();
    g.translate(boardRectangle.x, boardRectangle.y);

    AffineTransform defaultTransform = g.getTransform();

    for (int x = 0; x < BOARD_SIZE; x++) {
      for (int y = 0; y < BOARD_SIZE; y++) {
        Piece piece = pieces[x][y];

        g.setColor(getSquareColor(x, y, piece));
        g.fillRect(0, 0, SQUARE_SIZE, SQUARE_SIZE);

        if (piece != null) {
          if (!piece.equals(selectedPiece)
              && piece.type == PieceType.KING
              && piece.isChecked(new Square(x, y), this)) {
            g.setColor(CHECKED_SQUARE_TINT);
            g.fillRect(0, 0, SQUARE_SIZE, SQUARE_SIZE);
          }
          piece.draw(g, SQUARE_SIZE);
        }
        g.translate(0, SQUARE_SIZE);
      }
      g.translate(SQUARE_SIZE, -SQUARE_SIZE * BOARD_SIZE);
    }

    if (selectedPiece != null) {
      g.setTransform(defaultTransform);
      for (Square square :
          selectedPiece.type.getValidSquares(this, selectedPiece).keySet().toArray(new Square[0])) {
        boolean hasEnemy = getPiece(square) != null;
        g.setColor(VALID_SQUARE_COLOUR);

        if (hasEnemy) {
          g.setStroke(new BasicStroke(takesIndicatorThickness * 2));
          g.drawRoundRect(
              square.x() * SQUARE_SIZE + takesIndicatorThickness,
              square.y() * SQUARE_SIZE + takesIndicatorThickness,
              SQUARE_SIZE - (takesIndicatorThickness * 2),
              SQUARE_SIZE - (takesIndicatorThickness * 2),
              SQUARE_SIZE,
              SQUARE_SIZE);
        } else {
          g.fillRoundRect(
              square.x() * SQUARE_SIZE + VALID_MOVE_OFFSET,
              square.y() * SQUARE_SIZE + VALID_MOVE_OFFSET,
              VALID_MOVES_SIZE,
              VALID_MOVES_SIZE,
              VALID_MOVES_SIZE,
              VALID_MOVES_SIZE);
        }
      }
    }

    frame.finish(g);
  }

  public Piece getPiece(Square square) {
    return pieces[square.x()][square.y()];
  }

  public boolean containsFriend(Piece piece, Square position) {
    if (position.isInvalidSquare(this.pieces)) return false;
    Piece pieceAtPosition = this.pieces[position.x()][position.y()];

    // pawn moves pass "piece" as null,
    // because they cant take while walking, cause they weak n scrawny
    return pieceAtPosition != null && (piece == null || piece.WHITE == pieceAtPosition.WHITE);
  }

  public boolean notContainsEnemy(Piece piece, Square position) {
    if (position.isInvalidSquare(this.pieces)) return true;
    Piece pieceAtPosition = this.pieces[position.x()][position.y()];

    return pieceAtPosition == null || piece.WHITE == pieceAtPosition.WHITE;
  }

  private Color getSquareColor(int x, int y, Piece piece) {
    // i dont like stacking ternary operators
    if (piece != null && piece == selectedPiece) return SELECTED_SQUARE;
    return x % 2 == y % 2 ? WHITE_SQUARE : BLACK_SQUARE;
  }

  public Rectangle getBoardRectangle() {
    return new Rectangle(0, 0, RENDER_SIZE, RENDER_SIZE);
  }
}