package com.example.jameskempf.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by jameskempf on 4/21/16.
 */
public class Coin extends GameObject {

    private Bitmap image;

    public Coin(Bitmap res, int x, int y, int width, int height, int dy) {
        this.image = res;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dy = dy;
    }
    public void update() {
        y += dy;
    }
    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }
}
