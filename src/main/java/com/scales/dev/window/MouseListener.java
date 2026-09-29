package com.scales.dev.window;

import com.scales.dev.Main;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseListener extends MouseAdapter {
  @Override
  public void mousePressed(MouseEvent e) {
    Main.getBoard().draw();
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    Main.getBoard().draw();
  }
}
