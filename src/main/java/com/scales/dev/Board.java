package com.scales.dev;

import java.awt.*;

public class Board {
  public static final Color WHITE = new Color(238, 238, 210);
  public static final Color BLACK = new Color(118, 150, 86);
  private static final int RENDER_SIZE = 512;
  private static final int BOARD_SIZE = 8;
  private static final int SQUARE_SIZE = RENDER_SIZE / BOARD_SIZE;

  public final Piece[][] pieces = new Piece[BOARD_SIZE][BOARD_SIZE];

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
    Window frame = Main.getFrame();
    Graphics2D g = frame.start();

    int offsetX = (frame.getWidth() / 2) - (RENDER_SIZE / 2);
    int offsetY = (frame.getHeight() / 2) - (RENDER_SIZE / 2);

    g.translate(offsetX, offsetY);

    for (int x = 0; x < BOARD_SIZE; x++) {
      for (int y = 0; y < BOARD_SIZE; y++) {
        g.setColor(x % 2 == y % 2 ? WHITE : BLACK);
        g.fillRect(0, 0, SQUARE_SIZE, SQUARE_SIZE);

        Piece piece = pieces[x][y];
        if (piece != null) {
          piece.draw(g, SQUARE_SIZE);
        }
        g.translate(0, SQUARE_SIZE);
      }
      g.translate(SQUARE_SIZE, -SQUARE_SIZE * BOARD_SIZE);
    }

    frame.finish(g);
  }
}