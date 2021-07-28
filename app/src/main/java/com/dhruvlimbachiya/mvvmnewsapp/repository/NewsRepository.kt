package com.dhruvlimbachiya.mvvmnewsapp.repository

import com.dhruvlimbachiya.mvvmnewsapp.api.NewsRetrofitInstance
import com.dhruvlimbachiya.mvvmnewsapp.db.ArticleDatabase
import com.dhruvlimbachiya.mvvmnewsapp.model.NewsResponse
import retrofit2.Response

/**
 * Created by Dhruv Limbachiya on 28-07-2021.
 */
class NewsRepository(
    val articleDatabase: ArticleDatabase
) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse> {
        return NewsRetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
    }

    suspend fun searchQuery(searchQuery: String, pageNumber: Int): Response<NewsResponse> {
        return NewsRetrofitInstance.api.searchNews(searchQuery, pageNumber)
    }

}