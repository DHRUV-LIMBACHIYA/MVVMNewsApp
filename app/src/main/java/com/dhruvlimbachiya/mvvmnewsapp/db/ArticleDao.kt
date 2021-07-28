package com.dhruvlimbachiya.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dhruvlimbachiya.mvvmnewsapp.model.Article

/**
 * Created by Dhruv Limbachiya on 28-07-2021.
 */

// Interface for communicating with Database.
@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(article: Article)

    @Query("SELECT * FROM articles")
    fun getAllSavedArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}