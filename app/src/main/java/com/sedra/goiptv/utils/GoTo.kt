package com.sedra.goiptv.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.sedra.goiptv.data.model.Category
import com.sedra.goiptv.view.channels.PlayChannelActivity
import com.sedra.goiptv.view.customsection.CustomSectionActivity
import com.sedra.goiptv.view.movie.MovieDetailsActivity
import com.sedra.goiptv.view.department.DepartmentActivity
import com.sedra.goiptv.view.movie.PlayMovieActivity
import com.sedra.goiptv.view.sections.MainActivity
import com.sedra.goiptv.view.series.SeriesDetailsForTv

object GoTo{
    fun goToMainActivity(activity: Activity){
        val i = Intent(activity, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(i)
    }
    fun goToDepartmentActivity(context: Context, id: Int, name: String){
        val i = Intent(context, DepartmentActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(EXTRA_TYPE_ID, id)
        i.putExtra(EXTRA_TYPE_NAME, name)
        context.startActivity(i)
    }

    fun goToCustomSectionActivity(context: Context, id: Int, name: String){
        val i = Intent(context, CustomSectionActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(EXTRA_TYPE_ID, id)
        i.putExtra(EXTRA_TYPE_NAME, name)
        context.startActivity(i)
    }
    fun goToMovieDetails(context: Context, id: Int){
        val i = Intent(context, MovieDetailsActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(MOVIE_ID_PARAMETER, id)
        context.startActivity(i)
    }
    fun playMovieActivity(context: Context, id: Int, containerExtension: String?){
        val i = Intent(context, PlayMovieActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(STREAM_ID_INTENT_EXTRA, id)
        i.putExtra(STREAM_EXT, containerExtension)
        context.startActivity(i)
    }

    fun playChannel(context: Context, id: Int, catList: ArrayList<Category>){
        val i = Intent(context, PlayChannelActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(STREAM_ID_INTENT_EXTRA, id)
        i.putExtra(CATEGORY_LIST_INTENT_EXTRA, catList)
        context.startActivity(i)
    }

    fun goToSeriesDetails(context: Context, seriesId: Int) {
        val i = Intent(context, SeriesDetailsForTv::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(SERIES_ID_PARAMETER, seriesId)
        context.startActivity(i)
    }
}