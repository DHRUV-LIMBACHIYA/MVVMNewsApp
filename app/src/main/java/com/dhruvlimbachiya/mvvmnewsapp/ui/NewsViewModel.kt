package com.dhruvlimbachiya.mvvmnewsapp.ui

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruvlimbachiya.mvvmnewsapp.api.NewsRetrofitInstance
import com.dhruvlimbachiya.mvvmnewsapp.model.NewsResponse
import com.dhruvlimbachiya.mvvmnewsapp.repository.NewsRepository
import com.dhruvlimbachiya.mvvmnewsapp.utils.Constants
import com.dhruvlimbachiya.mvvmnewsapp.utils.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.concurrent.TimeUnit

/**
 * Created by Dhruv Limbachiya on 28-07-2021.
 */
class NewsViewModel(
    val repository: NewsRepository
) : ViewModel() {

    // Breaking News
    private val _breakingNewsResponse = MutableLiveData<Resource<NewsResponse>>()
    val breakingNewsResponse: LiveData<Resource<NewsResponse>> = _breakingNewsResponse
    private var breakingNewsPageNumber = 1

    // Search News
    private val _searchNewsResponse = MutableLiveData<Resource<NewsResponse>>()
    val searchNewsResponse: LiveData<Resource<NewsResponse>> = _searchNewsResponse
    private var searchNewsPageNumber = 1

    var query: Observable<String>? = null

    init {
        getAllBreakingNews("in")
    }

    /**
     * Function for getting all the breaking news
     * @param countryCode = 2 Letter country code.
     */
    private fun getAllBreakingNews(countryCode: String) {
        viewModelScope.launch {
            _breakingNewsResponse.postValue(Resource.Loading()) // Status = Loading.
            val response = repository.getBreakingNews(countryCode, breakingNewsPageNumber)
            _breakingNewsResponse.postValue(handleBreakingNewsResponse(response))
        }
    }


    /**
     * Function for executing api call based on search query.
     * @param searchQuery = query to search.
     */
    fun searchNews(searchQuery: String) {
        viewModelScope.launch {
            _searchNewsResponse.postValue(Resource.Loading()) // Status = Loading.
            val response = repository.searchQuery(searchQuery, breakingNewsPageNumber)
            _searchNewsResponse.postValue(handleSearchNewsResponse(response))
        }
    }

    /**
     * Handle the Breaking News api response and update status accordingly.
     * @return resource - wrapped news response
     */
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                return Resource.Success(responseBody)
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
                return Resource.Success(responseBody)
            }
        }
        return Resource.Error(response.message())
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