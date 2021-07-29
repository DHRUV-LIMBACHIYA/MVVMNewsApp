package com.dhruvlimbachiya.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhruvlimbachiya.mvvmnewsapp.R
import com.dhruvlimbachiya.mvvmnewsapp.adapters.NewsAdapter
import com.dhruvlimbachiya.mvvmnewsapp.ui.NewsActivity
import com.dhruvlimbachiya.mvvmnewsapp.ui.NewsViewModel
import com.dhruvlimbachiya.mvvmnewsapp.utils.Constants
import com.dhruvlimbachiya.mvvmnewsapp.utils.Constants.SEARCH_TIME_DELAY
import com.dhruvlimbachiya.mvvmnewsapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.item_error_message.*
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

        mAdapter.setOnItemClickListener { article ->
            findNavController().navigate(
                SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleFragment(article) // Pass Article from Breaking News Fragment to Article Fragment.
            )
        }

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

        btnRetry.setOnClickListener {
            mViewModel.searchNews(etSearch.text.toString())
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
            addOnScrollListener(scrollListener)
        }
    }

    /**
     * Function responsible for listening live events.
     */
    private fun subscribeToLiveEvents() {
        mViewModel.liveSearchNewsResponse.observe(viewLifecycleOwner) { newsResource ->
            when (newsResource) {
                is Resource.Loading -> showProgressBar()  // On Loading.

                is Resource.Success -> {  // On Success.
                    hideProgressBar()
                    hideErrorMessage()
                    newsResource.data?.let { news ->
                        mAdapter.differ.submitList(news.articles.toList())
                        val totalPages = news.totalResults / Constants.PAGE_SIZE + 2
                        isLastPage = mViewModel.searchNewsPageNumber == totalPages
                        if (isLastPage) {
                            rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Resource.Error -> { // On Error.
                    hideProgressBar()
                    newsResource.message?.let { msg ->
                        showErrorMessage(msg) // Show Error Message.
                        Toast.makeText(
                            requireContext(), "An error occured : $msg",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    /**
     * Hide the error layout.
     */
    private fun hideErrorMessage() {
        searchNewsItemErrorMessage.isVisible = false
        isError = false
    }

    /**
     * Show the error layout with error message.
     */
    private fun showErrorMessage(message: String) {
        searchNewsItemErrorMessage.isVisible = true
        tvErrorMessage.text = message
        isError = true
    }


    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    var isError = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition =
                layoutManager.findFirstVisibleItemPosition() // Position of the first visible view.
            val visibleItemCount = layoutManager.childCount // Number of items currently visible.
            val totalItemCount =
                layoutManager.itemCount // Total numbers of item attached to the Recyclerview.

            val isNoError = !isError
            val isNotLoadingAndNotLastPage =
                !isLoading && !isLastPage // Not loading && not at a last page.
            val isAtLastItem =
                firstVisibleItemPosition + visibleItemCount >= totalItemCount // Check an item is at last item or not.
            val isNotAtBeginning =
                firstVisibleItemPosition >= 0 // Ensuring that first item should not be visible.
            val isTotalMoreThanVisible = totalItemCount >= Constants.PAGE_SIZE

            val shouldPaginate =
                isNoError && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                mViewModel.searchNews(etSearch.text.toString())
                isScrolling = false
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