package com.sedra.goiptv.view.department

import android.app.UiModeManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.sedra.goiptv.R
import com.sedra.goiptv.data.db.FavouriteItem
import com.sedra.goiptv.data.model.*
import com.sedra.goiptv.databinding.FragmentFavouritesBinding
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.utils.Status.*
import com.sedra.goiptv.view.FavouritesAdapter
import com.sedra.goiptv.view.FavouritesFragment
import com.sedra.goiptv.view.FavouritesViewModel
import com.sedra.goiptv.view.channels.PlayChannelsNewActivity
import com.sedra.goiptv.view.movie.MovieDetailsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesActivity : AppCompatActivity() {

    var binding: FragmentFavouritesBinding? = null
    lateinit var adapter: FavouritesAdapter
    private val viewModel: FavouritesViewModel by viewModels()
    var query = ""
    val favs = ArrayList<FavouriteItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_favourites)
//        if ((getSystemService(AppCompatActivity.UI_MODE_SERVICE) as UiModeManager).currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
        binding!!.favouritesRv.layoutManager = GridLayoutManager(this, 4)
//        } else {
//            binding!!.favouritesRv.layoutManager = GridLayoutManager(this, 2)
//        }
        adapter = FavouritesAdapter(this, emptyList(), object : FavouriteItemOnClick {
            override fun onClick(item: FavouriteItem, view: View) {
                when (item.type) {
                    MOVIE_TYPE -> {
                        val intent =
                            Intent(this@FavouritesActivity, MovieDetailsActivity::class.java)
                        intent.putExtra(MOVIE_ID_PARAMETER, item.id)
                        startActivity(intent)
                    }
                    SERIES_TYPE -> {
                        GoTo.goToSeriesDetails(this@FavouritesActivity, item.id)
                    }
                    CHANNEL_TYPE -> {
                        val intent =
                            Intent(this@FavouritesActivity, PlayChannelsNewActivity::class.java)
                        intent.putExtra(STREAM_ID_INTENT_EXTRA, item.id)
//                        intent.putExtra(CATEGORY_LIST_INTENT_EXTRA, ArrayList<Category>())
                        startActivity(intent)
                    }
                }
            }
        })
        binding!!.favouritesRv.adapter = adapter
        fetchFavourites()
    }


    private fun fetchFavourites() {
        viewModel.allFavourites(this).observe(this) {
            if (it != null) {
                favs.clear()
                favs.addAll(it)
                adapter.setItems(it)
            }
        }
    }

//    private fun showSearchDialog() {
//        val myDialog = this?.let { Dialog(it) }
//        myDialog?.setContentView(R.layout.dialog_search)
//        myDialog?.setCancelable(true)
//        myDialog?.setCanceledOnTouchOutside(true)
//        myDialog?.findViewById<ImageView>(R.id.closeSearchDialog)?.setOnClickListener {
//            myDialog.dismiss()
//            myDialog.cancel()
//        }
//        myDialog?.findViewById<EditText>(R.id.searchEt)?.setText(query)
//        myDialog?.findViewById<EditText>(R.id.searchEt)?.addTextChangedListener(object :
//            TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                query = s.toString()
//                filter(query)
//            }
//        })
//        myDialog?.show()
//        val window = myDialog?.window
//        val wlp = window!!.attributes
//
//        wlp.gravity = Gravity.TOP
//        window.attributes = wlp
//        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//
//        val back = ColorDrawable(Color.TRANSPARENT)
//        val inset = InsetDrawable(back, 20)
//        window.setBackgroundDrawable(inset)
//
//    }

    private fun filter(searchText: String) {
        if (searchText.isBlank()) {
            adapter.setItems(favs)
            return
        }
        val filteredFavs = favs.filter { favourite ->
            favourite.title.replace("-", " ").replace("_", " ").contains(searchText, true)
        }

        adapter.setItems(filteredFavs)
    }


    private fun checkTv(): Boolean {
        var isAndroidTv = false
        if ((this?.getSystemService(AppCompatActivity.UI_MODE_SERVICE) as UiModeManager).currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            isAndroidTv = true
        } else if (this?.packageManager!!
                .hasSystemFeature(PackageManager.FEATURE_TELEVISION) || this?.packageManager!!
                .hasSystemFeature(PackageManager.FEATURE_LEANBACK)
        ) {
            isAndroidTv = true
        }

        return isAndroidTv
    }

    companion object {
        const val MOVIE_TYPE = 0
        const val SERIES_TYPE = 1
        const val CHANNEL_TYPE = 2

        @JvmStatic
        fun newInstance() =
            FavouritesFragment()
    }

}