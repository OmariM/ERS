package com.omari.ait.egyptianratscrew.adapters

import android.content.Context
import android.graphics.Color.rgb
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omari.ait.egyptianratscrew.R
import com.omari.ait.egyptianratscrew.models.Computer
import kotlinx.android.synthetic.main.cpu_info_item.view.*

class CPUInfoAdapter(val items: MutableList<Computer>, val context: Context) :
    RecyclerView.Adapter<CPUInfoAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CPUInfoAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.cpu_info_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCPU = items[position]
        val color = if (currentCPU.isMyTurn) rgb(23, 200, 45) else rgb(255, 255, 255)
        holder.tvCPUInfo.text = currentCPU.getInfo()
        holder.itemView.setBackgroundColor(color)
    }

    fun addItem(item: Computer) {
        items += item
        notifyItemInserted(items.lastIndex)
    }

    fun removeItem(item: Computer) {
        val indexOfRemovedItem = items.indexOf(item)
        items.removeAt(indexOfRemovedItem)
        notifyItemRemoved(indexOfRemovedItem)
    }

    fun updateItem(item: Computer) {
        val indexOfComputer = items.indexOf(item)
        notifyItemChanged(indexOfComputer)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCPUInfo = view.tvCPUInfo
    }
}