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
import com.sedra.goiptv.data.model.*
import com.sedra.goiptv.databinding.ActivityPlayChannelBinding
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.view.department.ChannelAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayChannelsNewActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlayChannelBinding
    private val viewModel: ChannelsViewModel by viewModels()
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var currentStreamId: Int? = -1

    @Inject
    lateinit var userInfo: UserInfo

    @Inject
    lateinit var preferences: SharedPreferences
    val channelList = ArrayList<LiveStream>()
    private var epgCountDownTimer: CountDownTimer? = null
    private var channelSwitch: CountDownTimer? = null
    private val epgMap = HashMap<Int, List<EpgListings>>()
    lateinit var categoryAdapter: ChannelsCategoryAdapter
    lateinit var channelsAdapter: ChannelAdapter
    private val catList = ArrayList<Category>()
    private var currentPosition = -1
    var currentEnteredNumber = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_channel)
        getChannelsData()
    }

    private fun setupUI() {
        initTimers()
        binding.epgConstraintLayout.root.isVisible = false
        channelsAdapter = ChannelAdapter(this,
                object : ChannelOnClick {
                    override fun onClick(view: View, clicked: Boolean, liveStream: LiveStream, position: Int) {
                        binding.apply {
//                            epgConstraintLayout.root.isVisible = !clicked
//                            group.isVisible = !clicked
//                            ChannelInPlayerRv.isVisible = !clicked
                            if (clicked) {
                                restartEpgViewTimer()
//                                epgConstraintLayout.root.isVisible = !clicked
                                group.isVisible = !clicked
                                ChannelInPlayerRv.isVisible = !clicked
                            }
                        }
                        if(!clicked) handleChannelChoosed(liveStream, position)
                    }
                })
        categoryAdapter = ChannelsCategoryAdapter(
                catList,
                object : CategoryOnClick {
                    override fun onClick(view: View, category: Category) {
                        if (category.category_id == "-1")
                            channelsAdapter.submitList(channelList)
                        else
                            channelsAdapter.submitList(channelList.filter { it.categoryId == category.category_id })
                        binding.ChannelInPlayerRv.isVisible = true
                    }
                })
        binding.apply {
            videoView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            channelCategoryInPlayer.layoutManager = LinearLayoutManager(this@PlayChannelsNewActivity)
            ChannelInPlayerRv.layoutManager = LinearLayoutManager(this@PlayChannelsNewActivity)
            channelCategoryInPlayer.adapter = categoryAdapter
            ChannelInPlayerRv.adapter = channelsAdapter
            playerParent.setOnClickListener {
                if (group.visibility == View.VISIBLE) {
                    hideChannelList()
                } else {
                    showChannelList()
                }
            }

        }
        fetchChannels()
    }

    private fun decreaseChannel() {
        try {
            val live = channelsAdapter.currentList[currentPosition - 1]
            handleChannelChoosed(live, currentPosition - 1)
            restartEpgViewTimer()
        } catch (e: Exception) {

        }
    }

    private fun increaseChannel() {
        try {
            val live = channelsAdapter.currentList[currentPosition + 1]
            handleChannelChoosed(live, currentPosition + 1)
            restartEpgViewTimer()
        } catch (e: Exception) {

        }
    }

    private fun handleChannelChoosed(liveStream: LiveStream, position: Int) {
        binding.epgConstraintLayout.playedChannelName.text = liveStream.name
        currentPosition = position
        binding.epgConstraintLayout.channelNumber.text = "${position+1}"
        if (epgMap[liveStream.streamId!!] == null) {
            fetchEpg(liveStream.streamId!!, liveStream.streamIcon)
        } else
            populateEpgAndDetermineVisibility(epgMap[liveStream.streamId!!], liveStream.streamIcon)
        currentStreamId = liveStream.streamId
        playLiveStream(liveStream)
    }

    private fun initTimers() {
        epgCountDownTimer = object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                binding.apply {
                    epgConstraintLayout.root.isVisible = false
                    group.isVisible = false
                    ChannelInPlayerRv.isVisible = false
                }

            }
        }
        initChannelSwitchTimer()
    }

    private fun initChannelSwitchTimer() {
        channelSwitch = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                binding.apply {
                    channelSwitchTv.isVisible = false
                    currentEnteredNumber = ""
                    try {
                        handleChannelChoosed(
                            channelList[currentEnteredNumber.toInt()],
                            currentEnteredNumber.toInt() + 1
                        )
                        channelSwitch = null
                    } catch (e: Exception) {

                    }
                }

            }
        }
    }

    private fun hideChannelList() {
        binding.apply {
            group.isVisible = false
            ChannelInPlayerRv.isVisible = false
        }
    }

    private fun showChannelList() {
        binding.apply {
            epgConstraintLayout.root.isVisible = false
            group.isVisible = true
            ChannelInPlayerRv.isVisible = true
        }
    }

    private fun getChannelsData() {
        viewModel.getChannelsCategories().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (resource.data != null) {
                            catList.clear()
                            catList.add(Category(
                                    "-1",
                                    "All Channels",
                                    0
                            ))
                            catList.addAll(resource.data)
                            setupUI()
                        }

                    }
                    Status.ERROR -> {
                    }
                    Status.LOADING -> {
                    }
                }
            }
        }
    }

    private fun fetchChannels() {
        viewModel.getAllChannels().observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { data ->
                            channelList.clear()
                            channelList.addAll(data)
                        }
                    }
                    Status.ERROR -> {
                        Log.e("TAG", "fetchChannels: ${resource.message}")
                    }
                    Status.LOADING -> {

                    }
                }
            }

        })
    }

    private fun fetchEpg(streamId: Int, streamIcon: String? = null) {
        viewModel.getEpg(streamId, 2)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                epgMap[streamId] = it.data!!.epg_listings
                                populateEpgAndDetermineVisibility(epgMap[streamId], streamIcon)
                            }
                            Status.ERROR -> {
                            }
                            Status.LOADING -> {
                            }
                        }
                    }
                }
    }

    private fun populateEpgAndDetermineVisibility(epgList: List<EpgListings>?, streamIcon: String?) {
//        restartEpgViewTimer()
        binding.epgConstraintLayout.apply {
            upNextTime.text = ""
            upNextTitle.text = ""
            nowWatchingTime.text = ""
            nowWatchingTitle.text = ""
        }
        Glide.with(this)
                .load(streamIcon)
                .into(binding.epgConstraintLayout.channelImg)
        if (epgList != null && epgList.isNotEmpty()) {
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
            binding.epgConstraintLayout.apply {
                upNextTime.isVisible = true
                upNextTitle.isVisible = true
                nowWatchingTime.isVisible = true
                nowWatchingTitle.isVisible = true
                upNextTime.text = "$startTimeNext -> $endTimeNext"
                upNextTitle.text = decodedStringNext
                nowWatchingTime.text = "$startTime -> $endTime"
                nowWatchingTitle.text = decodedString
                nowWatchingTitle.setOnClickListener {

                    playTimeShift(epgItem)
                }
            }
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
        if (player == null) {
            val trackSelector = DefaultTrackSelector(this)
            trackSelector.setParameters(
                    trackSelector.buildUponParameters().setMaxVideoSizeSd()
            )
            player = SimpleExoPlayer.Builder(this)
                    .setTrackSelector(trackSelector)
                    .build()
        }
        binding.videoView.player = player

    }

    private fun playLiveStream(liveStream: LiveStream) {
        val url =
            "http://${preferences.getString(PREF_URL, "")}:${preferences.getString(PREF_PORT, "")}/"
        Log.e(
            "TAG",
            "playLiveStream:${url}${userInfo.username}/${userInfo.password}/${liveStream.streamId} ",
        )
        val mediaItem = MediaItem.Builder().apply {
            setUri("${url}${userInfo.username}/${userInfo.password}/${liveStream.streamId}")
        }.build()

        player?.setMediaItem(mediaItem)
        player?.playWhenReady = playWhenReady
        player?.seekTo(currentWindow, playbackPosition)
        player?.prepare()
    }

    private fun playTimeShift(epg: EpgListings) {
        val url = "http://${preferences.getString(PREF_URL, "")}:${preferences.getString(PREF_PORT, "")}/"

        val mediaItem = MediaItem.Builder().apply {
//            setUri("${url}${userInfo.username}/${userInfo.password}/${id}")
            setUri("http://ui-tv.se:2095/streaming/timeshift.php?username=00531832836&password=00531832836&stream=16&start=2021-04-12:00-21&duration=60")
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
//        if (!binding.ChannelInPlayerRv.isVisible){
//            when (keyCode) {
//                KeyEvent.KEYCODE_DPAD_UP -> {
//                    increaseChannel()
//                }
//                KeyEvent.KEYCODE_DPAD_DOWN -> {
//                    decreaseChannel()
//                }
//                KeyEvent.KEYCODE_CHANNEL_UP -> {
//                    increaseChannel()
//                }
//                KeyEvent.KEYCODE_CHANNEL_DOWN -> {
//                    decreaseChannel()
//                }
//                else ->{
//                    if (keyCode != KeyEvent.KEYCODE_BACK)
//                        showChannelList()
//                }
//            }
//        }
        if (!binding.ChannelInPlayerRv.isVisible && keyCode != KeyEvent.KEYCODE_BACK) {
            showChannelList()
        }
        if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            binding.channelSwitchTv.isVisible = true
            currentEnteredNumber += keyCode
            binding.channelSwitchTv.text = currentEnteredNumber
            if (channelSwitch == null) {
                initChannelSwitchTimer()
                channelSwitch?.start()
            }

        }

        return super.onKeyDown(keyCode, event)
    }


    private fun restartEpgViewTimer() {
        if (epgCountDownTimer == null) return
        epgCountDownTimer?.cancel()
        epgCountDownTimer?.start()
        binding.epgConstraintLayout.root.isVisible = true
    }

    override fun onBackPressed() {
        if (binding.channelCategoryInPlayer.isVisible)
            hideChannelList()
        else
            super.onBackPressed()
    }

    override fun onDestroy() {
        epgCountDownTimer?.cancel()
        epgCountDownTimer = null
        channelSwitch?.cancel()
        channelSwitch = null
        super.onDestroy()
    }
}