package com.quakbo.client.retrofit;

import com.quakbo.client.SaveResult;
import com.quakbo.joke.Joke;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitJokeService {
    @GET("/joke")
    Call<String> fetchJoke();

    @DELETE("/joke/{id}")
    Call<Void> deleteJoke(@Path("id") int id);

    @POST("/joke")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Call<SaveResult> addJoke(@Body Joke joke);
}
