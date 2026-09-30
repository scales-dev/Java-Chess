package com.scales.dev.game;

import java.awt.*;

public class Piece {

  public PieceTypes type;
  public final boolean WHITE;

  public long lastMoved = -1;
  public int timesMoved = 0;
  public Square lastDistance = new Square(0, 0);

  public Square position;

  public Piece(PieceTypes piece, boolean white, Square position) {
    this.type = piece;
    this.WHITE = white;

    this.position = position;
  }

  public void draw(Graphics2D g, int size) {
    type.draw(g, size, WHITE);
  }
}
