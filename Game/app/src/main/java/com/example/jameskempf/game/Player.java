package com.example.jameskempf.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by jameskempf on 4/19/16.
 */
public class Player extends GameObject {
    private Bitmap image;
    private int score;
    private int coinCount;
    private boolean playing;
    private long startTime;

    public Player(Bitmap res, int width, int height) {
        image = res;
        x = GamePanel.WIDTH/2 - width/2;
        y = GamePanel.HEIGHT - 100;
        dx = 0;
        score = 0;
        this.height = height;
        this.width = width;
    }
    public void setLeft() {
        dx = -1;
    }
    public void setRight() {
        dx = 1;
    }
    public void setStop() {
        dx = 0;
    }
    public void addScore(int i) {
        score += i;
    }
    public int getScore() {
        return score;
    }
    public void addCoin() {
        coinCount ++;
    }
    public int getCoinCount() {
        return coinCount;
    }
    public void reset() {
        score = 0;
        x = GamePanel.WIDTH/2 - width/2;
        dx = 0;
    };
    public boolean getPlaying() {
        return playing;
    }
    public void setPlaying(boolean b) {
        playing = b;
    }
    public void update() {
        // Move player
        if (dx != 0) {
            x += GamePanel.PLAYER_SPEED * dx;
            if (x < 0) {
                x = 0;
            }
            else if (x > GamePanel.WIDTH - width) {
                x = GamePanel.WIDTH - width;
            }
        }
    }
    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }
}
