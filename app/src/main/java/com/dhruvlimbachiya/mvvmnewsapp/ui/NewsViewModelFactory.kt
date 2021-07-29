package com.dhruvlimbachiya.mvvmnewsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dhruvlimbachiya.mvvmnewsapp.NewsApplication
import com.dhruvlimbachiya.mvvmnewsapp.repository.NewsRepository
import java.lang.IllegalArgumentException

/**
 * Created by Dhruv Limbachiya on 28-07-2021.
 */

class NewsViewModelFactory(
    private val application: Application,
    private val repository: NewsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(NewsViewModel::class.java)){
            NewsViewModel(application,repository) as  T
        }else{
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}