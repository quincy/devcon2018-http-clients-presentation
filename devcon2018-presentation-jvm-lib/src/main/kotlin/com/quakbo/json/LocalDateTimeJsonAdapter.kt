package com.quakbo.json

import com.soywiz.klock.DateTime
import com.soywiz.klock.SimplerDateFormat
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

val ISO_DATE_TIME = SimplerDateFormat("YYYY-MM-dd'T'HH:mm:ss'zzz'")

class LocalDateTimeJsonAdapter {
    @ToJson
    fun toJson(dateTime: DateTime) = ISO_DATE_TIME.format(dateTime)

    @FromJson
    fun fromJson(s: String): DateTime = ISO_DATE_TIME.parseDate(s)
}