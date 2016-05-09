package com.example.jameskempf.game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShopActivity extends Activity implements View.OnClickListener {

    private JSONObject dataJSON;
    private final String selectedColor = "#baff6b";
    private final String deselectedColor = "#463c2c";
    private final int jennyCost = 1;
    private final int jerryCost = 2;
    private final int kevinCost = 3;
    private TextView
        jimText,
        jennyText,
        jerryText,
        kevinText,
        coinText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_shop);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Afritubu.ttf");

        jimText = (TextView)findViewById(R.id.jim);
        jennyText = (TextView)findViewById(R.id.jenny);
        jerryText = (TextView)findViewById(R.id.jerry);
        kevinText = (TextView)findViewById(R.id.kevin);
        coinText = (TextView)findViewById(R.id.coinText);
        TextView item1d = (TextView)findViewById(R.id.jimDescript);
        TextView item2d = (TextView)findViewById(R.id.jennyDescript);
        TextView item3d = (TextView)findViewById(R.id.jerryDescript);
        TextView item4d = (TextView)findViewById(R.id.kevinDescript);

        jimText.setTypeface(typeface);
        jennyText.setTypeface(typeface);
        jerryText.setTypeface(typeface);
        kevinText.setTypeface(typeface);
        coinText.setTypeface(typeface);
        item1d.setTypeface(typeface);
        item2d.setTypeface(typeface);
        item3d.setTypeface(typeface);
        item4d.setTypeface(typeface);

        jimText.setOnClickListener(this);
        jennyText.setOnClickListener(this);
        jerryText.setOnClickListener(this);
        kevinText.setOnClickListener(this);

        try {
            dataJSON = new JSONObject(readFromFile("data.json"));

            if (!dataJSON.getBoolean("jenny"))
                jennyText.setText("JUNGLE JENNY - " + jennyCost + " COINS");
            if (!dataJSON.getBoolean("jerry"))
                jerryText.setText("JUNGLE JERRY - " + jerryCost + " COINS");
            if (!dataJSON.getBoolean("kevin"))
                kevinText.setText("KEVIN - " + kevinCost + " COINS");

            coinText.setText("coins: " + dataJSON.getInt("coins"));

            setColor(dataJSON.getString("selected"), selectedColor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.jim:
                System.out.println("JIM");
                selectCharacter("jim");
                break;
            case R.id.jenny:
                System.out.println("JENNY");
                System.out.println("jenny");
                if (buyCharacter("jenny"))
                    selectCharacter("jenny");
                break;
            case R.id.jerry:
                System.out.println("JERRY");
                if (buyCharacter("jerry"))
                    selectCharacter("jerry");
                break;
            case R.id.kevin:
                System.out.println("KEVIN");
                if (buyCharacter("kevin"))
                    selectCharacter("kevin");
                break;
        }
        writeToFile("data.json", dataJSON.toString());
    }
    // Sets color of character text to specified color
    private void setColor(String character, String colorString) {
        int color = Color.parseColor(colorString);
        switch(character) {
            case "jim":
                System.out.println("ITEM1");
                jimText.setTextColor(color);
                break;
            case "jenny":
                System.out.println("ITEM2");
                jennyText.setTextColor(color);
                break;
            case "jerry":
                System.out.println("ITEM3");
                jerryText.setTextColor(color);
                break;
            case "kevin":
                System.out.println("ITEM4");
                kevinText.setTextColor(color);
                break;
        }
    }
    // Set chosen character to specified character
    private void selectCharacter(String character) {
        try {
            switch(dataJSON.getString("selected")) {
                case "jim":
                    setColor("jim", deselectedColor);
                    break;
                case "jenny":
                    setColor("jenny", deselectedColor);
                    break;
                case "jerry":
                    setColor("jerry", deselectedColor);
                    break;
                case "kevin":
                    setColor("kevin", deselectedColor);
                    break;
            }
            setColor(character, selectedColor);
            dataJSON.put("selected", character);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // Returns true if able to buy, false if not
    private Boolean buyCharacter(String character) {
        Boolean ableToBuy = false;
        try {
            if (dataJSON.getBoolean(character))
                return true;
            switch (character) {
                case "jenny":
                    if (dataJSON.getInt("coins") >= jennyCost) {
                        ableToBuy = true;
                        dataJSON.put("jenny", true);
                        jennyText.setText("JUNGLE JENNY");
                        dataJSON.put("coins", dataJSON.getInt("coins") - jennyCost);
                    }
                    break;
                case "jerry":
                    if (dataJSON.getInt("coins") >= jerryCost) {
                        ableToBuy = true;
                        dataJSON.put("coins", dataJSON.getInt("coins") - jerryCost);
                        dataJSON.put("jerry", true);
                        jerryText.setText("JUNGLE JERRY");
                    }
                    break;
                case "kevin":
                    if (dataJSON.getInt("coins") >= kevinCost) {
                        ableToBuy = true;
                        dataJSON.put("coins", dataJSON.getInt("coins") - kevinCost);
                        dataJSON.put("kevin", true);
                        kevinText.setText("KEVIN");
                    }
                    break;
            }
            coinText.setText("coins: " + dataJSON.getInt("coins"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(ableToBuy);
        return ableToBuy;
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
}
