package com.example.jameskempf.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by jameskempf on 4/19/16.
 * based on https://www.youtube.com/playlist?list=PLWweaDaGRHjvQlpLV0yZDmRKVBdy6rSlg
 */
public class Background {

    private Bitmap image;
    private int x, y;

    public Background(Bitmap res) {
        image = res;
    }

    public void update() {

    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }
}
