package com.dhruvlimbachiya.mvvmnewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.text.TextUtils
import androidx.lifecycle.*
import com.dhruvlimbachiya.mvvmnewsapp.NewsApplication
import com.dhruvlimbachiya.mvvmnewsapp.api.NewsRetrofitInstance
import com.dhruvlimbachiya.mvvmnewsapp.model.Article
import com.dhruvlimbachiya.mvvmnewsapp.model.NewsResponse
import com.dhruvlimbachiya.mvvmnewsapp.repository.NewsRepository
import com.dhruvlimbachiya.mvvmnewsapp.utils.Constants
import com.dhruvlimbachiya.mvvmnewsapp.utils.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by Dhruv Limbachiya on 28-07-2021.
 */
class NewsViewModel(
    application: Application,
    private val repository: NewsRepository
) : AndroidViewModel(application) {

    // Breaking News
    private val _breakingNewsResponse = MutableLiveData<Resource<NewsResponse>>()
    val liveBreakingNewsResponse: LiveData<Resource<NewsResponse>> = _breakingNewsResponse
    var breakingNewsPageNumber = 1
    var breakingNewsResponse: NewsResponse? = null

    // Search News
    private val _searchNewsResponse = MutableLiveData<Resource<NewsResponse>>()
    val liveSearchNewsResponse: LiveData<Resource<NewsResponse>> = _searchNewsResponse
    var searchNewsPageNumber = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getAllBreakingNews("in")
    }

    /**
     * Function for getting all the breaking news
     * @param countryCode = 2 Letter country code.
     */
    fun getAllBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }


    /**
     * Function for executing api call based on search query.
     * @param searchQuery = query to search.
     */
    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    /**
     * Get the articles which are saved by user.
     */
    fun getSavedArticles() = repository.getSavedArticles()

    /**
     * Insert or update the article in Room DB.
     */
    fun insertOrUpdateArticle(article: Article) = viewModelScope.launch {
        repository.insertOrUpdate(article)
    }

    /**
     * Delete a particular article in Room DB.
     */
    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article)
    }

    /**
     * Handle the Breaking News api response and update status accordingly.
     * @return resource - wrapped news response
     */
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                breakingNewsPageNumber++ // Increment the page number.
                if(breakingNewsResponse == null){
                    breakingNewsResponse = responseBody
                }else{
                    val newArticles = responseBody.articles
                    breakingNewsResponse?.articles?.addAll(newArticles) // Append new articles to the old articles list.
                }
                return Resource.Success(breakingNewsResponse ?: responseBody)
            }
        }
        return Resource.Error(response.message())
    }

    /**
     * Handle the Search News api response and update status accordingly.
     * @return resource - wrapped news response
     */
    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                searchNewsPageNumber++ // Increment page number
                if(searchNewsResponse == null){
                    searchNewsResponse = responseBody
                }else{
                    val newArticles = responseBody.articles
                    searchNewsResponse?.articles?.addAll(newArticles) // Append new articles to the old articles list.
                }
                return Resource.Success(searchNewsResponse ?: responseBody)
            }
        }
        return Resource.Error(response.message())
    }

    /**
     * Make a safe breaking news call by checking various errors.
     */
    private suspend fun safeBreakingNewsCall(countryCode: String){
        _breakingNewsResponse.postValue(Resource.Loading()) // Status = Loading.
        try {
            if(hasInternetConnection()){
                val response = repository.getBreakingNews(countryCode, breakingNewsPageNumber)
                _breakingNewsResponse.postValue(handleBreakingNewsResponse(response))
            }else {
                _breakingNewsResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException ->  _breakingNewsResponse.postValue(Resource.Error("Network Failure"))
                else ->  _breakingNewsResponse.postValue(Resource.Error("Unknown Error Occured : ${t.message}"))
            }
        }
    }

    /**
     * Make a safe search new call by checking various errors.
     */
    private suspend fun safeSearchNewsCall(searchQuery: String){
        _searchNewsResponse.postValue(Resource.Loading()) // Status = Loading.
        try {
            if(hasInternetConnection()){
                val response = repository.searchQuery(searchQuery, searchNewsPageNumber)
                _searchNewsResponse.postValue(handleSearchNewsResponse(response))
            }else {
                _searchNewsResponse.postValue(Resource.Error("No internet connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException ->  _searchNewsResponse.postValue(Resource.Error("Network Failure"))
                else ->  _searchNewsResponse.postValue(Resource.Error("Unknown Error Occured : ${t.message}"))
            }
        }
    }

    /**
     * Checks the internet is available or not.
      */
    private fun hasInternetConnection(): Boolean{

        val connectivityManager = getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    /**
     * Search news based on query.[RxJava Implementation]
     */
    fun searchQueryUsingRxJava(rxSearchObservable: Observable<String>) {
        rxSearchObservable.let {
            _searchNewsResponse.postValue(Resource.Loading())
            it.debounce(Constants.SEARCH_TIME_DELAY, TimeUnit.MILLISECONDS)
                .filter { query ->
                    return@filter !TextUtils.isEmpty(query)
                }
                .distinctUntilChanged()
                .switchMap { query ->
                    return@switchMap NewsRetrofitInstance.api.searchNewsUsingRx(
                        query,
                        searchNewsPageNumber
                    )
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    response?.let { news ->
                        _searchNewsResponse.postValue(Resource.Success(news))
                    }
                }, { throwable ->
                    _searchNewsResponse.postValue(Resource.Error(throwable.message))
                })
        }
    }
}