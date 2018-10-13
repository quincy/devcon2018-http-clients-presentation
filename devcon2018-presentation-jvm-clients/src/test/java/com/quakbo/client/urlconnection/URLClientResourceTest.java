package com.quakbo.client.urlconnection;

import com.quakbo.client.SaveResult;
import com.quakbo.joke.Joke;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.slf4j.Logger;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.once;

public class URLClientResourceTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String jokeJson = "{\"joke\":\"I would tell you a UDP joke, but you might not get it...\"}";
    private static final String jokeText = "I would tell you a UDP joke, but you might not get it...";

    private MockServerClient mockServerClient;

    private Logger fakeLog = mock(Logger.class);
    private Moshi moshi = new Moshi.Builder().build();

    @Test
    public void fetchJoke() throws IOException {
        // Given the joke server is healthy
        HttpRequest request = request()
                .withMethod("GET")
                .withPath("/joke");

        mockServerClient.when(request)
                .respond(response()
                        .withStatusCode(200)
                        .withBody(jokeJson, UTF_8));

        // When I request a joke from the joke server
        URLClientResource client = new URLClientResource(fakeLog, moshi, "localhost", mockServerClient.remoteAddress().getPort());
        Joke joke = client.fetchJoke();

        // Then I receive a funny joke
        assertThat(joke, is(new Joke(jokeText)));

        mockServerClient.verify(request, once());
    }

    @Test
    public void addJoke() {
        // Given the joke server is healthy
        HttpRequest request = request()
                .withMethod("POST")
                .withPath("/joke")
                .withBody(jokeJson);

        mockServerClient.when(request)
                .respond(response()
                        .withStatusCode(201)
                        .withBody("{\"message\":\"Success\",\"id\":1}", UTF_8));

        // When I send a request to add a new joke to the joke server
        URLClientResource client = new URLClientResource(fakeLog, moshi, "localhost", mockServerClient.remoteAddress().getPort());
        SaveResult saveResult = client.addJoke(new Joke(jokeText));

        // Then I receive a response confirming the joke has been saved
        assertThat(saveResult, is(new SaveResult(1, "Success")));

        mockServerClient.verify(request, once());
    }
}