package com.pivopiev.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    // ─────────────────────────────────────────────────────────────────────────
    //  ★ КОНФИГУРАЦИЯ — попълни своя API ключ тук
    // ─────────────────────────────────────────────────────────────────────────
    private val API_KEY = "PASTE_YOUR_API_KEY_HERE"
    private val CHANNEL_HANDLE = "Pivopiev"   // без @
    // ─────────────────────────────────────────────────────────────────────────

    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: VideoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var errorView: TextView
    private lateinit var btnRetry: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check API key
        if (API_KEY == "PASTE_YOUR_API_KEY_HERE") {
            showApiKeyError()
            return
        }

        setupViews()
        setupRecyclerView()
        observeViewModel()
        viewModel.loadVideos(API_KEY, CHANNEL_HANDLE)
    }

    private fun setupViews() {
        swipeRefresh = findViewById(R.id.swipeRefresh)
        progressBar = findViewById(R.id.progressBar)
        errorView = findViewById(R.id.tvError)
        btnRetry = findViewById(R.id.btnRetry)

        swipeRefresh.setColorSchemeResources(R.color.accent_red)
        swipeRefresh.setOnRefreshListener {
            viewModel.loadVideos(API_KEY, CHANNEL_HANDLE, refresh = true)
        }

        btnRetry.setOnClickListener {
            viewModel.loadVideos(API_KEY, CHANNEL_HANDLE, refresh = true)
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        adapter = VideoAdapter { video -> openVideo(video) }

        val columns = if (resources.configuration.screenWidthDp >= 600) 3 else 2
        val layoutManager = GridLayoutManager(this, columns)

        // Full-width span for loading-more footer
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = 1
        }

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        // Infinite scroll
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val lm = rv.layoutManager as GridLayoutManager
                val lastVisible = lm.findLastVisibleItemPosition()
                val total = adapter.itemCount
                if (lastVisible >= total - 6) {
                    viewModel.loadMore(API_KEY, CHANNEL_HANDLE)
                }
            }
        })
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    errorView.visibility = View.GONE
                    btnRetry.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                }
                is UiState.LoadingMore -> {
                    // Keep showing list, just a subtle indicator
                }
                is UiState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    errorView.visibility = View.GONE
                    btnRetry.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    adapter.submitList(state.videos)
                }
                is UiState.Error -> {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    if (adapter.itemCount == 0) {
                        recyclerView.visibility = View.GONE
                        errorView.visibility = View.VISIBLE
                        btnRetry.visibility = View.VISIBLE
                        errorView.text = "Грешка: ${state.message}"
                    } else {
                        Toast.makeText(this, "Грешка при зареждане: ${state.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun openVideo(video: VideoItem) {
        // Try YouTube app first, fall back to browser
        val youtubeAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:${video.videoId}"))
        youtubeAppIntent.setPackage("com.google.android.youtube")
        if (youtubeAppIntent.resolveActivity(packageManager) != null) {
            startActivity(youtubeAppIntent)
        } else {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=${video.videoId}")
            )
            startActivity(browserIntent)
        }
    }

    private fun showApiKeyError() {
        setContentView(R.layout.activity_setup)
    }
}
