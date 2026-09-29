package com.scales.dev.window;

import com.scales.dev.game.Board;
import java.awt.*;
import java.awt.image.BufferStrategy;
import javax.swing.*;

// I personally wish that all JFrames were spawned like this, sadly they arent.
public class Window extends JFrame {
  private final BufferStrategy bs;

  public Window() {
    super("Speed Chess");

    this.pack();
    Insets insets = this.getInsets();

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);
    this.setBackground(Color.BLACK);
    this.setSize(
        Board.RENDER_SIZE + insets.left + insets.right,
        Board.RENDER_SIZE + insets.top + insets.bottom);
    this.setLocationRelativeTo(null);
    this.setVisible(true);

    this.createBufferStrategy(3);
    bs = this.getBufferStrategy();

    this.addMouseListener(new MouseListener());
    this.addComponentListener(new ComponentListener());
  }

  public Graphics2D start() {
    Graphics2D g = (Graphics2D) bs.getDrawGraphics();
    // g.clearRect(0, 0, this.getWidth(), this.getHeight());
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    return g;
  }

  public void finish(Graphics2D g) {
    g.dispose();
    bs.show();
    Toolkit.getDefaultToolkit().sync();
  }
}
