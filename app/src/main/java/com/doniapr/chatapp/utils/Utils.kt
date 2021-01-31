package com.doniapr.chatapp.utils

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

object Utils {
    fun buildMediaSource(uri: Uri, context: Context?): MediaSource? {
        val mediaSource: MediaSource
        mediaSource =
            if (uri.toString().startsWith("http://") || uri.toString().startsWith("https://")) {
                val dataSourceFactory: DataSource.Factory = DefaultHttpDataSourceFactory("doniapr")
                ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            } else {
                val dataSourceFactoryFile: DataSource.Factory = DefaultDataSourceFactory(
                    context, Util.getUserAgent(context, "ChatApp")
                )
                ExtractorMediaSource.Factory(dataSourceFactoryFile).createMediaSource(uri)
            }
        return mediaSource
    }
}