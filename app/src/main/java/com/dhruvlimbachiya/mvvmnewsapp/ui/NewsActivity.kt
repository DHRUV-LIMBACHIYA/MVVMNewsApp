package com.dhruvlimbachiya.mvvmnewsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dhruvlimbachiya.mvvmnewsapp.R
import com.dhruvlimbachiya.mvvmnewsapp.api.NewsRetrofitInstance
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.coroutines.launch

class NewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        // Testing
        lifecycleScope.launch {
            NewsRetrofitInstance.api.getBreakingNews()
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment

        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
    }
}
