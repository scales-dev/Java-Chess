package com.scales.dev;

import com.scales.dev.game.Board;
import com.scales.dev.game.PieceType;
import com.scales.dev.window.Window;
import lombok.Getter;

public class Main {
  @Getter private static final Window frame = new Window();
  @Getter private static final Board board = new Board();

  // did you know that you dont need the space between String[] and args?
  // you can also make it String args[] or String...args, or even put nothing in Java 25!
  public static void main(String[] args) {
    PieceType.loadIcons();

    Main.getBoard().draw();
  }
}
