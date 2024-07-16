package com.huj.hae.quiz.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.huj.hae.quiz.Class.Constant;
import com.huj.hae.quiz.R;

public class MainActivity extends AppCompatActivity {

    RelativeLayout start_btn;
    TextView privacy_policy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        init();

        SharedPreferences preferences = getSharedPreferences("TOTAL_POINTS", MODE_PRIVATE);
        int totalPoints = preferences.getInt("totalPoints", 0);

        Constant.points = totalPoints;

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                startActivity(intent);
            }
        });

        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void init() {
        start_btn = findViewById(R.id.start_btn);
        privacy_policy = findViewById(R.id.privacy_policy);
    }
}