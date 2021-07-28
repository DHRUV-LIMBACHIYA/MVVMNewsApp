package com.dhruvlimbachiya.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhruvlimbachiya.mvvmnewsapp.R
import com.dhruvlimbachiya.mvvmnewsapp.adapters.NewsAdapter
import com.dhruvlimbachiya.mvvmnewsapp.ui.NewsActivity
import com.dhruvlimbachiya.mvvmnewsapp.ui.NewsViewModel
import kotlinx.android.synthetic.main.fragment_search_news.*

/**
 * Created by Dhruv Limbachiya on 27-07-2021.
 */
class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    lateinit var mViewModel: NewsViewModel
    private lateinit var mAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewModel = (activity as NewsActivity).mViewModel

        setUpRecyclerView()

        mAdapter.setOnItemClickListener { article ->
            findNavController().navigate(
                SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleFragment(article) // Pass Article from Breaking News Fragment to Article Fragment.
            )
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
}