package com.sedra.goiptv.view.series

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sedra.goiptv.*
import com.sedra.goiptv.data.model.UserInfo
import com.sedra.goiptv.data.model.series_models.Episode
import com.sedra.goiptv.data.model.series_models.Season
import com.sedra.goiptv.databinding.ActivitySeriesDetailsForTvBinding
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.data.model.series_models.SeriesDetailsResponse
import com.sedra.goiptv.utils.SERIES_ID_PARAMETER
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SeriesDetailsForTv : AppCompatActivity() {

    private val viewModel: SeriesViewModel by viewModels()
    lateinit var binding: ActivitySeriesDetailsForTvBinding
    lateinit var adapter: SeasonAdapterOnTv
    @Inject
    lateinit var userInfo: UserInfo

    var isFavourite = false
    var seriesDetails: SeriesDetailsResponse? = null
//    val favouritesViewModel: FavouritesViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_series_details_for_tv)
//        fetchFavourites(intent.getIntExtra(SERIES_ID_PARAMETER, 0))
        fetchSeriesDetails()
//        binding.addItemToFavouriteTv.setOnClickListener {
//            if (isFavourite) {
//                isFavourite = false
//                binding.addItemToFavouriteTv.text = getString(R.string.add_to_favourite)
//                removeFavourite(seriesDetails)
//            } else {
//                isFavourite = true
//                binding.addItemToFavouriteTv.text = getString(R.string.remove_from_fav)
//                addSeriesToFavourite(seriesDetails)
//            }
//
//        }
        binding.back.setOnClickListener {
            finish()
        }
    }

//    private fun fetchFavourites(intExtra: Int) {
//        favouritesViewModel.allFavourites(this).observe(this) {
//            if (it != null) {
//                if (it.any { item -> item.id == intExtra }) {
//                    binding.addItemToFavouriteTv.text = getString(R.string.remove_from_fav)
//                    isFavourite = true
//                }
//            }
//        }
//    }

//    private fun removeFavourite(series: SeriesDetailsResponse?) {
//        if (series == null) return
//        val item = FavouriteItem(
//                intent.getIntExtra(SERIES_ID_PARAMETER, -1),
//                series.info?.name ?: "",
//                series.info?.cover ?: "",
//                FavouritesFragment.SERIES_TYPE
//        )
//        Toast.makeText(this, "Item removed from favourite", Toast.LENGTH_SHORT).show()
//        favouritesViewModel.removeFavourite(this@SeriesDetailsForTv, item)
//    }
//
//    private fun addSeriesToFavourite(series: SeriesDetailsResponse?) {
//        if (series == null) return
//        val item = FavouriteItem(
//                intent.getIntExtra(SERIES_ID_PARAMETER, -1),
//                series.info?.name ?: "",
//                series.info?.cover ?: "",
//                FavouritesFragment.SERIES_TYPE
//        )
//        favouritesViewModel.addFavourite(this, item)
//        Toast.makeText(this, "Series added to favourites", Toast.LENGTH_SHORT).show()
//    }

    private fun fetchSeriesDetails() {
//        if (userInfo.username == null) return
        viewModel.getSeriesDetails(
                userInfo.username!!,
                userInfo.password!!,
            intent.getIntExtra(SERIES_ID_PARAMETER, 0)
        ).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    it?.let { resource ->
                        binding!!.seriesData = resource.data?.info
                        Glide.with(this)
                            .load(resource.data?.info?.cover)
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

    private fun updateUI(seriesDetails: SeriesDetailsResponse) {
//        val seasons =  seriesDetails.seasons!!.filter { !it.name?.contains("Specials",true)!! }
        val seasons = seriesDetails.episodes!!.keys.map {
            Season().apply {
                name = it
            }
        }
        val episodeAdapter =
            EpisodeAdapter(emptyList(), seriesDetails.info?.cover, object : EpisodeOnClick {
                override fun onClick(view: View, episode: Episode) {
                    val intent = Intent(this@SeriesDetailsForTv, PlaySeriesActivity::class.java)
                    intent.putExtra(STREAM_ID_INTENT_EXTRA, episode.id)
                    intent.putExtra(STREAM_EXT, episode.containerExtension)
                    startActivity(intent)
                }
            })
        adapter = SeasonAdapterOnTv(
            seasons, object : SeasonOnClick {
                override fun onClick(view: View, season: Season) {
                    episodeAdapter.setItems(
                        seriesDetails.episodes!!.values.elementAt(
                            seasons.indexOf(
                                season
                            )
                        )
                    )
                    Glide.with(this@SeriesDetailsForTv)
                        .load(season.cover)
                        .into(binding.seriesCover)
                }
            }
        )

        binding.tvSeriesSeasonsRv.layoutManager = LinearLayoutManager(this)
        binding.tvSeriesSeasonsRv.adapter = adapter

        binding.tvSeasonEpisodsRv.layoutManager = GridLayoutManager(this, 4)
        binding.tvSeasonEpisodsRv.adapter = episodeAdapter

    }
}