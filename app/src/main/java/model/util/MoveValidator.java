package model.util;

import model.Vector2d;

public interface MoveValidator {
    boolean canMoveTo(Vector2d position);
}