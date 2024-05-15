package com.example.incrementalgame.entities;

import com.badlogic.gdx.math.Rectangle;

public class Player {
    private Rectangle bounds;

    public Player(float x, float y, float width, float height) {
        bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void updatePosition(float deltaX) {
        bounds.x += deltaX;
    }
}
