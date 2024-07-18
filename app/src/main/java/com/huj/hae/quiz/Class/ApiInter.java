package com.huj.hae.quiz.Class;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInter {
    @GET("hujhae71/ids/main/QuizId.json")
    Call<ResponseBody> GetAdsIds();
}
