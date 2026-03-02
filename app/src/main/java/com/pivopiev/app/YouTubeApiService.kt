package com.pivopiev.app

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {

    // Step 1: Get uploads playlist ID from channel handle
    @GET("channels")
    suspend fun getChannelByHandle(
        @Query("part") part: String = "contentDetails",
        @Query("forHandle") forHandle: String,
        @Query("key") apiKey: String
    ): Response<ChannelResponse>

    // Step 2: List videos in the uploads playlist (newest first by default)
    @GET("playlistItems")
    suspend fun getPlaylistItems(
        @Query("part") part: String = "snippet",
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int = 50,
        @Query("pageToken") pageToken: String? = null,
        @Query("key") apiKey: String
    ): Response<PlaylistResponse>

    companion object {
        private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

        fun create(): YouTubeApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(YouTubeApiService::class.java)
        }
    }
}
