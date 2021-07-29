package com.dhruvlimbachiya.mvvmnewsapp.utils

import android.os.Build
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * Created by Dhruv Limbachiya on 29-07-2021.
 */

fun convertTimestampToDate(timeStamp: String): String? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val inputFormatter: ZonedDateTime = ZonedDateTime.parse(timeStamp)
        val outputFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm a", Locale.getDefault())
        return outputFormatter.format(inputFormatter)
    }
    return timeStamp
}

