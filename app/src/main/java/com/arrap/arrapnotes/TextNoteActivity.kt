package com.arrap.arrapnotes

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class TextNoteActivity : AppCompatActivity() {

    private lateinit var arrowBack : ImageView
    private var savedTextTitle: String = ""
    private var savedTextContent: String = ""

    private lateinit var fechaTextView: TextView
    private  var fecha: String? = null

    private lateinit var  textInputLayout: TextInputLayout
    private lateinit var textInputEditText: TextInputEditText

    private  var  fileTitle : String? = null
    private  var  fileContent : String? = null
    private  var  fileColor : String? = null

    private  var color : String = "#d6aa44"
    private lateinit var btnColor :ImageView

    private lateinit var noteEditText:EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_note)

        initializeIDS()
        initializeListeners()
        initializeObjects()

    }

    //Inicialiar ids
    private fun initializeIDS(){
        fechaTextView = findViewById(R.id.fechaTextView)
        textInputLayout = findViewById(R.id.textInputLayout)
        textInputEditText = findViewById(R.id.textInputEditText)
        noteEditText = findViewById(R.id.noteEditText)
        btnColor = findViewById(R.id.btnColor)

    }

    //Inicializar listeners
    private fun initializeListeners(){
        textInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                savedTextTitle = charSequence.toString()

            }

            override fun afterTextChanged(editable: Editable) {}
        })

        arrowBack = findViewById(R.id.arrowBack)
        arrowBack.setOnClickListener {
            onBackPressed()
        }

        btnColor.setOnClickListener {
           when(color){
               "#d6aa44"->  {
                   btnColor.setImageResource(R.drawable.circle_red)
                   color = "#FF0000"
               }
              "#FF0000" -> {
                   btnColor.setImageResource(R.drawable.rounded_background_blue)
                   color = "#0055FF"
               }
               "#0055FF"->{
                   btnColor.setImageResource(R.drawable.rounded_background_green)
                   color = "#039617"
               }
               "#039617" -> {
                   btnColor.setImageResource(R.drawable.fab_background)
                   color = "#d6aa44"
               }
           }
            println(color)

        }

        val editText = findViewById<EditText>(R.id.noteEditText)
        editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                savedTextContent = p0.toString()

            }

            override fun afterTextChanged(p0: Editable?) {}

        })
    }
    private fun getCurrentDateAsString(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        return currentDateTime.format(formatter)
    }

    //Inicializar objetos
    private fun initializeObjects(){


        //Revise por informacion enviada
        fileTitle = intent.getStringExtra("fileTitle")
        fileContent = intent.getStringExtra("fileContent")
        fileColor = intent.getStringExtra("color")
        fecha = intent.getStringExtra("fecha") ?: getCurrentDateAsString()

        if(!fileTitle.isNullOrEmpty() || !fileContent.isNullOrEmpty()){
            savedTextContent = fileContent.toString()
            savedTextTitle = fileTitle.toString()
            color = fileColor.toString()
            textInputEditText.setText(fileTitle)
            noteEditText.setText(fileContent)
            fechaTextView.text = fecha

            when(color){
                "#d6aa44"->  {
                    btnColor.setImageResource(R.drawable.fab_background)

                }
                "#FF0000" -> {
                    btnColor.setImageResource(R.drawable.circle_red)

                }
                "#0055FF"->{
                    btnColor.setImageResource(R.drawable.rounded_background_blue)

                }
                "#039617" -> {
                    btnColor.setImageResource(R.drawable.rounded_background_green)

                }


            }


        }else{
            fechaTextView.text = fecha
        }
    }

    //Retroseso
    override fun onBackPressed() {
        super.onBackPressed()


        guardarNota()

        val intent = Intent(this, MainActivity::class.java)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
        startActivity(intent, options.toBundle())

    }

    //Guardar Nota
    private fun guardarNota() {
        if (!savedTextContent.isNullOrEmpty() || !savedTextTitle.isNullOrEmpty()) {
            val gson = Gson()

            val note = Notes(savedTextTitle, savedTextContent,color,fecha.toString())


            val existingNotes = loadNotesFromJson()

            // Buscar la nota en existingNotes que coincida con fileTitle y actualizarla
            var noteUpdated = false
            for (existingNote in existingNotes) {
                if (existingNote.fileTitle == fileTitle) {
                    existingNote.fileTitle = savedTextTitle
                    existingNote.fileContent = savedTextContent
                    existingNote.color = color

                    noteUpdated = true
                    break
                }
            }

            // Si la nota no fue actualizada, agregarla a la lista
            if (!noteUpdated) {
                existingNotes.add(note)
            }

            val jsonString = gson.toJson(existingNotes)
            val fileName = "notes.json"
            val fileContents = jsonString

            try {
                openFileOutput(fileName, Context.MODE_PRIVATE).use {
                    it.write(fileContents.toByteArray())
                }
                printFileContents(fileName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //Cargar data anterior de las notas del json
    private fun loadNotesFromJson(): ArrayList<Notes> {
        val fileName = "notes.json"
        var existingNotes: ArrayList<Notes>

        try {
            val inputStream = openFileInput(fileName)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val stringBuilder = StringBuilder()
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = bufferedReader.readLine()
            }

            val gson = Gson()
            existingNotes = gson.fromJson(stringBuilder.toString(), object : TypeToken<ArrayList<Notes>>() {}.type)
            inputStream.close()

        } catch (e: FileNotFoundException) {
            existingNotes = ArrayList()
        }

        return existingNotes
    }

    //Imprimir datos del json en consola
    private fun printFileContents(fileName: String) {
        try {
            val inputStream = openFileInput(fileName)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val stringBuilder = StringBuilder()
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = bufferedReader.readLine()
            }

            val fileContents = stringBuilder.toString()
            Log.d("FileContents", fileContents) // Imprimir en Logcat

            inputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}