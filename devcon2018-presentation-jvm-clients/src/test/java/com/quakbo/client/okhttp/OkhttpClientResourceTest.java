package com.quakbo.client.okhttp;

import com.quakbo.client.SaveResult;
import com.quakbo.joke.Joke;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.nio.charset.Charset;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class OkhttpClientResourceTest {
    private static MockWebServer mockWebServer = new MockWebServer();
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String jokeJson = "{\"joke\":\"I would tell you a UDP joke, but you might not get it...\"}";
    private static final String jokeText = "I would tell you a UDP joke, but you might not get it...";

    private Logger fakeLog = mock(Logger.class);
    private Moshi moshi = new Moshi.Builder().build();

    @BeforeClass
    public static void setUp() throws IOException {
        mockWebServer.start();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void fetchJoke() throws IOException, InterruptedException {
        // Given the joke server is healthy
        mockWebServer.enqueue(new MockResponse().setBody(jokeJson));

        // When I request a joke from the joke server
        OkHttpClient okhttpClient = new OkHttpClient.Builder().build();
        OkhttpClientResource client = new OkhttpClientResource(fakeLog, moshi, okhttpClient, "localhost", mockWebServer.getPort());
        Joke joke = client.fetchJoke();

        // Then I receive a funny joke
        assertThat(joke, is(new Joke(jokeText)));

        RecordedRequest actualRequest = mockWebServer.takeRequest();
        assertThat(actualRequest.getMethod(), is("GET"));
        assertThat(actualRequest.getPath(), is("/joke"));
    }

    @Test
    public void addJoke() throws InterruptedException {
        // Given the joke server is healthy
        mockWebServer.enqueue(new MockResponse().setBody("{\"message\":\"Success\",\"id\":1}"));

        // When I send a request to add a new joke to the joke server
        OkHttpClient okhttpClient = new OkHttpClient.Builder().build();
        OkhttpClientResource client = new OkhttpClientResource(fakeLog, moshi, okhttpClient, "localhost", mockWebServer.getPort());
        SaveResult saveResult = client.addJoke(new Joke(jokeText));

        // Then I receive a response confirming the joke has been saved
        assertThat(saveResult, is(new SaveResult(1, "Success")));

        RecordedRequest actualRequest = mockWebServer.takeRequest();
        assertThat(actualRequest.getMethod(), is("POST"));
        assertThat(actualRequest.getPath(), is("/joke"));
        assertThat(actualRequest.getBody().readString(UTF_8), is(jokeJson));
    }
}