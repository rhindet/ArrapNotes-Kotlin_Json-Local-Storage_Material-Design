package com.arrap.arrapnotes

import android.graphics.Color
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Notes(var fileTitle: String = "", var fileContent: String = "",  var color: String = "#d6aa44",  var creationDateString: String = getCurrentDateAsString()){
    var isChecked: Boolean = false

    // Agrega esta propiedad


    // Campo interno para el color en formato de entero
    private var colorValue: Int = Color.parseColor(color)

    // Getter para acceder al valor de color como cadena
    fun getColorHex(): String {
        return color
    }

    // Getter para acceder al valor de color como entero
    fun getColorInt(): Int {
        return colorValue
    }

    // Setter para actualizar el color en formato de cadena
    fun setColorHex(hexColor: String) {
        color = hexColor
        colorValue = Color.parseColor(hexColor)
    }

    // Método toString personalizado
    fun toString2(): String {
        return "Notes(fileTitle='$fileTitle', fileContent='$fileContent', isChecked=$isChecked)"
    }
    companion object {
        private fun getCurrentDateAsString(): String {
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            return currentDate.format(formatter)
        }
    }
}
