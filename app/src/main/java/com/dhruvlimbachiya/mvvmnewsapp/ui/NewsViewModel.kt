package com.dhruvlimbachiya.mvvmnewsapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruvlimbachiya.mvvmnewsapp.model.NewsResponse
import com.dhruvlimbachiya.mvvmnewsapp.repository.NewsRepository
import com.dhruvlimbachiya.mvvmnewsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Created by Dhruv Limbachiya on 28-07-2021.
 */
class NewsViewModel(
    val repository: NewsRepository
) : ViewModel() {

    private val _breakingNewsResponse = MutableLiveData<Resource<NewsResponse>>()
    val breakingNewsResponse: LiveData<Resource<NewsResponse>> = _breakingNewsResponse

    private var breakingNewsPageNumber = 1

    init {
        getAllBreakingNews("in")
    }

    /**
     * Function for getting all the breaking news
     * @param countryCode = 2 Letter country code.
     */
    fun getAllBreakingNews(countryCode: String) {
        viewModelScope.launch {
            _breakingNewsResponse.postValue(Resource.Loading()) // Status = Loading.
            val response = repository.getBreakingNews(countryCode,breakingNewsPageNumber)
            _breakingNewsResponse.postValue(handleBreakingNewsResponse(response))
        }
    }

    /**
     * Handle the Breaking News api response and update status accordingly.
     * @return resource - wrapped news response
     */
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if(response.isSuccessful && response.body() != null){
            response.body()?.let { responseBody ->
                return  Resource.Success(responseBody)
            }
        }
        return Resource.Error(response.message())
    }
}