package com.quakbo.client.okhttp;

import com.quakbo.client.Client;
import com.quakbo.client.DeleteResult;
import com.quakbo.client.SaveResult;
import com.quakbo.joke.Joke;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;

public class OkhttpClientResource implements Client {
    private static final String APPLICATION_JSON = "application/json";

    private final Logger log;
    private final Moshi moshi;
    private final OkHttpClient httpClient;
    private final String host;

    public OkhttpClientResource(Logger log, Moshi moshi, OkHttpClient httpClient, String server, int port) {
        this.log = log;
        this.moshi = moshi;
        this.httpClient = httpClient;
        this.host = "http://" + server + ":" + port;
    }

    @Override
    public Joke fetchJoke() throws IOException {
        log.info("Received GET /joke");
        Request request = new Request.Builder()
                .url(host + "/joke")
                .build();
        Call call = httpClient.newCall(request);
        try (Response response = call.execute()) {
            return moshi.adapter(Joke.class).fromJson(Objects.requireNonNull(response.body()).source());
        }
    }

    @Override
    public DeleteResult deleteJoke(int id) {
        log.info("Received DELETE /joke/{}", id);
        Request request = new Request.Builder()
                .url(host + "/joke/" + id)
                .delete()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return new DeleteResult(id, "Success");
            }
            throw new IllegalStateException();
        } catch (IllegalStateException | IOException e) {
            return new DeleteResult(id, e.getMessage());
        }
    }

    @Override
    public SaveResult addJoke(Joke joke) {
        log.info("Received POST /joke with body={}", joke);
        RequestBody requestBody = RequestBody.create(MediaType.get(APPLICATION_JSON), moshi.adapter(Joke.class).toJson(joke));
        Request request = new Request.Builder()
                .url(host + "/joke")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return moshi.adapter(SaveResult.class).fromJson(Objects.requireNonNull(response.body()).source());
        } catch (IOException e) {
            return new SaveResult(-1, e.getMessage());
        }
    }
}
