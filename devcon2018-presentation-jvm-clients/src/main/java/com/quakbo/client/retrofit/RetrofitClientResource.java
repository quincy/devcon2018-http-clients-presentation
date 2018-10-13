package com.quakbo.client.retrofit;

import com.quakbo.client.Client;
import com.quakbo.client.DeleteResult;
import com.quakbo.client.SaveResult;
import com.quakbo.joke.Joke;
import java.io.IOException;
import org.slf4j.Logger;
import retrofit2.Response;

public class RetrofitClientResource implements Client {
    private final Logger log;
    private final RetrofitJokeService jokeService;

    public RetrofitClientResource(Logger log, RetrofitJokeService jokeService) {
        this.log = log;
        this.jokeService = jokeService;
    }

    @Override
    public Joke fetchJoke() throws IOException {
        log.info("Received GET /joke");
        Response<Joke> response = jokeService.fetchJoke().execute();
        return response.body();
    }

    @Override
    public DeleteResult deleteJoke(int id) {
        log.info("Received DELETE /joke/{}", id);

        try {
            jokeService.deleteJoke(id).execute();
        } catch (IOException e) {
            return new DeleteResult(id, e.getMessage());
        }

        return new DeleteResult(id, "Success");
    }

    @Override
    public SaveResult addJoke(Joke joke) {
        log.info("Received POST /joke with body={}", joke);

        Response<SaveResult> response;
        try {
            response = jokeService.addJoke(joke).execute();
        } catch (IOException e) {
            return new SaveResult(-1, e.getMessage());
        }

        return response.body();
    }
}
