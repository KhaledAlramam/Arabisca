package com.sedra.goiptv.view.customsection

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sedra.goiptv.*
import com.sedra.goiptv.data.model.ItemsResponse
import com.sedra.goiptv.data.model.UserInfo
import com.sedra.goiptv.data.model.series_models.Season
import com.sedra.goiptv.databinding.ActivitySeriesDetailsForTvBinding
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.view.series.SeasonAdapterOnTv
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CustomSeriesDetailsActivity : AppCompatActivity() {

    private val viewModel: CustomSeriesViewModel by viewModels()
    lateinit var binding: ActivitySeriesDetailsForTvBinding
    lateinit var episodeAdapter: CustomEpisodeAdapter
    lateinit var adapter: SeasonAdapterOnTv

    @Inject
    lateinit var userInfo: UserInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_series_details_for_tv)
        fetchSeriesDetails()
        binding.back.setOnClickListener {
            finish()
        }
    }


    private fun fetchSeriesDetails() {
        viewModel.getSeriesSeasons(
                intent.getIntExtra(SERIES_ID_PARAMETER, 0)
        ).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    it?.let { resource ->
                        Glide.with(this)
                                .load(resource.data!!.data[0].image)
                                .into(binding!!.seriesCover)
                        updateUI(resource.data!!)
                    }

                }
                Status.ERROR -> {
                    Log.e("TAG", "onCreate: " + it.message)

                }
                Status.LOADING -> {
                }
            }
        }
    }

    private fun updateUI(data: ItemsResponse) {
        val seasons = data.data.map {
            Season().apply {
                id = it.id
                name = it.name
                cover = it.image
            }
        }
        episodeAdapter =
                CustomEpisodeAdapter(emptyList(), "seriesDetails.info?.cover")
        adapter = SeasonAdapterOnTv(
                seasons, object : SeasonOnClick {
            override fun onClick(view: View, season: Season) {
                Glide.with(this@CustomSeriesDetailsActivity)
                        .load(season.cover)
                        .into(binding.seriesCover)
                fetchSeasonEpisodes(season.id ?: 0)
            }
        }
        )

        binding.tvSeriesSeasonsRv.layoutManager = LinearLayoutManager(this)
        binding.tvSeriesSeasonsRv.adapter = adapter

        binding.tvSeasonEpisodsRv.layoutManager = GridLayoutManager(this, 4)
        binding.tvSeasonEpisodsRv.adapter = episodeAdapter
    }


    private fun fetchSeasonEpisodes(seasonId: Int) {
        viewModel.getSeasonItems(seasonId).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    it?.let { resource ->
                        if (resource.data != null)
                            episodeAdapter.setItems(resource.data.data)
                    }

                }
                Status.ERROR -> {
                    Log.e("TAG", "onCreate: " + it.message)

                }
                Status.LOADING -> {
                }
            }
        }
    }
}