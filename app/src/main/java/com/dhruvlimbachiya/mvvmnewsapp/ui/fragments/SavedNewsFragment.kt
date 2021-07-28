package com.dhruvlimbachiya.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhruvlimbachiya.mvvmnewsapp.R
import com.dhruvlimbachiya.mvvmnewsapp.adapters.NewsAdapter
import com.dhruvlimbachiya.mvvmnewsapp.ui.NewsActivity
import com.dhruvlimbachiya.mvvmnewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*
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

        mViewModel.getSavedArticles().observe(viewLifecycleOwner){ articles ->
            if(articles.isNotEmpty()){
                mAdapter.differ.submitList(articles)
            }
        }

        swipeToDelete(view)
    }

    /**
     * Set up the recyclerview
     */
    private fun setUpRecyclerView() {
        mAdapter = NewsAdapter()
        rvSavedNews.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * Add swipe to delete feature and a option to undo the delete operation.
     */
    private fun swipeToDelete(view: View) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition // Get the current position.
                val article =
                    mAdapter.differ.currentList[position] // Get the article using position.
                mViewModel.deleteArticle(article) // Delete the article in Room Db.

                Snackbar.make(view, "Successfully deleted", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo") {
                        mViewModel.insertOrUpdateArticle(article) // Insert that article again on clicking "UNDO" action button.
                    }
                    show()
                }
            }
        }

        // Attach ItemTouchHelper to the Saved News RecyclerView.
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }
    }


}