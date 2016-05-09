package com.example.jameskempf.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Button startButton, shopButton, clearButton;
    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Afritubu.ttf");
        TextView coinText = (TextView)findViewById(R.id.coinText);
        TextView highscoreText = (TextView)findViewById(R.id.highscoreText);
        TextView levelText = (TextView)findViewById(R.id.levelText);
        coinText.setTypeface(typeface);
        highscoreText.setTypeface(typeface);
        levelText.setTypeface(typeface);

        startButton = (Button)findViewById(R.id.start);
        shopButton = (Button)findViewById(R.id.shop);
        clearButton = (Button)findViewById(R.id.clear);

        startButton.setTypeface(typeface);
        shopButton.setTypeface(typeface);

        startButton.setOnClickListener(this);
        shopButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

        SeekBar level = (SeekBar)findViewById(R.id.level);
        level.setOnSeekBarChangeListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.start:
                startActivity(new Intent(this, GameActivity.class));
                break;
            case R.id.shop:
                startActivity(new Intent(this, ShopActivity.class));
                break;
            case R.id.clear:
                clearData();
                break;
        }
    }
    private void clearData() {
        deleteFile("data.json");
        getData();
    }
    // Retrieve data
    // If does not exist, intitialize data
    private void getData() {
        String data = readFromFile("data.json");
        if (data == "") {
            System.out.println("DATA NOT FOUND!!!");
            JSONObject dataJSON = new JSONObject();
            try {
                dataJSON.put("highscore", 0);
                dataJSON.put("coins", 0);
                dataJSON.put("jenny", false);
                dataJSON.put("jerry", false);
                dataJSON.put("kevin", false);
                dataJSON.put("selected", "jim");
                dataJSON.put("level", "medium");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            data = dataJSON.toString();
            writeToFile("data.json", data);
        }
        try {
            JSONObject dataJSON = new JSONObject(data);
            TextView hsText = (TextView)findViewById(R.id.highscoreText);
            hsText.setText("highscore: " + dataJSON.getInt("highscore"));
            TextView cText = (TextView)findViewById(R.id.coinText);
            cText.setText("coins: " + dataJSON.getInt("coins"));
            TextView levelText = (TextView)findViewById(R.id.levelText);
            SeekBar level = (SeekBar)findViewById(R.id.level);
            switch(dataJSON.getString("level")) {
                case "easy":
                    levelText.setText("easy");
                    level.setProgress(0);
                    break;
                case "medium":
                    levelText.setText("medium");
                    level.setProgress(1);
                    break;
                case "hard":
                    levelText.setText("hard");
                    level.setProgress(2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // writeToFile and readFromFile from - http://stackoverflow.com/questions/4721626/how-to-get-the-current-context
    private void writeToFile(String fileName, String file) {
        try {
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private String readFromFile(String fileName) {
        String ret = "";
        try {
            InputStream inputStream = openFileInput(fileName);
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
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String data = readFromFile("data.json");
        try {
            JSONObject dataJSON = new JSONObject(data);
            TextView levelText = (TextView)findViewById(R.id.levelText);
            switch(progress) {
                case 0:
                    dataJSON.put("level", "easy");
                    levelText.setText("easy");
                    break;
                case 1:
                    dataJSON.put("level", "medium");
                    levelText.setText("medium");
                    break;
                case 2:
                    dataJSON.put("level", "hard");
                    levelText.setText("hard");
                    break;
            }
            System.out.println(progress);
            writeToFile("data.json", dataJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
