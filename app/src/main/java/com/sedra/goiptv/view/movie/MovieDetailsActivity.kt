package com.sedra.goiptv.view.movie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.MovieDetailsResponse
import com.sedra.goiptv.databinding.ActivityMovieDetailsBinding
import com.sedra.goiptv.utils.GoTo
import com.sedra.goiptv.utils.MOVIE_ID_PARAMETER
import com.sedra.goiptv.utils.Status.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsActivity : AppCompatActivity() {

    private val viewModel by viewModels<MoviesViewModel>()
    var binding: ActivityMovieDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details)
        val id = intent.getIntExtra(MOVIE_ID_PARAMETER, 0)
        getMovieDetails(id)
    }

    private fun getMovieDetails(id: Int) {
        viewModel.getMovieDetails(id).observe(this){
            it?.let { resource ->
                when(resource.status){
                    SUCCESS -> {
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
    }

    private fun updateUi(movieDetailsResponse: MovieDetailsResponse) {
        binding?.apply {
            Glide.with(this@MovieDetailsActivity)
                    .load(movieDetailsResponse.info.movie_image)
                    .into(movieDetailsImage)
            movieDetails = movieDetailsResponse
            playMovie.setOnClickListener {
                GoTo.playMovieActivity(this@MovieDetailsActivity, movieDetailsResponse.movie_data.stream_id, movieDetailsResponse.movie_data.container_extension)
            }
        }

    }
}