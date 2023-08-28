package com.arrap.arrapnotes

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


class Adapter(var notes : ArrayList<Notes>,var listener: OnItemClickListener) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    private var editMode = false

    fun isEditMode():Boolean{return editMode}
    fun setEditMode(mode:Boolean){
        if(editMode != mode){
            editMode = mode
            notifyDataSetChanged()
        }
    }


    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView),View.OnClickListener,View.OnLongClickListener{
        var tvFilename : TextView = itemView.findViewById(R.id.RVFiletitle)
        var tvContent: TextView = itemView.findViewById(R.id.RVFilContent)
        var checkbox : CheckBox = itemView.findViewById(R.id.checkbox)
        var TVFecha : TextView = itemView.findViewById(R.id.TVFecha)
        var color : LinearLayout = itemView.findViewById(R.id.colorLayout)
        init{
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        //Cunado se le de click llamara a la funcion  onClickListener del mainActivity
        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClickListener(position)
            }
        }
        //Cunado se le de un long click llamara a la funcion  onLongClickListener del mainActivity
        override fun onLongClick(p0: View?): Boolean {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemLongClickListener(position)
            }
            return true
        }
    }

    //se invoca por el RV crea un nuevo ViewHolder pero no llena los datos
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemview_layout,parent ,false)
        return  ViewHolder(view)
    }

    //El RV lo llama para asociar un Viewholder con una instancia de datos y llena  cada campo
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position != RecyclerView.NO_POSITION){
            var nota = notes[position]
            holder.tvFilename.text = nota.fileTitle
            holder.tvContent.text = nota.fileContent

            // Convertir el valor hexadecimal en un objeto de color
            val colorValue = Color.parseColor(nota.color)

            // Crear un objeto ColorDrawable con el valor de color
            val colorDrawable = ColorDrawable(colorValue)

            when(nota.color){
                "#d6aa44"->  {
                    holder.color.setBackgroundResource(R.drawable.rounded_background_color)

                }
                "#FF0000" -> {
                    holder.color.setBackgroundResource(R.drawable.rounded_background_red)

                }
                "#0055FF"->{
                    holder.color.setBackgroundResource(R.drawable.rounded_background_blue)
                }
                "#039617" -> {
                    holder.color.setBackgroundResource(R.drawable.rounded_background_green)
                }
            }

            holder.TVFecha.text = nota.creationDateString

            if(editMode){

                holder.checkbox.visibility = View.VISIBLE
                holder.checkbox.isChecked = nota.isChecked
            }else{
                holder.checkbox.visibility = View.GONE
                holder.checkbox.isChecked = false
            }
        }
    }

    //Regresa el numero de elementos de elementos en el RV y se usa para determinar cuando se detiene la muestra de los datos
    override fun getItemCount(): Int {
        return notes.size
    }






}