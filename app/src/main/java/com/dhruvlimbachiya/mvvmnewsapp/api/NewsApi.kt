package com.dhruvlimbachiya.mvvmnewsapp.api

import com.dhruvlimbachiya.mvvmnewsapp.model.NewsResponse
import com.dhruvlimbachiya.mvvmnewsapp.utils.Constants.API_KEY
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Dhruv Limbachiya on 27-07-2021.
 */
interface NewsApi {

    // Breaking News Endpoint
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "in",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    // Search Everything Endpoint.
    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    // Search Everything Endpoint.
    @GET("v2/everything")
    fun searchNewsUsingRx(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Observable<NewsResponse>
}