package com.quakbo

import com.quakbo.joke.Joke
import com.quakbo.joke.JokeService
import com.quakbo.json.LocalDateTimeJsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.delete
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post
import org.eclipse.jetty.http.HttpStatus

fun main(args: Array<String>) {
    val jokeService = JokeService()

    val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(LocalDateTimeJsonAdapter())
            .build()

    val app = Javalin.create().apply {
        exception(Exception::class.java) { e, ctx -> ctx.json(e) }
        error(404) { ctx -> ctx.json("not found") }
    }.start(8080)

    app.routes {
        get("/joke") { ctx ->
            ctx.result(jokeService.get().joke)
        }

        post("/joke") { ctx ->
            val joke = moshi.adapter(Joke::class.java).fromJson(ctx.body())
            if (joke == null) {
                ctx.status(HttpStatus.BAD_REQUEST_400).result("Malformed JSON = ${ctx.body()}")
            } else {
                val id = jokeService.save(joke)
                ctx.status(HttpStatus.CREATED_201)
                        .header("Content-Type", "application/json")
                        .result(moshi.adapter(SaveResult::class.javaObjectType).toJson(SaveResult("Success", id)))
            }
        }

        delete("/joke/:id") { ctx ->
            val id = ctx.pathParam("id").toInt()
            jokeService.delete(id)
            ctx.status(HttpStatus.OK_200)
        }
    }
}

data class SaveResult(val message: String, val id: Int)
