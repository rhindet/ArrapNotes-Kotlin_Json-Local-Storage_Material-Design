package com.arrap.arrapnotes

//Las funciones no tienen cuerpo pues se declara en las otras clases , el interface es como un contrato entre clases
interface OnItemClickListener {
    fun onItemClickListener(position:Int)
    fun onItemLongClickListener(position:Int)
}