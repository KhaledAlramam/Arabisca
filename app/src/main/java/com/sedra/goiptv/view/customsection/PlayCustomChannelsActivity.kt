package com.sedra.goiptv.view.customsection

import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.Util
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.*
import com.sedra.goiptv.databinding.ActivityPlayChannelBinding
import com.sedra.goiptv.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayCustomChannelsActivity : AppCompatActivity() {

    private var binding: ActivityPlayChannelBinding? = null
    val viewModel: SubSectionsViewModel by viewModels()
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var currentStreamId: Int? = -1

    @Inject
    lateinit var userInfo: UserInfo

    @Inject
    lateinit var preferences: SharedPreferences
    private var currentPosition = -1


    private val itemsHashMap = HashMap<Int, List<CustomItem>>()
    lateinit var itemsAdapter: CustomChannelAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_channel)
        setupUI()
    }

    private fun setupUI() {
        itemsAdapter = CustomChannelAdapter(this, object : PositionOnClick {
            override fun onClick(view: View, position: Int) {
                binding!!.group.isVisible = false
                binding!!.ChannelInPlayerRv.isVisible = false
                handleChannelChoosed(itemsAdapter.currentList[position], position)
            }
        })
        binding?.apply {
            playerParent.setOnClickListener {
                if (group.visibility == View.VISIBLE) {
                    hideChannelList()
                } else {
                    showChannelList()
                }
            }
            upChannel.setOnClickListener {
                increaseChannel()
            }
            downChannel.setOnClickListener {
                decreaseChannel()
            }
        }
        getSubSections()
    }

    private fun decreaseChannel() {
        try {
            val live = itemsAdapter.currentList[currentPosition - 1]
            handleChannelChoosed(live, currentPosition - 1)
        } catch (e: Exception) {

        }
    }

    private fun increaseChannel() {
        try {
            val live = itemsAdapter.currentList[currentPosition + 1]
            handleChannelChoosed(live, currentPosition + 1)
        } catch (e: Exception) {

        }
    }

    private fun handleChannelChoosed(liveStream: CustomItem, position: Int) {
        binding?.playedChannelName?.text = liveStream.name
        currentPosition = position
        binding?.channelNumber?.text = "${position + 1}"
        currentStreamId = liveStream.id
        playLiveStream(liveStream)
    }

    private fun hideChannelList() {
        binding?.apply {
            group.isVisible = false
            ChannelInPlayerRv.isVisible = false
        }
    }

    private fun showChannelList() {
        binding?.apply {
            epgConstraintLayout.isVisible = false
            group.isVisible = true
            ChannelInPlayerRv.isVisible = true
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

    private fun playLiveStream(customItem: CustomItem) {
        val mediaItem = MediaItem.Builder().apply {
            setUri(customItem.video)
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
        if (!binding!!.ChannelInPlayerRv.isVisible) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> {
                    increaseChannel()
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    decreaseChannel()
                }
                KeyEvent.KEYCODE_CHANNEL_UP -> {
                    increaseChannel()
                }
                KeyEvent.KEYCODE_CHANNEL_DOWN -> {
                    decreaseChannel()
                }
                else -> {
                    if (keyCode != KeyEvent.KEYCODE_BACK)
                        showChannelList()
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }


    override fun onBackPressed() {
        if (binding!!.channelCategoryInPlayer.isVisible)
            hideChannelList()
        else
            super.onBackPressed()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun getSubSections() {
        viewModel.getSubSections(intent.getIntExtra(EXTRA_TYPE_ID, 0)).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (resource.data != null)
                            manipulateData(resource.data)
                    }
                    Status.ERROR -> {
                    }
                    Status.LOADING -> {
                    }
                }
            }
        }
    }

    private fun getItems(subSectionId: Int) {
        viewModel.getItems(subSectionId).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (resource.data != null) {
                            itemsHashMap[subSectionId] = resource.data.data
                            showItems(resource.data.data)
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

    fun showItems(items: List<CustomItem>) {
        itemsAdapter.submitList(items)
    }

    private fun manipulateData(response: SubSectionsResponse) {
        val departmentTitleAdapter = CustomLiveTitleAdapter(response.data, object : OnDepartmentClicked {
            override fun onClick(view: View, position: Int) {
                val id = response.data[position].id
                binding!!.ChannelInPlayerRv.isVisible = true
                if (itemsHashMap.keys.contains(id)) {
                    showItems(itemsHashMap[id]!!)
                } else {
                    getItems(id)
                }
            }
        })
        binding?.channelCategoryInPlayer?.apply {
            adapter = departmentTitleAdapter
            layoutManager = LinearLayoutManager(this@PlayCustomChannelsActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        binding?.ChannelInPlayerRv?.apply {
            adapter = itemsAdapter
            layoutManager = GridLayoutManager(this@PlayCustomChannelsActivity, 2, LinearLayoutManager.HORIZONTAL, false)
        }
    }

}