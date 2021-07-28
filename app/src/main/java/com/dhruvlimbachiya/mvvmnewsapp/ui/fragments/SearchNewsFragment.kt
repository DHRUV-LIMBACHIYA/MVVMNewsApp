package com.dhruvlimbachiya.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhruvlimbachiya.mvvmnewsapp.R
import com.dhruvlimbachiya.mvvmnewsapp.adapters.NewsAdapter
import com.dhruvlimbachiya.mvvmnewsapp.ui.NewsActivity
import com.dhruvlimbachiya.mvvmnewsapp.ui.NewsViewModel
import com.dhruvlimbachiya.mvvmnewsapp.utils.Constants.SEARCH_TIME_DELAY
import com.dhruvlimbachiya.mvvmnewsapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Dhruv Limbachiya on 27-07-2021.
 */
class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    lateinit var mViewModel: NewsViewModel
    private lateinit var mAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewModel = (activity as NewsActivity).mViewModel

        setUpRecyclerView()
        subscribeToLiveEvents()

        /**
         *  RxJava implementation for search feature.
         */
        // mViewModel.searchQueryUsingRxJava(rxSearchObservable(etSearch))

        /**
         *  Use Coroutine for search feature.
         */
        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel() // Cancel the job if it already exists.
            job = MainScope().launch {
                delay(SEARCH_TIME_DELAY) // Make a delay to give user some time to input his query properly.
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        mViewModel.searchNews(editable.toString()) // Search the news based on the editText input.
                    }
                }
            }
        }
    }

    /**
     * Set up the recyclerview
     */
    private fun setUpRecyclerView() {
        mAdapter = NewsAdapter()
        rvSearchNews.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * Function responsible for listening live events.
     */
    private fun subscribeToLiveEvents() {
        mViewModel.searchNewsResponse.observe(viewLifecycleOwner) { newsResource ->
            when (newsResource) {
                is Resource.Loading -> showProgressBar()  // On Loading.

                is Resource.Success -> {  // On Success.
                    hideProgressBar()
                    newsResource.data?.let { news ->
                        mAdapter.differ.submitList(news.articles)
                    }
                }

                is Resource.Error -> { // On Error.
                    hideProgressBar()
                    Log.e(TAG, "An error occured : ${newsResource.message}")
                }
            }
        }
    }

    /**
     * Hide the progressbar
     */
    private fun hideProgressBar() {
        paginationProgressBar.isVisible = false
    }

    /**
     * Show the progressbar
     */
    private fun showProgressBar() {
        paginationProgressBar.isVisible = true
    }

    companion object {
        private const val TAG = "SearchNewsNewsFragment"
    }
}