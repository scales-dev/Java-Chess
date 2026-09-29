package com.scales.dev;

import java.awt.*;
import lombok.Getter;

public class Main {
  @Getter private static final Window FRAME = new Window();
  public static boolean running = true;

  static void main() {
    while (running) {
      Graphics2D g = FRAME.start();

      g.clearRect(0, 0, FRAME.getWidth(), FRAME.getHeight());

      long time = System.currentTimeMillis();
      g.fillRect(
          Math.abs((int) (time * 1.2 % (FRAME.getWidth() * 2)) - FRAME.getWidth()),
          Math.abs((int) (time % (FRAME.getHeight() * 2)) - FRAME.getHeight()),
          100,
          100);

      FRAME.finish(g);
    }
  }
}
