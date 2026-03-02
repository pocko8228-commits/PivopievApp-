package com.pivopiev.app

import com.google.gson.annotations.SerializedName

// ── YouTube API response models ──────────────────────────────────────────────

data class ChannelResponse(
    val items: List<ChannelItem>?
)

data class ChannelItem(
    val contentDetails: ContentDetails?
)

data class ContentDetails(
    val relatedPlaylists: RelatedPlaylists?
)

data class RelatedPlaylists(
    val uploads: String?
)

// ── Playlist items ───────────────────────────────────────────────────────────

data class PlaylistResponse(
    val nextPageToken: String?,
    val items: List<PlaylistItem>?
)

data class PlaylistItem(
    val snippet: PlaylistSnippet?
)

data class PlaylistSnippet(
    val title: String?,
    val description: String?,
    val publishedAt: String?,
    val thumbnails: Thumbnails?,
    val resourceId: ResourceId?
)

data class Thumbnails(
    val medium: Thumbnail?,
    val high: Thumbnail?,
    val maxres: Thumbnail?
)

data class Thumbnail(
    val url: String?
)

data class ResourceId(
    val videoId: String?
)

// ── UI model ─────────────────────────────────────────────────────────────────

data class VideoItem(
    val videoId: String,
    val title: String,
    val description: String,
    val publishedAt: String,
    val thumbnailUrl: String
)
