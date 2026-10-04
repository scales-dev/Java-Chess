package com.scales.dev.game;

public record MoveAction(Runnable action, boolean isCapture) {}
