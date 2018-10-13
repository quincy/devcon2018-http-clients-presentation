package com.quakbo.client;

import com.quakbo.client.apache.ApacheClientResource;
import com.quakbo.client.okhttp.OkhttpClientResource;
import com.quakbo.client.retrofit.RetrofitClientResource;
import com.quakbo.client.retrofit.RetrofitJokeService;
import com.quakbo.client.urlconnection.URLClientResource;
import com.quakbo.joke.Joke;
import com.quakbo.json.JsonMapper;
import com.squareup.moshi.Moshi;
import io.javalin.Javalin;
import io.javalin.json.JavalinJson;
import okhttp3.OkHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ClientMain {
    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(ClientMain.class);
        Moshi moshi = new Moshi.Builder().build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        CloseableHttpClient apacheClient = HttpClients.createDefault();

        String server = "localhost";
        int port = 8080;

        Client urlClientResource = new URLClientResource(log, moshi, server, port);
        Client apacheClientResource = new ApacheClientResource(log, moshi, apacheClient);
        Client okhttpClientResource = new OkhttpClientResource(log, moshi, okHttpClient, server, port);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + server + ":" + port)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        RetrofitJokeService retrofitJokeService = retrofit.create(RetrofitJokeService.class);
        Client retrofitClientResource = new RetrofitClientResource(log, retrofitJokeService);

        JsonMapper mapper = new JsonMapper(moshi);
        JavalinJson.setToJsonMapper(mapper);
        JavalinJson.setFromJsonMapper(mapper);

        Javalin app = Javalin.create();
        app.enableCaseSensitiveUrls();
        app.enableDebugLogging();

        addRoutes(app, "/urlConnection", urlClientResource);
        addRoutes(app, "/apache", apacheClientResource);
        addRoutes(app, "/okhttp", okhttpClientResource);
        addRoutes(app, "/retrofit", retrofitClientResource);

        app.start(8081);
    }

    private static void addRoutes(Javalin app, String basePath, Client client) {
        app.get(basePath + "/joke", ctx -> ctx.json(client.fetchJoke()));
        app.post(basePath + "/joke", ctx -> ctx.json(client.addJoke(new Joke(ctx.body()))));
        app.delete(basePath + "/joke/:id", ctx -> ctx.json(client.deleteJoke(Integer.parseInt(ctx.pathParam("id")))));
    }
}
