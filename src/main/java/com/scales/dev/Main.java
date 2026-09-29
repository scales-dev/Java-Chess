package com.scales.dev;

import lombok.Getter;

public class Main {
  @Getter private static final Window frame = new Window();
  @Getter private static final Board board = new Board();

  static void main() {
    board.draw();
  }
}
