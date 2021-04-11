package com.sedra.goiptv.view.customsection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
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
class CustomSectionActivity : AppCompatActivity() {

    var binding: ActivityDepartmentBinding? = null
    val viewModel: SubSectionsViewModel by viewModels()
    private val itemsHashMap = HashMap<Int, List<CustomItem>>()
    val itemsAdapter = ItemsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_department)
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
        viewModel.getItems(subSectionId).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        if (resource.data != null){
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

    fun showItems(items: List<CustomItem>){
        itemsAdapter.submitList(items)
    }


    private fun manipulateData(response: SubSectionsResponse) {
        val departmentTitleAdapter = CustomSectionTitleAdapter(response.data, object : OnDepartmentClicked {
            override fun onClick(view: View, position: Int) {
                val id = response.data[position].id
                if (itemsHashMap.keys.contains(id)){
                    showItems(itemsHashMap[id]!!)
                }else{
                    getItems(id)
                }
            }
        })
        binding?.departmentTitleRv?.apply {
            adapter = departmentTitleAdapter
            layoutManager = LinearLayoutManager(this@CustomSectionActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        binding?.itemsRv?.apply {
            adapter = itemsAdapter
            layoutManager = GridLayoutManager(this@CustomSectionActivity, 2, LinearLayoutManager.HORIZONTAL, false)
        }
        getItems(response.data.first().id)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}