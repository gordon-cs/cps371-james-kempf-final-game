package com.example.jameskempf.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jameskempf on 4/18/16.
 * based on https://www.youtube.com/playlist?list=PLWweaDaGRHjvQlpLV0yZDmRKVBdy6rSlg
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final int WIDTH = 500;
    public static final int HEIGHT = 850;
    public static final int PLAYER_SPEED = 8;
    public static int TOUCH_X;

    private static final int PLAYER_SIZE = 33;

    private static final int ROCK_SIZE = 40;
    private int ROCK_CHANCE = 10;
    private static final int ROCK_CHANCE_EASY = 6;
    private static final int ROCK_CHANCE_MEDIUM = 10;
    private static final int ROCK_CHANCE_HARD = 14;
    private static final int ROCK_MIN_SPEED = 6;
    private static final int ROCK_MAX_SPEED = 12;

    private static final int COIN_SIZE = 40;
    private static final int COIN_CHANCE = 3;
    private static final int COIN_SPEED = 6;

    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<Rock> rocks = new ArrayList();
    private ArrayList<Coin> coins = new ArrayList();
    private ArrayList<Bitmap> rockImages = new ArrayList();
    int rockIndex = 0;
    private int currentHighscore;
    private int currentCoins;
    private Boolean firstPlay = true;
    private JSONObject dataJSON;

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
        // Save data
        try {
            dataJSON.put("firstPlay", true);
            writeToFile("data.json", dataJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Stop thread
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
        // Retrieve saved data
        try {
            dataJSON = new JSONObject(readFromFile("data.json"));
            currentHighscore = dataJSON.getInt("highscore");
            currentCoins = dataJSON.getInt("coins");
            // Set character
            Bitmap playerImage = BitmapFactory.decodeResource(getResources(), R.drawable.jim);
            switch (dataJSON.getString("selected")) {
                case "jenny":
                    playerImage = BitmapFactory.decodeResource(getResources(), R.drawable.jenny);
                    break;
                case "jerry":
                    playerImage = BitmapFactory.decodeResource(getResources(), R.drawable.jerry);
                    break;
                case "kevin":
                    playerImage = BitmapFactory.decodeResource(getResources(), R.drawable.kevin);
                    break;
            }
            // Set difficulty
            player = new Player(playerImage, PLAYER_SIZE, PLAYER_SIZE);
            switch(dataJSON.getString("level")) {
                case "easy":
                    ROCK_CHANCE = ROCK_CHANCE_EASY;
                    System.out.println("EASY");
                    break;
                case "medium":
                    ROCK_CHANCE = ROCK_CHANCE_MEDIUM;
                    System.out.println("MEDIUM");
                    break;
                case "hard":
                    ROCK_CHANCE = ROCK_CHANCE_HARD;
                    System.out.println("HARD");
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Set rocks images
        rockImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.branch_1));
        rockImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.branch_2));
        rockImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.branch_3));
        rockImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.branch_4));
        rockImages.add(BitmapFactory.decodeResource(getResources(), R.drawable.rock));
        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background));
        // Start main thread
        thread.setRunning(true);
        thread.start();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Move player
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!player.getPlaying()) {
                startGame();
            }
            else {
                final float scaleFactorX = WIDTH/(getWidth() * 1.f);
                TOUCH_X = (int)(event.getX() * scaleFactorX);
                // Move left
                if (TOUCH_X < WIDTH/2) {
                    player.setLeft();
                }
                // Move right
                else if (TOUCH_X > WIDTH/2) {
                    player.setRight();
                }
            }
            return true;
        }
        // Stop moving
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
                Bitmap rockImage = rockImages.get(rockIndex);
                if (++ rockIndex >= rockImages.size()) {
                    rockIndex = 0;
                }
                rocks.add(new Rock(rockImage,
                        r.nextInt((WIDTH - ROCK_SIZE - 0) + 1) + 0, 0 - ROCK_SIZE,
                        ROCK_SIZE, ROCK_SIZE,
                        r.nextInt((ROCK_MAX_SPEED - ROCK_MIN_SPEED) + 1) + ROCK_MIN_SPEED));
            }
            // Spawn coins
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
            // Update coins
            // Check for collisions
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
            for (int i = 0; i < rocks.size(); i++) {
                rocks.get(i).draw(canvas);
            }
            for (int i = 0; i < coins.size(); i++) {
                coins.get(i).draw(canvas);
            }

            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#463c2c"));
            Typeface typeface = Typeface.createFromAsset(this.getContext().getAssets(), "Afritubu.ttf");
            paint.setTypeface(typeface);
            paint.setTextSize(28);

            String scoreString= "score: ";
            scoreString += player.getScore();
            canvas.drawText(scoreString, 10, 30, paint);

            String coinString = "coins: ";
            coinString += player.getCoinCount();
            canvas.drawText(coinString, 10, 60, paint);

            String highscoreString = "highscore: ";
            highscoreString += Integer.toString(currentHighscore);
            canvas.drawText(highscoreString, 10, 90, paint);

            if (!player.getPlaying()) {
                if (firstPlay) {
                    System.out.println("FIRST TIME");
                    Bitmap tutorialImage = BitmapFactory.decodeResource(getResources(), R.drawable.arrows);
                    canvas.drawBitmap(tutorialImage, 0, 0, null);
                } else {
                    Paint startPaint = new Paint();
                    startPaint.setColor(Color.parseColor("#baff6b"));
                    startPaint.setTypeface(typeface);
                    String s = "tap to start";
                    startPaint.setTextSize(65);
                    int yPos = (int) ((HEIGHT / 2) - ((startPaint.descent() + startPaint.ascent()) / 2));
                    canvas.drawText(s, 110, yPos, startPaint);
                }
            }
            player.draw(canvas);

            canvas.restoreToCount(savedState);
        }
    }
    // Ends the game
    // Saves data to json object
    private void endGame() {
        try {
            if (player.getScore() > currentHighscore) {
                currentHighscore = player.getScore();
                dataJSON.put("highscore", currentHighscore);
            }
            if (player.getCoinCount() > 0) {
                currentCoins += player.getCoinCount();
                dataJSON.put("coins", currentCoins);
            }
            writeToFile("data.json", dataJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        player.setPlaying(false);
    }
    // Start new game
    // Resets everything
    private void startGame() {
        player.reset();
        rocks.clear();
        coins.clear();
        player.setPlaying(true);
        if (firstPlay) {
            firstPlay = false;
        }
    }
    // writeToFile and readFromFile from - http://stackoverflow.com/questions/4721626/how-to-get-the-current-context
    private void writeToFile(String fileName, String file) {
        try {
            FileOutputStream fos = this.getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private String readFromFile(String fileName) {
        String ret = "";
        try {
            InputStream inputStream = this.getContext().openFileInput(fileName);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }
}
