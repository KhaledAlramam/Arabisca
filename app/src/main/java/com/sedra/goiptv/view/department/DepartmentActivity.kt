package com.sedra.goiptv.view.department

import android.app.AlertDialog
import android.app.UiModeManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.*
import com.sedra.goiptv.databinding.ActivityDepartmentBinding
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.utils.Status.*
import com.sedra.goiptv.view.channels.PlayChannelsNewActivity
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import javax.inject.Inject

@AndroidEntryPoint
class DepartmentActivity : AppCompatActivity() {

    private val viewModel: CategoryViewModel by viewModels()
    lateinit var binding: ActivityDepartmentBinding
    private val moviesList = ArrayList<Movie>()
    private val seriesList = ArrayList<Series>()
    private val channelList = ArrayList<LiveStream>()
    private val catList = ArrayList<Category>()
    private val gridAdapter = MovieAdapter()
    private val gridSeriesAdapter = SeriesAdapter()
    var progressDialog: AlertDialog? = null
    @Inject
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_department)
        progressDialog = SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait...")
                .setCancelable(false)
                .setTheme(R.style.CustomProgressDialogTheme)
                .build()
        val imageLink =  preferences.getString(
            PREF_APP_IMG,
            ""
        )
        Glide.with(this)
            .load(imageLink)
            .into(binding.imageView12)

        when (intent.getIntExtra(EXTRA_TYPE_ID, 0)) {
            MOVIES_ID -> {
                getMoviesData()
                binding.categoryName = getString(R.string.movies_)
            }
            SERIES_ID -> {
                getSeriesData()
                binding.categoryName = getString(R.string.series_)

            }else -> {
                finish()
            }
        }
    }

    private fun getSeriesData() {
        viewModel.getSeriesCategories().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        progressDialog?.dismiss()
                        if (resource.data != null)
                            getAllSeries(resource.data)
                    }
                    ERROR -> {
                        progressDialog?.dismiss()
                    }
                    LOADING -> {
                        progressDialog?.show()
                    }
                }
            }
        }
    }

    private fun getAllSeries(data: List<Category>) {
        viewModel.getAllSeries().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        progressDialog?.dismiss()
                        if (resource.data != null)
                            manipulateSeriesData(data, resource.data)
                    }
                    ERROR -> {

                        progressDialog?.dismiss()
                    }
                    LOADING -> {

                        progressDialog?.show()
                    }
                }
            }

        }
    }

    private fun manipulateSeriesData(categories: List<Category>, list: List<Series>) {
        seriesList.clear()
        seriesList.addAll(list)
        categories.forEach { loopedCategory ->
            loopedCategory.series = list
                    .filter { series -> series.category_id == loopedCategory.category_id }
            catList.add(loopedCategory)
        }
        val departmentTitleAdapter = DepartmentTitleAdapter(categories, object : OnDepartmentClicked {
            override fun onClick(view: View, position: Int) {
                gridSeriesAdapter.submitList(categories[position].series)
            }
        })
        binding.departmentTitleRv.apply {
            adapter = departmentTitleAdapter
            layoutManager = LinearLayoutManager(this@DepartmentActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        binding.itemsRv.apply {
            adapter = gridSeriesAdapter
            layoutManager = if (checkTv()) {
                GridLayoutManager(this@DepartmentActivity, 2, LinearLayoutManager.HORIZONTAL, false)
            } else {
                GridLayoutManager(this@DepartmentActivity, 2, LinearLayoutManager.HORIZONTAL, false)
            }
        }
        gridSeriesAdapter.submitList(categories[0].series)
    }

    private fun getMoviesData() {
        viewModel.getMoviesCategories().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        progressDialog?.dismiss()
                        if (resource.data != null)
                            getAllMovies(resource.data)
                    }
                    ERROR -> {
                        progressDialog?.dismiss()
                    }
                    LOADING -> {
                        progressDialog?.show()
                    }
                }
            }
        }
    }

    private fun getAllMovies(categories: List<Category>) {
        viewModel.getAllMovies().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        progressDialog?.dismiss()
                        if (resource.data != null)
                            manipulateData(categories, resource.data)
                    }
                    ERROR -> {
                        progressDialog?.dismiss()

                    }
                    LOADING -> {

                        progressDialog?.show()
                    }
                }
            }

        }
    }

    private fun manipulateData(categories: List<Category>, data: List<Movie>) {
        moviesList.clear()
        moviesList.addAll(data)
        categories.forEach { loopedCategory ->
            loopedCategory.movies = data
                    .filter { movie -> movie.category_id == loopedCategory.category_id }
            catList.add(loopedCategory)
        }
        val departmentTitleAdapter = DepartmentTitleAdapter(categories, object : OnDepartmentClicked {
            override fun onClick(view: View, position: Int) {
                gridAdapter.submitList(categories[position].movies)
            }
        })
        binding.departmentTitleRv.apply {
            adapter = departmentTitleAdapter
            layoutManager = LinearLayoutManager(this@DepartmentActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        binding.itemsRv.apply {
            adapter = gridAdapter
            layoutManager = if (checkTv()) {
                GridLayoutManager(this@DepartmentActivity, 2, LinearLayoutManager.HORIZONTAL, false)
            } else {
                GridLayoutManager(this@DepartmentActivity, 2, LinearLayoutManager.HORIZONTAL, false)
            }
        }
        gridAdapter.submitList(categories[0].movies)
    }

    companion object {
        const val MOVIES_ID = -1
        const val SERIES_ID = -2
        const val CHANNELS_ID = -3
        const val RADIO_ID = -4
    }

    private fun checkTv(): Boolean {
        var isAndroidTv = false
        if ((getSystemService(UI_MODE_SERVICE) as UiModeManager).currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            isAndroidTv = true
        } else if (packageManager!!
                        .hasSystemFeature(PackageManager.FEATURE_TELEVISION) || packageManager!!
                        .hasSystemFeature(PackageManager.FEATURE_LEANBACK)
        ) {
            isAndroidTv = true
        }

        return isAndroidTv
    }

}