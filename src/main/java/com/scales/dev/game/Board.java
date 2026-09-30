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

        PieceTypes type =
            switch (Character.toLowerCase(c)) {
              case 'k' -> PieceTypes.King;
              case 'q' -> PieceTypes.Queen;
              case 'r' -> PieceTypes.Castle;
              case 'b' -> PieceTypes.Bishop;
              case 'n' -> PieceTypes.Horse;
              case 'p' -> PieceTypes.Pawn;
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
          piece.draw(g, SQUARE_SIZE);
        }
        g.translate(0, SQUARE_SIZE);
      }
      g.translate(SQUARE_SIZE, -SQUARE_SIZE * BOARD_SIZE);
    }

    if (selectedPiece != null) {
      g.setTransform(defaultTransform);
      for (Square square :
          selectedPiece.TYPE.getValidSquares(selectedPiece).keySet().toArray(new Square[0])) {
        g.setColor(new Color(22, 22, 22, 100));
        g.fillRoundRect(
            square.x() * SQUARE_SIZE + VALID_MOVE_OFFSET,
            square.y() * SQUARE_SIZE + VALID_MOVE_OFFSET,
            VALID_MOVES_SIZE,
            VALID_MOVES_SIZE,
            VALID_MOVES_SIZE,
            VALID_MOVES_SIZE);
      }
    }

    frame.finish(g);
  }

  public Piece getPiece(Square square) {
    return pieces[square.x()][square.y()];
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