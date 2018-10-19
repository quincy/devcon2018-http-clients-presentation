package com.quakbo.client.urlconnection;

import com.quakbo.client.DeleteResult;
import com.quakbo.client.SaveResult;
import com.quakbo.joke.Joke;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import okio.Okio;
import org.slf4j.Logger;

public class URLClientResource implements com.quakbo.client.Client {
    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final String APPLICATION_JSON = "application/json; charset-UTF-8";

    private final Logger log;
    private final Moshi moshi;
    private final String host;

    public URLClientResource(Logger log, Moshi moshi, String server, int port) {
        this.log = log;
        this.moshi = moshi;
        this.host = "http://" + server + ":" + port;
    }

    @Override
    public Joke fetchJoke() throws IOException {
        log.info("Received GET /joke");

        URLConnection connection = new URL(host + "/joke").openConnection();
        InputStream response = connection.getInputStream();
        return new Joke(Okio.buffer(Okio.source(response)).readString(CHARSET));
    }

    @Override
    public SaveResult addJoke(Joke joke) {
        log.info("Received POST /joke with body={}", joke);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(host + "/joke").openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", APPLICATION_JSON);
            connection.setDoOutput(true);

            String json = moshi.adapter(Joke.class).toJson(joke);

            try (OutputStream output = connection.getOutputStream()) {
                output.write(json.getBytes(CHARSET));
            }

            return moshi.adapter(SaveResult.class).fromJson(Okio.buffer(Okio.source(connection.getInputStream())));
        } catch (IOException e) {
            return new SaveResult(-1, e.getMessage());
        }
    }

    @Override
    public DeleteResult deleteJoke(int id) {
        log.info("Received DELETE /joke/{}", id);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(host + "/joke/" + id).openConnection();
            connection.setRequestMethod("DELETE");
            connection.connect();

            int status = connection.getResponseCode();
            if (status == 200) {
                return new DeleteResult(id, "Success");
            }

            throw new IllegalStateException("Delete request failed with status + " + status);
        } catch (IllegalStateException | IOException e) {
            return new DeleteResult(-1, e.getMessage());
        }
    }
}
