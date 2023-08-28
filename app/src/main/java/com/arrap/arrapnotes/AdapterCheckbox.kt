package com.arrap.arrapnotes

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView

class AdapterCheckbox(private var itemList: MutableList<String>) : RecyclerView.Adapter<AdapterCheckbox.YourViewHolder>() {

    inner class YourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemview_layout,parent ,false)
        return  YourViewHolder(view)
    }

    override fun onBindViewHolder(holder: YourViewHolder, position: Int) {
        val item = itemList[position]
        holder.checkBox.isChecked = false // Cambia esto a true si tu lógica requiere que estén marcados por defecto

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val movedItem = itemList.removeAt(fromPosition)
        itemList.add(toPosition, movedItem)
        notifyItemMoved(fromPosition, toPosition)
    }
}
