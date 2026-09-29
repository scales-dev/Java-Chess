package com.scales.dev;

import java.awt.*;
import java.awt.image.BufferStrategy;
import javax.swing.*;

// I personally wish that all JFrames were spawned like this, sadly they arent.
public class Window extends JFrame {
  public BufferStrategy bs;

  protected Window() {
    super("Speed Chess");

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(750, 750);
    this.setLocationRelativeTo(null);
    this.setVisible(true);

    this.createBufferStrategy(3);
    bs = this.getBufferStrategy();

    this.addMouseListener(new MouseListener());
  }

  public Graphics2D start() {
    Graphics2D g = (Graphics2D) bs.getDrawGraphics();
    g.clearRect(0, 0, this.getWidth(), this.getHeight());
    return g;
  }

  public void finish(Graphics2D g) {
    g.dispose();
    bs.show();
    Toolkit.getDefaultToolkit().sync();
  }
}
