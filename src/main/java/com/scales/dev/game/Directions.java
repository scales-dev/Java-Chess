package com.scales.dev.game;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Directions {
  NORTH(new Square(0, 1)),
  NORTH_EAST(new Square(1, 1)),
  EAST(new Square(1, 0)),
  SOUTH_EAST(new Square(1, -1)),
  SOUTH(new Square(0, -1)),
  SOUTH_WEST(new Square(-1, -1)),
  WEST(new Square(-1, 0)),
  NORTH_WEST(new Square(-1, 1));

  public final Square vector;
}
