package com.scales.dev.game;

import com.scales.dev.Main;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

public class Piece {
  private static final RescaleOp INVERT_IMAGE =
      new RescaleOp(new float[] {-1f, -1f, -1f, 1f}, new float[] {255f, 255f, 255f, 0f}, null);

  public final PieceTypes TYPE;
  public final boolean WHITE;

  public final BufferedImage ICON;

  public long lastMoved = -1;
  public int timesMoved = 0;
  public Square lastDistance = new Square(0, 0);

  public Square position;

  public Piece(PieceTypes piece, boolean white, Square position) {
    this.TYPE = piece;
    this.WHITE = white;

    this.ICON = this.getIcon();

    this.position = position;
  }

  private String getImagePath() {
    return "/pieces/" + this.TYPE.name().toLowerCase() + ".png";
  }

  private BufferedImage getIcon() {
    try {
      BufferedImage image =
          ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream(this.getImagePath())));

      return this.WHITE
          ? image
          : INVERT_IMAGE.filter(
              image,
              new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void draw(Graphics2D g, int size) {
    g.drawImage(ICON, 0, 0, size, size, null);
  }
}
