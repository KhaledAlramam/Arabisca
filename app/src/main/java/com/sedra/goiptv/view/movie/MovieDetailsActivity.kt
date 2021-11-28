package com.sedra.goiptv.view.movie

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.sedra.goiptv.R
import com.sedra.goiptv.data.db.FavouriteItem
import com.sedra.goiptv.data.model.MovieDetailsResponse
import com.sedra.goiptv.databinding.ActivityMovieDetailsBinding
import com.sedra.goiptv.utils.GoTo
import com.sedra.goiptv.utils.MOVIE_ID_PARAMETER
import com.sedra.goiptv.utils.PREF_APP_IMG
import com.sedra.goiptv.utils.Status.*
import com.sedra.goiptv.utils.TrailerDialog
import com.sedra.goiptv.view.FavouritesFragment
import com.sedra.goiptv.view.FavouritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToLong


@AndroidEntryPoint
class MovieDetailsActivity : AppCompatActivity() {

    private val viewModel by viewModels<MoviesViewModel>()
    lateinit var binding: ActivityMovieDetailsBinding

    @Inject
    lateinit var preferences: SharedPreferences
    val favouritesViewModel: FavouritesViewModel by viewModels()
    var isFavourite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details)
        val id = intent.getIntExtra(MOVIE_ID_PARAMETER, 0)
        val imageLink = preferences.getString(
            PREF_APP_IMG,
            ""
        )

        getMovieDetails(id)
    }

    fun findNb(m: Long): Long {
        // your code
        var sum: Long = 0
        var counter: Float =1.0f
        while(sum<=m){
            sum += counter.pow(3).toLong()
            if(sum == m) return counter.roundToLong()
            counter++
        }
        return (-1).toLong()
    }

    private fun getMovieDetails(id: Int) {
        viewModel.getMovieDetails(id).observe(this){
            it?.let { resource ->
                when(resource.status){
                    SUCCESS -> {
                        Log.e("TAG", "getMovieDetails: ${resource.data}")
                        resource.data?.let { movieDetailsResponse ->
                            updateUi(movieDetailsResponse)
                        }

                    }
                    ERROR -> {

                    }
                    LOADING -> {

                    }
                }
            }
        }
        fetchFavourites(id)

    }

    private fun updateUi(movieDetailsResponse: MovieDetailsResponse) {
        binding.apply {
            Glide.with(this@MovieDetailsActivity)
                .load(movieDetailsResponse.info.movie_image)
                .into(movieDetailsImage)
            Glide.with(this@MovieDetailsActivity)
                .load(movieDetailsResponse.info.movie_image)
                .into(binding.imageView13)
            playTrailer.setOnClickListener {
                val id = movieDetailsResponse.info.youtube_trailer
//                val webIntent =
//                    Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$id"))
//                val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"));
//                try {
//                    startActivity(appIntent)
//                } catch (ex: ActivityNotFoundException) {
//                    startActivity(webIntent)
//                }
                TrailerDialog(id, this@MovieDetailsActivity).show()
            }
            movieDetails = movieDetailsResponse
            binding!!.favourite.setOnClickListener {
                if (isFavourite) {
                    isFavourite = false
                    binding!!.favourite.setImageResource(R.drawable.ic_favorite)
                    removeFavourite(movieDetailsResponse)
                } else {
                    isFavourite = true
                    binding!!.favourite.setImageResource(R.drawable.ic_filled_favorite)
                    addMovieToFavourite(movieDetailsResponse)
                }
            }
            playMovie.setOnClickListener {
                GoTo.playMovieActivity(
                    this@MovieDetailsActivity,
                    movieDetailsResponse.movie_data.stream_id,
                    movieDetailsResponse.movie_data.container_extension
                )
            }
        }

    }


    private fun removeFavourite(movieDetails: MovieDetailsResponse?) {
        if (movieDetails == null) return
        val item = FavouriteItem(
            movieDetails.movie_data.stream_id ?: -1,
            movieDetails.movie_data.name ?: "",
            movieDetails.info.movie_image ?: "",
            FavouritesFragment.MOVIE_TYPE
        )
        Toast.makeText(this, "Item removed from favourite", Toast.LENGTH_SHORT).show()
        favouritesViewModel.removeFavourite(this@MovieDetailsActivity, item)
    }

    private fun fetchFavourites(intExtra: Int) {
        favouritesViewModel.allFavourites(this).observe(this) {
            if (it != null) {
                if (it.any { item -> item.id == intExtra }) {
                    binding!!.favourite.setImageResource(R.drawable.ic_filled_favorite)
                    isFavourite = true
                }
            }
        }
    }

    private fun addMovieToFavourite(movieDetails: MovieDetailsResponse?) {
        if (movieDetails == null) return
        val item = FavouriteItem(
            movieDetails.movie_data.stream_id ?: -1,
            movieDetails.movie_data.name ?: "",
            movieDetails.info.movie_image ?: "",
            FavouritesFragment.MOVIE_TYPE
        )
        favouritesViewModel.addFavourite(this, item)
        Toast.makeText(this, "Movie added to favourites", Toast.LENGTH_SHORT).show()
    }

}