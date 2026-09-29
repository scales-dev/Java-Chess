package com.scales.dev.window;

import com.scales.dev.Main;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;

public class ComponentListener extends ComponentAdapter {
  private final Timer resizeTimer = new Timer(50, _ -> Main.getBoard().draw());

  @Override
  public void componentResized(ComponentEvent e) {
    if (resizeTimer.isRunning()) {
      resizeTimer.restart();
      return;
    }
    resizeTimer.start();
  }
}
