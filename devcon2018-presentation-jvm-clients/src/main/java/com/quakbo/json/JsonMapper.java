package com.quakbo.json;

import com.quakbo.client.DeleteResult;
import com.quakbo.client.SaveResult;
import com.quakbo.joke.Joke;
import com.soywiz.klock.DateTime;
import com.squareup.moshi.Moshi;
import io.javalin.json.FromJsonMapper;
import io.javalin.json.ToJsonMapper;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class JsonMapper implements ToJsonMapper, FromJsonMapper {
    private final Moshi moshi;

    public JsonMapper(Moshi moshi) {
        this.moshi = moshi;
    }

    @NotNull
    @Override
    public String map(@NotNull Object o) {
        if (o instanceof Joke) {
            return moshi.adapter(Joke.class).toJson((Joke) o);
        } else if (o instanceof DateTime) {
            return moshi.adapter(DateTime.class).toJson((DateTime) o);
        } else if (o instanceof SaveResult) {
            return moshi.adapter(SaveResult.class).toJson((SaveResult) o);
        } else if (o instanceof DeleteResult) {
            return moshi.adapter(DeleteResult.class).toJson((DeleteResult) o);
        } else {
            throw new IllegalArgumentException("Crud");
        }
    }

    @Override
    public <T> T map(@NotNull String s, @NotNull Class<T> aClass) {
        try {
            if (aClass.equals(Joke.class)) {
                return aClass.cast(moshi.adapter(Joke.class).fromJson(s));
            } else if (aClass.equals(DateTime.class)) {
                return aClass.cast(moshi.adapter(DateTime.class).fromJson((s)));
            } else if (aClass.equals(SaveResult.class)) {
                return aClass.cast(moshi.adapter(SaveResult.class).fromJson((s)));
            } else if (aClass.equals(DeleteResult.class)) {
                return aClass.cast(moshi.adapter(DeleteResult.class).fromJson((s)));
            } else {
                throw new IllegalArgumentException("Crud");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Crud", e);
        }
    }
}
