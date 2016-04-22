package com.example.jameskempf.game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button startButton, shopButton;
    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rl = (RelativeLayout)findViewById(R.id.relativeLayout);

        startButton = (Button)findViewById(R.id.start);
        shopButton = (Button)findViewById(R.id.shop);

        startButton.setOnClickListener(this);
        shopButton.setOnClickListener(this);
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
        }
        Log.v("END SWITCH", "asd");
    }
}
