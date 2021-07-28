package com.dhruvlimbachiya.mvvmnewsapp.db

import androidx.room.TypeConverter
import com.dhruvlimbachiya.mvvmnewsapp.model.Source

/**
 * Created by Dhruv Limbachiya on 28-07-2021.
 */
class Converters {

    /**
     * Convert the source into string
     */
    @TypeConverter
    fun fromSourceToString(source: Source): String = source.name

    /**
     * Convert the string into source object.
     */
    @TypeConverter
    fun fromStringToSource(name: String) = Source(name,name)

}