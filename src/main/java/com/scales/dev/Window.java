package com.scales.dev;

import javax.swing.*;

public class Window extends JFrame {
  protected Window() {
    super("Speed Chess");

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(750, 750);
    this.setLocationRelativeTo(null);
    this.setVisible(true);
  }
}
