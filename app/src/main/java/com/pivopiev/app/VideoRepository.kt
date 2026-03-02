package com.pivopiev.app

import android.util.Log

class VideoRepository {

    private val api = YouTubeApiService.create()

    // Cache the uploads playlist ID so we don't refetch it every time
    private var uploadsPlaylistId: String? = null

    suspend fun fetchVideos(
        apiKey: String,
        channelHandle: String,
        pageToken: String? = null
    ): Result<Pair<List<VideoItem>, String?>> {
        return try {
            // Step 1 – resolve channel handle → uploads playlist ID
            if (uploadsPlaylistId == null) {
                val channelResp = api.getChannelByHandle(forHandle = channelHandle, apiKey = apiKey)
                if (!channelResp.isSuccessful) {
                    return Result.failure(Exception("Channel lookup failed: ${channelResp.code()} ${channelResp.errorBody()?.string()}"))
                }
                uploadsPlaylistId = channelResp.body()?.items?.firstOrNull()
                    ?.contentDetails?.relatedPlaylists?.uploads
                    ?: return Result.failure(Exception("Uploads playlist not found for handle: $channelHandle"))
            }

            // Step 2 – fetch playlist items (already sorted newest-first)
            val playlistResp = api.getPlaylistItems(
                playlistId = uploadsPlaylistId!!,
                pageToken = pageToken,
                apiKey = apiKey
            )

            if (!playlistResp.isSuccessful) {
                return Result.failure(Exception("Playlist fetch failed: ${playlistResp.code()} ${playlistResp.errorBody()?.string()}"))
            }

            val body = playlistResp.body()
            val videos = body?.items?.mapNotNull { item ->
                val snippet = item.snippet ?: return@mapNotNull null
                val videoId = snippet.resourceId?.videoId ?: return@mapNotNull null
                VideoItem(
                    videoId = videoId,
                    title = snippet.title ?: "Без заглавие",
                    description = snippet.description ?: "",
                    publishedAt = snippet.publishedAt ?: "",
                    thumbnailUrl = snippet.thumbnails?.maxres?.url
                        ?: snippet.thumbnails?.high?.url
                        ?: snippet.thumbnails?.medium?.url
                        ?: ""
                )
            } ?: emptyList()

            Result.success(Pair(videos, body?.nextPageToken))

        } catch (e: Exception) {
            Log.e("VideoRepository", "Error fetching videos", e)
            Result.failure(e)
        }
    }

    fun clearCache() {
        uploadsPlaylistId = null
    }
}
