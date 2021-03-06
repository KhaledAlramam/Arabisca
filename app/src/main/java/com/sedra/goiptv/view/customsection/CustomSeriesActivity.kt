package com.sedra.goiptv.view.customsection

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.*
import com.sedra.goiptv.databinding.ActivityDepartmentBinding
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.utils.Status.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomSeriesActivity : AppCompatActivity() {

    var binding: ActivityDepartmentBinding? = null
    val viewModel: SubSectionsViewModel by viewModels()
    private val itemsHashMap = HashMap<Int, List<CustomItem>>()
    private lateinit var itemsAdapter: ItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_department)
        itemsAdapter = ItemsAdapter(object : PositionOnClick {
            override fun onClick(view: View, position: Int) {
                GoTo.goToCustomSeriesDetailsActivity(
                    this@CustomSeriesActivity,
                    itemsAdapter.currentList[position].id,
                    itemsAdapter.currentList[position].name,
                )
            }
        })
        getSubSections()
    }


    private fun getSubSections() {
        viewModel.getSubSections(intent.getIntExtra(EXTRA_TYPE_ID, 0)).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        if (resource.data != null)
                            manipulateData(resource.data)
                    }
                    ERROR -> {
                    }
                    LOADING -> {
                    }
                }
            }
        }
    }

    private fun getItems(subSectionId: Int) {
        viewModel.getSeriesFromSubSections(subSectionId).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        if (resource.data != null) {
                            itemsHashMap[subSectionId] = resource.data.data
                            showItems(resource.data.data)
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

    fun showItems(items: List<CustomItem>) {
        itemsAdapter.submitList(items)
    }


    private fun manipulateData(response: SubSectionsResponse) {
        val departmentTitleAdapter = CustomSectionTitleAdapter(response.data, object : OnDepartmentClicked {
            override fun onClick(view: View, position: Int) {
                val id = response.data[position].id
                if (itemsHashMap.keys.contains(id)) {
                    showItems(itemsHashMap[id]!!)
                } else {
                    getItems(id)
                }
            }
        })
        binding?.departmentTitleRv?.apply {
            adapter = departmentTitleAdapter
            layoutManager = LinearLayoutManager(this@CustomSeriesActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        binding?.itemsRv?.apply {
            adapter = itemsAdapter
            layoutManager = GridLayoutManager(this@CustomSeriesActivity, 2, LinearLayoutManager.HORIZONTAL, false)
        }
        getItems(response.data.first().id)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}