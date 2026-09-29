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

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);
    this.setVisible(true);

    Canvas canvas = new Canvas();
    canvas.setPreferredSize(new Dimension(Board.RENDER_SIZE, Board.RENDER_SIZE));
    canvas.setIgnoreRepaint(true);

    this.add(canvas, BorderLayout.CENTER);
    this.pack();
    this.setLocationRelativeTo(null);

    canvas.createBufferStrategy(3);
    bs = canvas.getBufferStrategy();

    canvas.addMouseListener(new MouseListener());
  }

  public Graphics2D start() {
    Toolkit.getDefaultToolkit().sync();
    Graphics2D g = (Graphics2D) bs.getDrawGraphics();
    // g.clearRect(0, 0, this.getWidth(), this.getHeight());
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    return g;
  }

  public void finish(Graphics2D g) {
    g.dispose();

    // a rare time when i actually use "do"!!!
    do {
      bs.show();
    } while (bs.contentsRestored());

    Toolkit.getDefaultToolkit().sync();
  }
}
