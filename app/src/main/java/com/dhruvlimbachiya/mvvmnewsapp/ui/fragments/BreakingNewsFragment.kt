package com.dhruvlimbachiya.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhruvlimbachiya.mvvmnewsapp.R
import com.dhruvlimbachiya.mvvmnewsapp.adapters.NewsAdapter
import com.dhruvlimbachiya.mvvmnewsapp.ui.NewsActivity
import com.dhruvlimbachiya.mvvmnewsapp.ui.NewsViewModel
import com.dhruvlimbachiya.mvvmnewsapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*

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
    }

    /**
     * Set up the recyclerview
     */
    private fun setUpRecyclerView() {
        mAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * Function responsible for listening live events.
     */
    private fun subscribeToLiveEvents() {
        mViewModel.breakingNewsResponse.observe(viewLifecycleOwner) { newsResource ->
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
        private const val TAG = "BreakingNewsFragment"
    }
}