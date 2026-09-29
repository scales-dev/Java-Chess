package com.scales.dev;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

public enum Piece {
  King,
  Queen,
  Horse,
  Castle,
  Bishop,
  Pawn;

  private final RescaleOp INVERT_IMAGE =
      new RescaleOp(new float[] {-1f, -1f, -1f, 1f}, new float[] {255f, 255f, 255f, 0f}, null);

  public final BufferedImage image = getIcon();

  public void draw(Graphics2D g, int size) {
    g.drawImage(image, 0, 0, size, size, null);
  }

  private String getImagePath() {
    return "/pieces/" + this.name().toLowerCase() + ".png";
  }

  private BufferedImage getIcon() {
    try {
      BufferedImage image =
          ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream(this.getImagePath())));
      return INVERT_IMAGE.filter(
          image,
          new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
