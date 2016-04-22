package com.example.jameskempf.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by jameskempf on 4/19/16.
 */
public class Rock extends GameObject {

    private Bitmap image;

    public Rock(Bitmap res, int x, int y, int width, int height, int dy) {
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
