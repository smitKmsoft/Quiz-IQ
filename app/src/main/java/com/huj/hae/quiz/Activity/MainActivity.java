package com.huj.hae.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.huj.hae.quiz.Class.ApiClient;
import com.huj.hae.quiz.Class.ApiInter;
import com.huj.hae.quiz.Class.Constant;
import com.huj.hae.quiz.R;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsfeed.com/live/5bca077f-220f-4b46-9129-19d1d0a8521b"));
                startActivity(browserIntent);
            }
        });

        getAdsValue();

    }

    private void init() {
        start_btn = findViewById(R.id.start_btn);
        privacy_policy = findViewById(R.id.privacy_policy);
    }

    public void getAdsValue() {

        ApiInter apiService = ApiClient.getRetrofit().create(ApiInter.class);
        final Call<ResponseBody> call = apiService.GetAdsIds();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String res = "";
                try {

                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        res = responseBody.string();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (res.equals("")) {
                    return;
                }

                try {
                    System.out.println("getIds ===============> ");
                    JSONObject jsonObject = new JSONObject(res);
                    Constant.adsEnable = jsonObject.getBoolean("adsEnable");
                    Constant.RewardAdsId = jsonObject.getString("rewardAdId");
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    Constant.adsEnable = false;
                    Constant.RewardAdsId = "ca-app-pub-3940256099942544/5224354917";
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println(t.getMessage());
                Constant.adsEnable = false;
                Constant.RewardAdsId = "ca-app-pub-3940256099942544/5224354917";
            }
        });

    }
}