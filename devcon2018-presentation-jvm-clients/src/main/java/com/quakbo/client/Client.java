package com.quakbo.client;

import com.quakbo.joke.Joke;
import java.io.IOException;

public interface Client {
    Joke fetchJoke() throws IOException;

    SaveResult addJoke(Joke joke) throws IOException;

    DeleteResult deleteJoke(int id) throws IOException;
}
