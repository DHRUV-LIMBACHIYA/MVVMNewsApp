package com.dhruvlimbachiya.mvvmnewsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dhruvlimbachiya.mvvmnewsapp.R
import com.dhruvlimbachiya.mvvmnewsapp.api.NewsRetrofitInstance
import com.dhruvlimbachiya.mvvmnewsapp.db.ArticleDatabase
import com.dhruvlimbachiya.mvvmnewsapp.repository.NewsRepository
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.coroutines.launch

class NewsActivity : AppCompatActivity() {

    lateinit var mViewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        configureViewModel()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment

        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
    }

    /**
     * Initialize and set up the NewsViewModel.
     */
    private fun configureViewModel() {
        val repository = NewsRepository(ArticleDatabase(this))
        val viewModelFactory = NewsViewModelFactory(application,repository)
        mViewModel = ViewModelProvider(this, viewModelFactory).get(NewsViewModel::class.java)
    }
}
