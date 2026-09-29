package com.scales.dev;

import java.awt.*;

public class Board {
  public static final Color WHITE = new Color(238, 238, 210);
  public static final Color BLACK = new Color(118, 150, 86);
  private static final int RENDER_SIZE = 512;
  private static final int BOARD_SIZE = 8;
  private static final int SQUARE_SIZE = RENDER_SIZE / BOARD_SIZE;

  public final Piece[][] pieces = new Piece[BOARD_SIZE][BOARD_SIZE];

  public void draw() {
    Window frame = Main.getFrame();
    Graphics2D g = frame.start();

    int offsetX = (frame.getWidth() / 2) - (RENDER_SIZE / 2);
    int offsetY = (frame.getHeight() / 2) - (RENDER_SIZE / 2);

    g.translate(offsetX, offsetY);

    for (int x = 0; x < BOARD_SIZE; x++) {
      for (int y = 0; y < BOARD_SIZE; y++) {
        g.setColor(x % 2 == y % 2 ? WHITE : BLACK);
        g.fillRect(x * SQUARE_SIZE, y * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
      }
    }

    frame.finish(g);
  }
}
