package com.omari.ait.egyptianratscrew.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omari.ait.egyptianratscrew.R
import com.omari.ait.egyptianratscrew.models.Computer
import com.omari.ait.egyptianratscrew.models.Player
import kotlinx.android.synthetic.main.cpu_info_item.view.*

class CPUInfoAdapter(val items: MutableList<Computer>, val context: Context) : RecyclerView.Adapter<CPUInfoAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CPUInfoAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.cpu_info_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCPU = items[position]
        holder.tvCPUInfo.text = currentCPU.getInfo()
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

    fun updateItem(item : Computer) {
        val indexOfComputer = items.indexOf(item)
        notifyItemChanged(indexOfComputer)
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val tvCPUInfo = view.tvCPUInfo
    }
}