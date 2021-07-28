package com.dhruvlimbachiya.mvvmnewsapp.ui

import androidx.lifecycle.ViewModel
import com.dhruvlimbachiya.mvvmnewsapp.repository.NewsRepository

/**
 * Created by Dhruv Limbachiya on 28-07-2021.
 */
class NewsViewModel(
    val repository: NewsRepository
) : ViewModel() {
}