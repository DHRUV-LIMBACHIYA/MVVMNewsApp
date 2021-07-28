package com.dhruvlimbachiya.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.dhruvlimbachiya.mvvmnewsapp.R
import com.dhruvlimbachiya.mvvmnewsapp.ui.NewsActivity
import com.dhruvlimbachiya.mvvmnewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

/**
 * Created by Dhruv Limbachiya on 27-07-2021.
 */
class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var mViewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewModel = (activity as NewsActivity).mViewModel

        val article = args.article

        webView.apply {
            webViewClient = WebViewClient() // The client is responsible for loading web page in our app not in browser.
            loadUrl(article.url) // Load the web page url.
        }

        fab.setOnClickListener {
            mViewModel.insertOrUpdateArticle(article) // Insert article into Room Database.
            Snackbar.make(it,"Article saved successfully",Snackbar.LENGTH_SHORT).show()
        }
    }
}