package com.example.jameskempf.game;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jameskempf on 4/18/16.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final int WIDTH = 480;
    public static final int HEIGHT = 850;
    public static final int PLAYER_SPEED = 8;
    public static int TOUCH_X;

    private static final int PLAYER_SIZE = 40;

    private static final int ROCK_SIZE = 40;
    private static final int ROCK_CHANCE = 15;
    private static final int ROCK_MIN_SPEED = 4;
    private static final int ROCK_MAX_SPEED = 12;

    private static final int COIN_SIZE = 40;
    private static final int COIN_CHANCE = 3;
    private static final int COIN_SPEED = 6;

    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<Rock> rocks = new ArrayList();
    private ArrayList<Coin> coins = new ArrayList();

    public GamePanel(Context context) {

        super(context);

        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.whitebg));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.player),
                PLAYER_SIZE, PLAYER_SIZE);

        thread.setRunning(true);
        thread.start();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!player.getPlaying()) {
                startGame();
            }
            else {
                final float scaleFactorX = WIDTH/(getWidth() * 1.f);
                TOUCH_X = (int)(event.getX() * scaleFactorX);
                if (TOUCH_X < WIDTH/2) {
                    player.setLeft();
                }
                else if (TOUCH_X > WIDTH/2) {
                    player.setRight();
                }
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            player.setStop();
            TOUCH_X = WIDTH/2;
            return true;
        }
        return super.onTouchEvent(event);
    }
    public void update() {

        if (player.getPlaying()) {

            // Spawn rocks
            Random r = new Random();
            if (r.nextInt((100 - 0) + 1) + 0 < ROCK_CHANCE) {
                rocks.add(new Rock(BitmapFactory.decodeResource(getResources(), R.drawable.rock),
                        r.nextInt((WIDTH - ROCK_SIZE - 0) + 1) + 0, 0 - ROCK_SIZE,
                        ROCK_SIZE, ROCK_SIZE,
                        r.nextInt((ROCK_MAX_SPEED - ROCK_MIN_SPEED) + 1) + ROCK_MIN_SPEED));
            }
            // Sawn coins
            if (r.nextInt((100 - 0) + 1) + 0 < COIN_CHANCE) {
                coins.add(new Coin(BitmapFactory.decodeResource(getResources(), R.drawable.coin),
                        r.nextInt((WIDTH - COIN_SIZE - 0) + 1) + 0, 0 - COIN_SIZE,
                        COIN_SIZE, COIN_SIZE,
                        COIN_SPEED));
            }

            // Update Objects
            for (int i = 0; i < rocks.size(); i ++) {
                Rock rock = rocks.get(i);
                if (player.getRectangle().intersect(rock.getRectangle())) {
                    endGame();
                }
                else if (rock.getY() > HEIGHT) {
                    rocks.remove(i);
                    player.addScore(1);
                }
                else {
                    rock.update();
                }
            }
            for (int i = 0; i < coins.size(); i ++) {
                Coin coin = coins.get(i);
                if (player.getRectangle().intersect(coin.getRectangle())) {
                    player.addScore(10);
                    player.addCoin();
                    coins.remove(i);
                }
                else if (coin.getY() > HEIGHT) {
                    coins.remove(i);
                }
                else {
                    coin.update();
                }
            }
            bg.update();
            player.update();
        }
    }
    @Override
    public void draw(Canvas canvas) {
        final float scaleFactorX = getWidth()/(WIDTH * 1.f);
        final float scaleFactorY = getHeight()/(HEIGHT * 1.f);
        if (canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);

            bg.draw(canvas);
            player.draw(canvas);
            for (int i = 0; i < rocks.size(); i++) {
                rocks.get(i).draw(canvas);
            }
            for (int i = 0; i < coins.size(); i++) {
                coins.get(i).draw(canvas);
            }

            String scoreString= "Score: ";
            scoreString += player.getScore();
            Paint scorePaint = new Paint();
            scorePaint.setTextSize(30);
            canvas.drawText(scoreString, 10, 40, scorePaint);

            String coinString = "Coins: ";
            coinString += player.getCoinCount();
            Paint coinPaint = new Paint();
            coinPaint.setTextSize(30);
            canvas.drawText(coinString, 10, 80, coinPaint);

            if (!player.getPlaying()) {
                Paint startPaint = new Paint();
                String s = "Touch to Start";
                startPaint.setTextSize(50);
                int xPos = (int) ((WIDTH/2) - ((startPaint.descent() + startPaint.ascent())/2)) ;
                int yPos = (int) ((HEIGHT/2) - ((startPaint.descent() +    startPaint.ascent())/2)) ;
                canvas.drawText(s, 100, yPos, startPaint);
            }

            canvas.restoreToCount(savedState);
        }
    }
    private void endGame() {
        player.setPlaying(false);
    }
    private void startGame() {
        player.reset();
        rocks.clear();
        coins.clear();
        player.setPlaying(true);
    }
}
