package com.example.jameskempf.game;

import android.graphics.Rect;

/**
 * Created by jameskempf on 4/19/16.
 */
public abstract class GameObject {
    protected int x, y;
    protected int dx, dy;
    protected int width, height;

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public void setDX(int dx) {
        this.dx = dx;
    }
    public void setDY(int dy) {
        this.dy = dy;
    }
    public Rect getRectangle() {
        return new Rect(x, y, x + width, y + height);
    }
}
