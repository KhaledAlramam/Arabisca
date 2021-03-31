package com.sedra.goiptv.view.channels

import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.util.Util
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.Category
import com.sedra.goiptv.data.model.EpgListings
import com.sedra.goiptv.data.model.LiveStream
import com.sedra.goiptv.data.model.UserInfo
import com.sedra.goiptv.databinding.ActivityPlayChannelBinding
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.utils.Status.*
import com.sedra.goiptv.view.department.ChannelAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayChannelActivity : AppCompatActivity() {

    var _binding: ActivityPlayChannelBinding? = null
    val binding by lazy { _binding!! }
    val viewModel: ChannelsViewModel by viewModels()
    var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    @Inject
    lateinit var userInfo: UserInfo

    @Inject
    lateinit var preferences: SharedPreferences
    var originalSize = true
    val channelList = ArrayList<LiveStream>()
    lateinit var countDownTimer: CountDownTimer
    private val epgMap = HashMap<Int, List<EpgListings>>()
    lateinit var categoryAdapter: ChannelsCategoryAdapter
    lateinit var channelsAdapter: ChannelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_play_channel)
        setupUI()
    }


    private fun setupUI() {
        val url = "http://${preferences.getString(PREF_URL, "")}:${preferences.getString(PREF_PORT, "")}/"
        countDownTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                binding.group.visibility = View.GONE
                binding.bottomBannerGroup.visibility = View.GONE
                binding.ChannelInPlayerRv.visibility = View.GONE
            }
        }
        binding.apply {
            fitScreen.setOnClickListener {
                if (originalSize) {
                    videoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    fitScreen.text = "Restore"
                } else {
                    videoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    fitScreen.text = "Full Screen"
                }
                originalSize = !originalSize
            }
            closePlayer.setOnClickListener {
                finish()
            }
        }
        channelsAdapter = ChannelAdapter(
                object : ChannelOnClick {
                    override fun onClick(view: View, liveStream: LiveStream) {
                        if (epgMap[liveStream.streamId!!] == null) {
                            fetchEpg(liveStream.streamId!!)
                        } else
                            populateEpgAndDetermineVisibility(epgMap[liveStream.streamId!!], liveStream.streamIcon)
                        val currentId = liveStream.streamId
                        Log.e(
                                "TAG",
                                "$url/${this@PlayChannelActivity.userInfo?.username}/${this@PlayChannelActivity.userInfo?.password}/${
                                    currentId
                                }",
                        )
                        val mediaItem = MediaItem.Builder().setUri(
                                "$url/${this@PlayChannelActivity.userInfo?.username}/${this@PlayChannelActivity.userInfo?.password}/${
                                    currentId
                                }").build()
                        player!!.setMediaItem(mediaItem)
                        player!!.seekTo(currentWindow, playbackPosition)
                        player!!.playWhenReady = playWhenReady
                        player!!.prepare()
                        player!!.play()
                    }
                })
        categoryAdapter = ChannelsCategoryAdapter(
                intent.extras?.getSerializable(CATEGORY_LIST_INTENT_EXTRA) as List<Category>,
                object : CategoryOnClick {
                    override fun onClick(view: View, category: Category) {
                        channelsAdapter.submitList(channelList.filter { it.categoryId == category.category_id })
                        binding.ChannelInPlayerRv.visibility = View.VISIBLE
                    }
                })
        binding.apply {
            channelCategoryInPlayer.layoutManager = LinearLayoutManager(this@PlayChannelActivity)
            ChannelInPlayerRv.layoutManager = LinearLayoutManager(this@PlayChannelActivity)
            channelCategoryInPlayer.adapter = categoryAdapter
            ChannelInPlayerRv.adapter = channelsAdapter
        }
        fetchChannels()
        binding.apply {
            videoView.setOnClickListener {
                if (group.visibility == View.VISIBLE) {
                    group.visibility = View.GONE
                    ChannelInPlayerRv.visibility = View.GONE
                } else {
                    group.visibility = View.VISIBLE
                    ChannelInPlayerRv.visibility = View.VISIBLE
                }
            }

        }
        fetchEpg(
                intent.getIntExtra(STREAM_ID_INTENT_EXTRA, 0),
                intent.getStringExtra(STREAM_IMG),
        )
    }


    private fun fetchChannels() {
        val url = "http://${preferences.getString(PREF_URL, "")}:${preferences.getString(PREF_PORT, "")}/"
        viewModel.getAllChannels().observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        resource.data?.let { data ->
                            channelList.clear()
                            channelList.addAll(data)
                            val selectedOne = intent.getIntExtra(STREAM_ID_INTENT_EXTRA, 0)

                            countDownTimer.start()
                            player!!.setMediaItems(channelList.map {
                                MediaItem.Builder()
                                        .apply {
                                            setUri(
                                                    "$url/${this@PlayChannelActivity.userInfo?.username}/${this@PlayChannelActivity.userInfo?.password}/${
                                                        it.streamId
                                                    }"
                                            )
                                        }.build()
                            })
                            player!!.seekToDefaultPosition(
                                    channelList.indexOf(channelList.find { stream ->
                                        stream.streamId == selectedOne

                                    })
                            )
                        }
                    }
                    ERROR -> {

                    }
                    LOADING -> {

                    }
                }
            }

        })
    }

    private fun fetchEpg(streamId: Int, streamIcon:String? = null) {
        viewModel.getEpg(streamId, 2)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            SUCCESS -> {
                                epgMap[streamId] = it.data!!.epg_listings
                                populateEpgAndDetermineVisibility(epgMap[streamId], streamIcon)
                            }
                            ERROR -> {
                            }
                            LOADING -> {
                            }
                        }
                    }
                }
    }

    //
    private fun populateEpgAndDetermineVisibility(epgList: List<EpgListings>?, streamIcon: String?) {
        Glide.with(this@PlayChannelActivity)
                .load(streamIcon)
                .into(binding.channelImg)
        if (epgList != null) {
            if (epgList.isNotEmpty()) {
                val epgItem = epgList[0]
                val startTime = epgItem.start.split(" ")[1]
                val endTime = epgItem.end.split(" ")[1]
                val decodedBytes: ByteArray = Base64.decode(epgItem.title, Base64.DEFAULT)
                val decodedString = String(decodedBytes)
                val secondEpgItem = epgList.last()
                val startTimeNext = secondEpgItem.start.split(" ")[1]
                val endTimeNext = secondEpgItem.end.split(" ")[1]
                val decodedBytesNext: ByteArray = Base64.decode(secondEpgItem.title, Base64.DEFAULT)
                val decodedStringNext = String(decodedBytesNext)
                binding.apply {
                    upNextTime.text = "$startTimeNext -> $endTimeNext"
                    upNextTitle.text = decodedStringNext
                    nowWatchingTime.text = "$startTime -> $endTime"
                    nowWatchingTitle.text = decodedString

                }
            } else {
//                binding.bottomBannerGroup.visibility = View.GONE
            }
        } else {
//            binding.bottomBannerGroup.visibility = View.GONE
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

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || player == null) {
            initializePlayer()
        }
    }

    private fun initializePlayer() {
        val url = "http://${preferences.getString(PREF_URL, "")}:${preferences.getString(PREF_PORT, "")}/"

        if (player == null) {
            val trackSelector = DefaultTrackSelector(this)
            trackSelector.setParameters(
                    trackSelector.buildUponParameters().setMaxVideoSizeSd()
            )
            player = SimpleExoPlayer.Builder(this)
                    .setTrackSelector(trackSelector)
                    .build()
        }
        _binding?.videoView?.player = player
        val mediaItem = MediaItem.Builder().apply {
            setUri(
                    "${url}${userInfo.username}/${userInfo.password}/${
                        intent.getIntExtra(STREAM_ID_INTENT_EXTRA, 0)
                    }"
            )
        }.build()

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

        restartTimer()

        when (keyCode) {
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                player!!.next()
            }
            KeyEvent.KEYCODE_MEDIA_STEP_BACKWARD -> {
                player!!.previous()
            }
        }


        return super.onKeyDown(keyCode, event)
    }

    private fun restartTimer() {
        countDownTimer.cancel()
        countDownTimer.start()
        if (binding.group.visibility == View.GONE) {
            binding.group.visibility = View.VISIBLE
            binding.bottomBannerGroup.isVisible = true
        }
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }
}