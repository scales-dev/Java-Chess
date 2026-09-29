package com.scales.dev;

import com.scales.dev.game.Board;
import com.scales.dev.window.Window;
import lombok.Getter;

public class Main {
  @Getter private static final Window frame = new Window();
  @Getter private static final Board board = new Board();

  static void main() {
    Main.getBoard().draw();
  }
}
