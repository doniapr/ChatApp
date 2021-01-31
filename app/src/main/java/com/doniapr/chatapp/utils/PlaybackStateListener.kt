package com.doniapr.chatapp.utils

import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player

class PlaybackStateListener: Player.EventListener {
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        val stateString: String = when (playbackState) {
            ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
            ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
            ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
            ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
            else -> "UNKNOWN_STATE             -"
        }
        Log.d(
            "PlaybackStateListener", "changed state to " + stateString
                    + " playWhenReady: " + playWhenReady
        )
    }
}