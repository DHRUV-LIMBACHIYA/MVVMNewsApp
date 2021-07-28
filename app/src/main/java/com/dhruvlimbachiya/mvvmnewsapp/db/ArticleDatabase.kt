package com.dhruvlimbachiya.mvvmnewsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dhruvlimbachiya.mvvmnewsapp.model.Article

/**
 * Created by Dhruv Limbachiya on 28-07-2021.
 */

@Database(
    entities = [Article::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    // Room will implement this function internally.
    abstract fun getArticleDao(): ArticleDao

    companion object {
        @Volatile
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        /**
         * Create database on constructor invocation like ArticleDatabase()
         * If instance != null return instance else createDatabase() & return its instance in thread safe environment.
         */
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        /**
         * Create database using Room.databaseBuilder() method.
         * @return database - will return an instance of ArticleDatabase.
         */
        private fun createDatabase(context: Context): ArticleDatabase =
            Room.databaseBuilder(
                context,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
    }
}