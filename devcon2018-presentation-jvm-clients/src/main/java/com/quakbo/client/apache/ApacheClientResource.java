package com.quakbo.client.apache;

import com.quakbo.client.Client;
import com.quakbo.client.DeleteResult;
import com.quakbo.client.SaveResult;
import com.quakbo.joke.Joke;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import okio.Okio;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

public class ApacheClientResource implements Client {
    private static final String BASE_URL = "http://localhost:8080";
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private final Logger log;
    private final Moshi moshi;
    private final CloseableHttpClient httpClient;

    public ApacheClientResource(Logger log, Moshi moshi, CloseableHttpClient httpClient) {
        this.log = log;
        this.moshi = moshi;
        this.httpClient = httpClient;
    }

    @Override
    public Joke fetchJoke() throws IOException {
        log.info("Received GET /joke");

        HttpGet httpGet = new HttpGet(BASE_URL + "/joke");

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            Joke joke = new Joke(Okio.buffer(Okio.source(content)).readString(CHARSET));
            EntityUtils.consume(entity);
            return joke;
        }
    }

    @Override
    public DeleteResult deleteJoke(int id) {
        log.info("Received DELETE /joke/{}", id);

        HttpDelete httpDelete = new HttpDelete(BASE_URL + "/joke/" + id);

        try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);

            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                return new DeleteResult(id, "Success");
            }

            throw new IllegalStateException("Request failed with status " + status);
        } catch (IllegalStateException | IOException e) {
            return new DeleteResult(id, e.getMessage());
        }
    }

    @Override
    public SaveResult addJoke(Joke joke) {
        log.info("Received POST /joke with body={}", joke);

        HttpPost httpPost = new HttpPost(BASE_URL + "/joke");
        httpPost.setEntity(new StringEntity(moshi.adapter(Joke.class).toJson(joke), CHARSET));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            SaveResult saveResult = moshi.adapter(SaveResult.class).fromJson(Okio.buffer(Okio.source(entity.getContent())));

            EntityUtils.consume(entity);
            return saveResult;
        } catch (IOException e) {
            return new SaveResult(-1, e.getMessage());
        }
    }
}
