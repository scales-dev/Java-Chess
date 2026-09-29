package com.scales.dev.game;

import com.scales.dev.Main;
import com.scales.dev.window.Window;
import java.awt.*;
import lombok.Setter;

public class Board {
  public static final int BOARD_SIZE = 8;
  public static final int RENDER_SIZE = 512;
  private static final Color WHITE_SQUARE = new Color(238, 238, 210, 50);
  private static final Color BLACK_SQUARE = new Color(118, 150, 86, 50);
  public final Piece[][] pieces = new Piece[BOARD_SIZE][BOARD_SIZE];
  @Setter private Piece selectedPiece;
  private static final Color SELECTED_SQUARE = new Color(186, 202, 68, 50);
  private static final int SQUARE_SIZE = RENDER_SIZE / BOARD_SIZE;

  private static final String DEFAULT_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

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

        pieces[x++][y] = new Piece(type, white);
      }
    }
  }

  public void draw() {
    Graphics2D g = Main.getFrame().start();

    Rectangle boardRectangle = getBoardRectangle();
    g.translate(boardRectangle.x, boardRectangle.y);

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

    Main.getFrame().finish(g);
  }

  private Color getSquareColor(int x, int y, Piece piece) {
    // i dont like stacking ternary operators
    if (piece != null && piece == selectedPiece) return SELECTED_SQUARE;
    return x % 2 == y % 2 ? WHITE_SQUARE : BLACK_SQUARE;
  }

  public Rectangle getBoardRectangle() {
    Window frame = Main.getFrame();
    return new Rectangle(
        frame.getInsets().left + 1, frame.getInsets().top, RENDER_SIZE, RENDER_SIZE);
  }
}