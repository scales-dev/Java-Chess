package com.scales.dev.game;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Team {
  public ChessColour colour;
  public Piece lastMovedPiece;
  public Piece king;

  public Team(ChessColour colour, Piece[][] pieces) {
    this.colour = colour;

    for (Piece[] piecesX : pieces) {
      for (Piece piece : piecesX) {
        if (piece == null || piece.colour != colour || piece.type != PieceType.KING) continue;

        this.king = piece;
        return;
      }
    }
  }
}
