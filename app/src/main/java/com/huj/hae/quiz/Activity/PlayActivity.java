package com.huj.hae.quiz.Activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.gson.Gson;
import com.huj.hae.quiz.Class.Constant;
import com.huj.hae.quiz.Model.Data;
import com.huj.hae.quiz.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {

    ImageView sunIcon;
    TextView option1, option2, option3, option4, que_text, screenPoints;
    LinearLayout settingBtn;
    boolean isAnsCorrect = false;
    boolean isOption1Selected = false;
    boolean isOption2Selected = false;
    boolean isOption3Selected = false;
    boolean isOption4Selected = false;
    String selectedOption = "";
    Data data = new Data();
    ProgressBar timeProgress;
    TextView progressTxt;
    int i = 0;
    CountDownTimer mCountDownTimer;
    View[] optionViews;

    boolean isReplay = false;

    private RewardedAd rewardedAd;
    private String AdId = "ca-app-pub-3940256099942544/5224354917";


    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_play);

        new Thread(
                () -> {
                    // Initialize the Google Mobile Ads SDK on a background thread.
                    MobileAds.initialize(this, initializationStatus -> {});

                })
                .start();

        init();

        timeProgress.setMax(Constant.progressMax);

        optionViews = new View[]{option1, option2, option3, option4};

        String string = "#33115473";
        String bgClr = "#B1000000";
        int color = Integer.parseInt(string.replaceFirst("^#", ""), 16);

        settingBtn.setOnClickListener(v -> {

            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }

            isReplay = false;
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);

        });

        option1.setOnClickListener(v -> {

            isOption1Selected = true;
            isOption2Selected = false;
            isOption3Selected = false;
            isOption4Selected = false;

            selectedOption = "A";

            resetUi();


        });

        option2.setOnClickListener(v -> {
            isOption1Selected = false;
            isOption2Selected = true;
            isOption3Selected = false;
            isOption4Selected = false;

            selectedOption = "B";
            resetUi();


        });

        option3.setOnClickListener(v -> {

            isOption1Selected = false;
            isOption2Selected = false;
            isOption3Selected = true;
            isOption4Selected = false;

            selectedOption = "C";
            resetUi();

        });

        option4.setOnClickListener(v -> {

            isOption1Selected = false;
            isOption2Selected = false;
            isOption3Selected = false;
            isOption4Selected = true;

            selectedOption = "D";
            resetUi();

        });

        screenPoints.setText(Constant.points + "");

        sunIcon.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {


            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        }).rotation(180f).start();
    }

    private void countDownTimer() {

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        timeProgress.setProgress(i);
        mCountDownTimer = new CountDownTimer(Constant.timeDuration, Constant.interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                i++;

                timeProgress.setProgress(i);
                progressTxt.setText(i + "");
            }

            @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
            @Override
            public void onFinish() {
                i = 0;
                timeProgress.setProgress(Constant.progressMax);

                if (isOption1Selected || isOption2Selected || isOption3Selected || isOption4Selected) {

                    if (isAnsCorrect) {
                        ShowVictoryDialog();
                    }
                    else {
                        showDefeatDialog();
                    }
                }
                else {
                    showTimeOutDialog();
                }

            }
        };
        mCountDownTimer.start();

    }

    private void showTimeOutDialog() {
        Dialog dialog = new Dialog(PlayActivity.this);
        dialog.setContentView(R.layout.timeout_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.dialogbgclr));
        dialog.setCancelable(true);

        ImageView homeBtn = dialog.findViewById(R.id.homeBtn);
        ImageView replayBtn = dialog.findViewById(R.id.replayBtn);

        if (rewardedAd != null) {
            replayBtn.setVisibility(View.VISIBLE);
        } else {
            replayBtn.setVisibility(View.GONE);
        }

        homeBtn.setOnClickListener(v -> {

            finish();
            dialog.dismiss();

        });

        replayBtn.setOnClickListener(v -> {
            dialog.dismiss();
            showAd("replay");
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                selectedOption = "";
                option1.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                option2.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                option3.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                option4.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));

                recreate();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showDefeatDialog() {
        Dialog dialog = new Dialog(PlayActivity.this);
        dialog.setContentView(R.layout.defeat_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.dialogbgclr));
        dialog.setCancelable(true);

        ImageView homeBtn, replayBtn;

        homeBtn = dialog.findViewById(R.id.homeBtn);
        replayBtn = dialog.findViewById(R.id.replayBtn);

        if (rewardedAd != null) {
            replayBtn.setVisibility(View.VISIBLE);
        } else {
            replayBtn.setVisibility(View.GONE);
        }

        homeBtn.setOnClickListener(v1 -> {
            finish();
            dialog.dismiss();

        });

        replayBtn.setOnClickListener(v1 -> {
            dialog.dismiss();
            showAd("replay");

        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                selectedOption = "";
                option1.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                option2.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                option3.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                option4.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));

                recreate();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void ShowVictoryDialog() {
        Dialog dialog = new Dialog(PlayActivity.this);
        dialog.setContentView(R.layout.victory_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.dialogbgclr));
        dialog.setCancelable(true);

        ImageView nextBtn, sunImg,doubleCoinsBtn;
        TextView correctAnswerPoint = dialog.findViewById(R.id.correctAnswerPoint);
        correctAnswerPoint.setText(Constant.correctAnsPoints + "");

        nextBtn = dialog.findViewById(R.id.nextBtn);
        sunImg = dialog.findViewById(R.id.sunImg);
        doubleCoinsBtn = dialog.findViewById(R.id.doubleCoinsBtn);

        if (rewardedAd != null) {
            doubleCoinsBtn.setVisibility(View.VISIBLE);
        } else {
            doubleCoinsBtn.setVisibility(View.GONE);
        }

        Constant.points = Constant.points + Constant.correctAnsPoints;

        SharedPreferences pref = getApplicationContext().getSharedPreferences("TOTAL_POINTS", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("totalPoints", Constant.points);
        editor.apply();

        animation(sunImg);

        doubleCoinsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showAd("doubleCoin");

            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                selectedOption = "";
                option1.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                option2.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                option3.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                option4.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));

                recreate();
                dialog.dismiss();
            }
        });

        nextBtn.setOnClickListener(v1 -> {

            selectedOption = "";
            option1.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
            option2.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
            option3.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
            option4.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));

            recreate();
            dialog.dismiss();

        });

        dialog.show();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Data loadJson() {

        try {

            InputStream inputStream = getAssets().open("questions.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json;

            int max;
            JSONArray dataArray;
            String dataStr = "", que = "", optionA = "", optionB = "", optionC = "", optionD = "", answer = "";
            json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jObj = new JSONObject(json);
            dataArray = jObj.getJSONArray("Table");

            Random randomData = new Random();
            int randomIndex = randomData.nextInt(dataArray.length());

            dataStr = dataArray.getString(randomIndex);

            System.out.println("dataString : " + dataStr);


            Gson gson = new Gson();
            data = gson.fromJson(dataStr, Data.class);

            isOption1Selected = false;
            isOption2Selected = false;
            isOption3Selected = false;
            isOption4Selected = false;

            System.out.println("######### : " + data);

            resetUi();

        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        return data;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void resetUi() {

        System.out.println("Call resetUI");

        i = 0;

        que_text.setText(data.que);
        option1.setText(data.option.A);
        option2.setText(data.option.B);
        option3.setText(data.option.C);
        option4.setText(data.option.D);

        String answer = data.Ans;

        if (!TextUtils.isEmpty(selectedOption)) {
            if (selectedOption.equals(answer)) {
                setOptionBackground(this, selectedOption, answer, optionViews, true); // Assuming 'this' is your Activity or Context
                isAnsCorrect = true;
            } else {
                setOptionBackground(this, selectedOption, answer, optionViews, false);
                isAnsCorrect = false;
            }

            nextScreen();
        }

    }

    private void setOptionBackground(Context context, String selectedOption, String answer, @NonNull View[] optionViews, boolean isCorrect) {
        int correctBackground = isCorrect ? R.drawable.rightansbg : R.drawable.wrongansbg;
        int defaultBackground = R.drawable.option_bg;

        for (int i = 0; i < optionViews.length; i++) {
            char optionLetter = (char) ('A' + i);
            int background = (optionLetter == selectedOption.charAt(0)) ? correctBackground : defaultBackground;
            optionViews[i].setBackground(ContextCompat.getDrawable(context, background));
        }
    }

    private void nextScreen() {
        if (isOption1Selected || isOption2Selected || isOption3Selected || isOption4Selected) {

            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }


            if (isAnsCorrect) {
                ShowVictoryDialog();
            } else {
                showDefeatDialog();
            }

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onResume() {
        super.onResume();


        loadRewardedVideoAd();

        if (!isReplay) {
            selectedOption = "";
            option1.setBackground(getResources().getDrawable(R.drawable.option_bg, this.getTheme()));
            option3.setBackground(getResources().getDrawable(R.drawable.option_bg, this.getTheme()));
            option2.setBackground(getResources().getDrawable(R.drawable.option_bg, this.getTheme()));
            option4.setBackground(getResources().getDrawable(R.drawable.option_bg, this.getTheme()));

            data = loadJson();

            countDownTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

    }

    private void animation(@NonNull ImageView sunImg) {

        Animation scaleAnimation = new ScaleAnimation(1f, 0.5f, 1f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(Animation.INFINITE);


        Animation alphaAnimation = new AlphaAnimation(1f, 0.5f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setRepeatCount(Animation.INFINITE);


        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);


        sunImg.startAnimation(animationSet);

    }

    private void init() {
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        settingBtn = findViewById(R.id.settingBtn);
        que_text = findViewById(R.id.que_text);
        timeProgress = findViewById(R.id.timeProgress);
        progressTxt = findViewById(R.id.progressTxt);
        sunIcon = findViewById(R.id.sunIcon);
        screenPoints = findViewById(R.id.points);
    }

    public void loadRewardedVideoAd() {
        if (rewardedAd == null) {
            System.out.println("Ads log ******** " + "Ad loading started");
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(this, AdId,
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            System.out.println("Ads log ******** " + loadAdError);
                            rewardedAd = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd ad) {
                            rewardedAd = ad;
                            System.out.println("Ads log ******** " +"Ad was loaded.");
                        }
                    });
        }

    }

    private void showAd(String type) {
        if (rewardedAd != null) {
            rewardedAd.show(PlayActivity.this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    System.out.println("Ads log ********" + "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                }
            });

            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    System.out.println("Ads log ******** " + "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    System.out.println("Ads log ******** " + "Ad dismissed fullscreen content.");
                    rewardedAd = null;

                    if (type.equals("doubleCoin")) {
                        System.out.println("Ads log ******** " + type);
                        Constant.points = Constant.points + Constant.correctAnsPoints;

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("TOTAL_POINTS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt("totalPoints", Constant.points);
                        editor.apply();

                        selectedOption = "";
                        option1.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                        option2.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                        option3.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                        option4.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));

                        recreate();
                    }
                    else if (type.equals("replay")) {
                        isReplay = true;
                        isOption1Selected = false;
                        isOption2Selected = false;
                        isOption3Selected = false;
                        isOption4Selected = false;

                        System.out.println("Ads log ******** " + type);
                        option1.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                        option2.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                        option3.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));
                        option4.setBackground(getResources().getDrawable(R.drawable.option_bg, getTheme()));

                        screenPoints.setText(Constant.points + "");

                        i = 0;
                        countDownTimer();
                    }
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    System.out.println("Ads log ******** " + "Ad failed to show fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    System.out.println("Ads log ******** " + "Ad recorded an impression.");

                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    System.out.println("Ads log ******** " + "Ad showed fullscreen content.");

                }
            });
        }
        else {
            System.out.println("Ads log ******** " + "The rewarded ad wasn't ready yet.");
        }
    }
}

