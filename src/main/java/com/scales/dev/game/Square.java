package com.scales.dev.game;

public record Square(int x, int y) {
  public Square add(Square other) {
    return new Square(this.x + other.x, this.y + other.y);
  }

  public Square multiply(int times) {
    return new Square(this.x * times, this.y * times);
  }

  public Square offset(Directions direction) {
    return this.add(direction.vector);
  }

  public Square offset(Directions direction, int times) {
    return this.add(direction.vector.multiply(times));
  }
}
