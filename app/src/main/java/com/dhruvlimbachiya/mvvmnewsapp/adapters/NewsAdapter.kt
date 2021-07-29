package com.dhruvlimbachiya.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dhruvlimbachiya.mvvmnewsapp.R
import com.dhruvlimbachiya.mvvmnewsapp.model.Article
import com.dhruvlimbachiya.mvvmnewsapp.utils.convertTimestampToDate
import kotlinx.android.synthetic.main.item_article_preview.view.*

/**
 * Created by Dhruv Limbachiya on 28-07-2021.
 */

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Article>(){

        override fun areItemsTheSame(oldItem: Article, newItem: Article) = oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: Article, newItem: Article) = oldItem == newItem

    }

    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position] // Current article.
        holder.itemView.apply {
            Glide.with(this)
                .load(article.urlToImage)
                .transform(MultiTransformation(RoundedCorners(14)))
                .placeholder(R.drawable.news_placeholder)
                .error(R.drawable.news_placeholder)
                .into(ivArticleImage)


            tvSource.text = article.source?.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt?.let { convertTimestampToDate(it) }

            setOnClickListener {
                onItemClickListener?.let { listener ->
                    listener(article)
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((Article) -> Unit)? = null

    // Setter method for onItemClickListener.
    fun setOnItemClickListener(listener: (Article) -> Unit){
        onItemClickListener = listener
    }
}