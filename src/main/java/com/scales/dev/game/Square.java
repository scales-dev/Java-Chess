package com.scales.dev.game;

public record Square(int x, int y) {
  public Square add(Square other) {
    return new Square(this.x + other.x, this.y + other.y);
  }

  public Square difference(Square other) {
    return new Square(Math.abs(this.x - other.x), Math.abs(this.y - other.y));
  }

  public Square multiply(int times) {
    return new Square(this.x * times, this.y * times);
  }

  public Square offset(Direction direction) {
    return this.add(direction.vector);
  }

  public Square offset(Direction direction, int times) {
    return this.add(direction.vector.multiply(times));
  }

  public boolean isInvalidSquare(Piece[][] pieces) {
    return this.x() < 0
        || this.x() >= pieces.length
        || this.y() < 0
        || this.y() >= pieces[0].length;
  }
}
