package com.b00957180.csci5409_ta.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.b00957180.csci5409_ta.R
import com.b00957180.csci5409_ta.model.FinalDataItem

class CommodityListAdapter(private var dataSet: List<FinalDataItem>, private val context: Context) :
    RecyclerView.Adapter<CommodityListAdapter.ViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    // The method that runs when the ViewHolder is created. Returns the inflated view
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_commodity_list, viewGroup, false)

        return ViewHolder(view, itemClickListener)
    }

    // A viewHolder to create the view for each list item
    class ViewHolder(view: View, private val itemClickListener: OnItemClickListener?) :
        RecyclerView.ViewHolder(view) {

        var commodityText: TextView
        val lowPriceText: TextView
        val highPriceText: TextView

        // Constructor
        init {
            commodityText = view.findViewById(R.id.commodity_name)
            lowPriceText = view.findViewById(R.id.commodity_low_price)
            highPriceText = view.findViewById(R.id.commodity_high_price)
        }
    }

    // Method when a view holder is bound. Here we specify the data to be displayed inside each viewHolder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val record = dataSet[position]

        viewHolder.commodityText.text = record.commodity
        viewHolder.lowPriceText.text = "LP: ${record.low_price}"
        viewHolder.highPriceText.text = "HP: ${record.high_price}"
    }

    override fun getItemCount() = dataSet.size

    fun setData(dataItems: List<FinalDataItem>) {
        this.dataSet = dataItems
    }
}