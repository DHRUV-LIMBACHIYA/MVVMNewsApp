package com.dhruvlimbachiya.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhruvlimbachiya.mvvmnewsapp.R
import com.dhruvlimbachiya.mvvmnewsapp.adapters.NewsAdapter
import com.dhruvlimbachiya.mvvmnewsapp.ui.NewsActivity
import com.dhruvlimbachiya.mvvmnewsapp.ui.NewsViewModel
import com.dhruvlimbachiya.mvvmnewsapp.utils.Constants.PAGE_SIZE
import com.dhruvlimbachiya.mvvmnewsapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.item_error_message.*

/**
 * Created by Dhruv Limbachiya on 27-07-2021.
 */
class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var mViewModel: NewsViewModel
    lateinit var mAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewModel = (activity as NewsActivity).mViewModel
        setUpRecyclerView()
        subscribeToLiveEvents()

        mAdapter.setOnItemClickListener { article ->
            findNavController().navigate(
                BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(article) // Pass Article from Breaking News Fragment to Article Fragment.
            )
        }

        btnRetry.setOnClickListener {
            mViewModel.getAllBreakingNews("in")
        }
    }

    /**
     * Set up the recyclerview
     */
    private fun setUpRecyclerView() {
        mAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(scrollListener)
        }
    }

    /**
     * Function responsible for listening live events.
     */
    private fun subscribeToLiveEvents() {
        mViewModel.liveBreakingNewsResponse.observe(viewLifecycleOwner) { newsResource ->
            when (newsResource) {
                is Resource.Loading -> showProgressBar()  // On Loading.

                is Resource.Success -> {  // On Success.
                    hideProgressBar()
                    hideErrorMessage()
                    newsResource.data?.let { news ->
                        mAdapter.differ.submitList(news.articles.toList())
                        val totalPages = news.totalResults / PAGE_SIZE + 2
                        isLastPage = mViewModel.breakingNewsPageNumber == totalPages
                        if(isLastPage){
                            rvBreakingNews.setPadding(0,0,0,0)
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
        itemErrorMessage.isVisible = false
        isError = false
    }

    /**
     * Show the error layout with error message.
     */
    private fun showErrorMessage(message: String) {
        itemErrorMessage.isVisible = true
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
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition() // Position of the first visible view.
            val visibleItemCount = layoutManager.childCount // Number of items currently visible.
            val totalItemCount = layoutManager.itemCount // Total numbers of item attached to the Recyclerview.

            val isNoError = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage // Not loading && not at a last page.
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount // Check an item is at last item or not.
            val isNotAtBeginning = firstVisibleItemPosition >= 0 // Ensuring that first item should not be visible.
            val isTotalMoreThanVisible = totalItemCount >= PAGE_SIZE

            val shouldPaginate = isNoError && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                mViewModel.getAllBreakingNews("in")
                isScrolling = false
            }
        }
    }

    /**
     * Hide the progressbar
     */
    private fun hideProgressBar() {
        paginationProgressBar.isVisible = false
        isLoading = false
    }

    /**
     * Show the progressbar
     */
    private fun showProgressBar() {
        paginationProgressBar.isVisible = true
        isLoading = true
    }




    companion object {
        private const val TAG = "BreakingNewsFragment"
    }
}