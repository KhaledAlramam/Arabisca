package com.sedra.goiptv.view.channels

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Base64
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.Util
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.*
import com.sedra.goiptv.databinding.ActivityPlayChannelBinding
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.view.department.ChannelAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.text.FieldPosition
import javax.inject.Inject

@AndroidEntryPoint
class PlayChannelsNewActivity : AppCompatActivity() {
    private var binding: ActivityPlayChannelBinding? = null
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
    private val epgMap = HashMap<Int, List<EpgListings>>()
    lateinit var categoryAdapter: ChannelsCategoryAdapter
    lateinit var channelsAdapter: ChannelAdapter
    private val catList = ArrayList<Category>()
    private var currentPosition = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_channel)
        getChannelsData()
    }

    private fun setupUI() {
        initTimers()
        channelsAdapter = ChannelAdapter(this,
                object : ChannelOnClick {
                    override fun onClick(view: View, liveStream: LiveStream, position: Int) {
                        binding!!.group.visibility = View.GONE
                        binding!!.ChannelInPlayerRv.visibility = View.GONE
                        handleChannelChoosed(liveStream, position)
                    }
                })
        categoryAdapter = ChannelsCategoryAdapter(
                catList,
                object : CategoryOnClick {
                    override fun onClick(view: View, category: Category) {
                        channelsAdapter.submitList(channelList.filter { it.categoryId == category.category_id })
                        binding!!.ChannelInPlayerRv.visibility = View.VISIBLE
                    }
                })
        binding?.apply {
            channelCategoryInPlayer.layoutManager = LinearLayoutManager(this@PlayChannelsNewActivity)
            ChannelInPlayerRv.layoutManager = LinearLayoutManager(this@PlayChannelsNewActivity)
            channelCategoryInPlayer.adapter = categoryAdapter
            ChannelInPlayerRv.adapter = channelsAdapter
            playerParent.setOnClickListener {
                if (group.visibility == View.VISIBLE) {
                    group.visibility = View.GONE
                    ChannelInPlayerRv.visibility = View.GONE
                } else {
                    group.visibility = View.VISIBLE
                    ChannelInPlayerRv.visibility = View.VISIBLE
                }
            }
            upChannel.setOnClickListener {
                try {
                    val live = channelsAdapter.currentList[currentPosition + 1]
                    handleChannelChoosed(live, currentPosition + 1)
                } catch (e: Exception) {

                }
            }
            downChannel.setOnClickListener {
                try {
                    val live = channelsAdapter.currentList[currentPosition - 1]
                    handleChannelChoosed(live, currentPosition - 1)
                } catch (e: Exception) {

                }
            }
        }
        fetchChannels()
    }

    private fun handleChannelChoosed(liveStream: LiveStream, position: Int) {
        binding?.playedChannelName?.text = liveStream.name
        currentPosition = position
        binding?.channelNumber?.text = "${position+1}"
        if (epgMap[liveStream.streamId!!] == null) {
            fetchEpg(liveStream.streamId!!, liveStream.streamIcon)
        } else
            populateEpgAndDetermineVisibility(epgMap[liveStream.streamId!!], liveStream.streamIcon)
        currentStreamId = liveStream.streamId
        playLiveStream(currentStreamId)
    }

    private fun initTimers() {
        epgCountDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                binding?.apply {
                    epgConstraintLayout.isVisible = false
                }
            }
        }
    }

    private fun hideChannelList() {
        binding?.apply {
            group.visibility = View.GONE
            ChannelInPlayerRv.visibility = View.GONE
        }
    }

    private fun showChannelList() {
        binding?.apply {
            group.visibility = View.VISIBLE
            ChannelInPlayerRv.visibility = View.VISIBLE
        }
    }

    private fun getChannelsData() {
        viewModel.getChannelsCategories().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (resource.data != null) {
                            catList.clear()
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
        restartEpgViewTimer()
        Glide.with(this)
                .load(streamIcon)
                .into(binding!!.channelImg)
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
            binding?.apply {
                upNextTime.isVisible = true
                upNextTitle.isVisible = true
                nowWatchingTime.isVisible = true
                nowWatchingTitle.isVisible = true
                upNextTime.text = "$startTimeNext -> $endTimeNext"
                upNextTitle.text = decodedStringNext
                nowWatchingTime.text = "$startTime -> $endTime"
                nowWatchingTitle.text = decodedString
                textView16.isVisible = true
                textView17.isVisible = true
            }
        } else {
            binding?.apply {
                textView17.isVisible = false
                textView16.isVisible = false
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
        binding?.videoView?.player = player

    }

    private fun playLiveStream(id: Int?) {
        val url = "http://${preferences.getString(PREF_URL, "")}:${preferences.getString(PREF_PORT, "")}/"
        val mediaItem = MediaItem.Builder().apply {
            setUri("${url}${userInfo.username}/${userInfo.password}/${id}")
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
        if (keyCode != KeyEvent.KEYCODE_BACK)
            showChannelList()
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


    private fun restartEpgViewTimer() {
        if (epgCountDownTimer == null) return
        epgCountDownTimer?.cancel()
        epgCountDownTimer?.start()
        binding!!.epgConstraintLayout.isVisible = true
    }

    override fun onBackPressed() {
        if (binding!!.channelCategoryInPlayer.isVisible)
            hideChannelList()
        else
            super.onBackPressed()
    }

    override fun onDestroy() {
        epgCountDownTimer?.cancel()
        epgCountDownTimer = null
        binding = null
        super.onDestroy()
    }
}