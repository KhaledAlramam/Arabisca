package com.sedra.goiptv.view.movie

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.util.Util
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.UserInfo
import com.sedra.goiptv.databinding.ActivityPlayVideoBinding
import com.sedra.goiptv.utils.PREF_PORT
import com.sedra.goiptv.utils.PREF_URL
import com.sedra.goiptv.utils.STREAM_EXT
import com.sedra.goiptv.utils.STREAM_ID_INTENT_EXTRA
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayMovieActivity : AppCompatActivity() {

    lateinit var binding: ActivityPlayVideoBinding
    var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    var originalSize = true
    var url = ""
    @Inject
    lateinit var userInfo: UserInfo
    @Inject
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_video)
        url = preferences.getString(PREF_URL, "") ?: ""
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            videoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

//            fitScreen.setOnClickListener {
//                if (originalSize) {
//                    videoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
//                    fitScreen.text = "Restore"
//                } else {
//                    videoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
//                    fitScreen.text = "Full Screen"
//                }
//                originalSize = !originalSize
//            }
//            closePlayer.setOnClickListener {
//                finish()
//            }
//            videoView.setOnClickListener {
//                if (group.visibility == View.VISIBLE) {
//                    group.visibility = View.GONE
//                } else {
//                    group.visibility = View.VISIBLE
//                }
//            }
        }
    }


    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

//    @SuppressLint("InlinedApi")
//    private fun hideSystemUi() {
//        binding.videoView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
//                or View.SYSTEM_UI_FLAG_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
//    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
//        hideSystemUi()
        if (Util.SDK_INT < 24 || player == null) {
            initializePlayer()
        }
    }

    private fun initializePlayer() {
        val link = "http://${preferences.getString(PREF_URL,"")}:${preferences.getString(PREF_PORT,"")}/"

        if (player == null) {
            val trackSelector = DefaultTrackSelector(this)
            trackSelector.setParameters(
                    trackSelector.buildUponParameters().setMaxVideoSizeSd()
            )
            player = SimpleExoPlayer.Builder(this)
                    .setTrackSelector(trackSelector)
                    .build()
        }
        binding?.videoView?.player = player
        Log.e("TAG", "initializePlayer: ${
            "${link}movie/${userInfo?.username}/${userInfo?.password}/${
                intent.getIntExtra(STREAM_ID_INTENT_EXTRA, 0)
            }"
        }")

        val mediaItem =
                if (intent.getStringExtra(STREAM_EXT).isNullOrEmpty()) {
            MediaItem.Builder().apply {
                setUri(
                        "${link}movie/${userInfo.username}/${userInfo.password}/${
                            intent.getIntExtra(STREAM_ID_INTENT_EXTRA, 0)
                        }"
                )
            }.build()

        } else {
            MediaItem.Builder().apply {
                setUri(
                        "${link}movie/${userInfo.username}/${userInfo.password}/${
                            intent.getIntExtra(STREAM_ID_INTENT_EXTRA, 0)
                        }.${intent.getStringExtra(STREAM_EXT)}"
                )
            }.build()
        }

        player?.setMediaItem(mediaItem)
        player?.playWhenReady = playWhenReady
        player?.seekTo(currentWindow, playbackPosition)
        player?.prepare()
    }


    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.release()
            player = null
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        binding.videoView.showController()
        return super.onKeyDown(keyCode, event)
    }

}