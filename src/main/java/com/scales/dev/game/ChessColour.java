package com.scales.dev.game;

import java.awt.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ChessColour {
  WHITE(new Color(238, 238, 210), Direction.SOUTH),
  BLACK(new Color(118, 150, 86), Direction.NORTH);

  public final Color colour;
  public final Direction pawnDirection;

  public static Color getSquareColour(int x, int y) {
    return x % 2 == y % 2 ? WHITE.colour : BLACK.colour;
  }
}
