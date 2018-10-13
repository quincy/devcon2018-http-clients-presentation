package com.quakbo.joke

class JokeService(private var jokeList: MutableList<Joke>) {
    private val jokeMap = mutableMapOf<Int, Joke>()

    init {
        jokeList.asSequence()
                .withIndex()
                .map { Pair(it.index, it.value) }
                .toMap(jokeMap)
    }

    var next = 0

    constructor() : this(jokes)

    fun get(): Joke {
        val joke = jokeList[next]
        next = if (next == jokeList.size - 1) 0 else next + 1
        return joke
    }

    fun save(joke: Joke): Int {
        val id = jokeMap.size
        jokeMap[jokeMap.size] = joke
        return id
    }

    fun delete(id: Int) {
        jokeMap.remove(id)
    }
}

private val jokes = mutableListOf(
        Joke("I would tell you a UDP joke, but you might not get it..."),
        Joke("""Why aren't number jokes funny in Octal?
            |
            |Because 7 10 11!""".trimMargin()),
        Joke("""What do you call 8 hobbits?
            |
            |A hobbyte!""".trimMargin()),
        Joke("The two hardest problems in computer science are cache invalidation, naming things and off by one errors.")
)