package com.sedra.goiptv.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.sedra.goiptv.CatchUpActivity
import com.sedra.goiptv.UpdateContentActivity
import com.sedra.goiptv.data.model.Category
import com.sedra.goiptv.view.channels.PlayChannelsNewActivity
import com.sedra.goiptv.view.customsection.CustomSectionActivity
import com.sedra.goiptv.view.customsection.CustomSeriesActivity
import com.sedra.goiptv.view.customsection.CustomSeriesDetailsActivity
import com.sedra.goiptv.view.customsection.PlayCustomChannelsActivity
import com.sedra.goiptv.view.department.DepartmentActivity
import com.sedra.goiptv.view.department.FavouritesActivity
import com.sedra.goiptv.view.movie.MovieDetailsActivity
import com.sedra.goiptv.view.movie.PlayMovieActivity
import com.sedra.goiptv.view.radio.RadioActivity
import com.sedra.goiptv.view.sections.MainActivity
import com.sedra.goiptv.view.series.SeriesDetailsForTv
import com.sedra.goiptv.view.settings.SettingsActivity

object GoTo {
    fun goToMainActivity(activity: Activity) {
        val i = Intent(activity, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(i)
    }

    fun goToUpdateContent(activity: Activity) {
        val i = Intent(activity, UpdateContentActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(i)
    }

    fun goToSettings(activity: Context) {
        val i = Intent(activity, SettingsActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(i)
    }

    fun catchUp(context: Context) {
        val i = Intent(context, CatchUpActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(i)
    }

    fun favourites(context: Context) {
        val i = Intent(context, FavouritesActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(i)
    }

    fun goToDepartmentActivity(context: Context, id: Int, name: String) {
        val i = Intent(context, DepartmentActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(EXTRA_TYPE_ID, id)
        i.putExtra(EXTRA_TYPE_NAME, name)
        context.startActivity(i)
    }

    fun goToPlayChannelActivity(context: Context) {
        val i = Intent(context, PlayChannelsNewActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(i)
    }
    fun goToRadioActivity(context: Context) {
        val i = Intent(context, RadioActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(i)
    }

    fun goToPlayCustomChannelActivity(context: Context, id: Int) {
        val i = Intent(context, PlayCustomChannelsActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(EXTRA_TYPE_ID, id)
        context.startActivity(i)
    }

    fun goToCustomSectionActivity(context: Context, id: Int, name: String) {
        val i = Intent(context, CustomSectionActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(EXTRA_TYPE_ID, id)
        i.putExtra(EXTRA_TYPE_NAME, name)
        context.startActivity(i)
    }

    fun goToCustomSeriesActivity(context: Context, id: Int) {
        val i = Intent(context, CustomSeriesActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(EXTRA_TYPE_ID, id)
        context.startActivity(i)
    }

    fun goToCustomSeriesDetailsActivity(context: Context, id: Int, name: String) {
        val i = Intent(context, CustomSeriesDetailsActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(SERIES_ID_PARAMETER, id)
        i.putExtra(SERIES_NAME, name)
        context.startActivity(i)
    }

    fun goToMovieDetails(context: Context, id: Int) {
        val i = Intent(context, MovieDetailsActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(MOVIE_ID_PARAMETER, id)
        context.startActivity(i)
    }

    fun playMovieActivity(context: Context, id: Int, containerExtension: String?) {
        val i = Intent(context, PlayMovieActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(STREAM_ID_INTENT_EXTRA, id)
        i.putExtra(STREAM_EXT, containerExtension)
        context.startActivity(i)
    }

    fun playChannel(context: Context, id: Int, streamIcon: String, catList: ArrayList<Category>) {
        val i = Intent(context, PlayChannelsNewActivity::class.java)
//        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(STREAM_ID_INTENT_EXTRA, id)
        i.putExtra(STREAM_IMG, streamIcon)
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