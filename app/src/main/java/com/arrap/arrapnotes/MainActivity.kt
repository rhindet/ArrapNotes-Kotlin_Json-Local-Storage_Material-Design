package com.arrap.arrapnotes

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity(),OnItemClickListener {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var btnTextBS: MaterialButton
    private lateinit var btnChecklistBS: MaterialButton
    private lateinit var bottomSheetBG : View
    private lateinit var recyclerview : RecyclerView

    private lateinit var bottomSheetBehavior2: BottomSheetBehavior<LinearLayout>
    private var allCkecked = false



    private lateinit var fab : FloatingActionButton

    private lateinit var toolbar:MaterialToolbar

    private lateinit var notes :ArrayList<Notes>
    private lateinit var mAdapter : Adapter
    private lateinit var filteredNotesArray: ArrayList<Notes>

    private lateinit var  editBar : View

    private var controlDelete : Boolean = false
    private lateinit var imagenFondo : AppCompatImageView

    private lateinit var  btnSelectAll : ImageButton
    private lateinit var bottomSheet : LinearLayout
    private  lateinit var  btnRename :ImageButton
    private  lateinit var  btnDelete:ImageButton
    private  lateinit var  colorLayout:Layout
    private  lateinit var  tvDelete:TextView
    private  lateinit var  tvRename:TextView

    private lateinit var searchInput : TextInputEditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        inicializeIds()
        initializeObjects()
        inicializeListeners()
        searchInput = findViewById(R.id.search_input)

        searchInput.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var query = p0.toString()
                searchDatabase(query)

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        hideBotomSheet()

    }

    private fun searchDatabase(query: String){
        if (query.isEmpty()) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(searchInput.windowToken, 0)
            filteredNotesArray.clear()
            filteredNotesArray.addAll(notes) // Restaurar las notas originales
            mAdapter.notifyDataSetChanged()
        } else {
            val filteredNotes = notes.filter { note ->
                note.fileTitle.toLowerCase().contains(query.toLowerCase())
            }
            controlDelete = true
            filteredNotesArray.clear()
            filteredNotesArray.addAll(filteredNotes)
            printFilteredNotesArray()
            printFilteredNotesArray2()
            mAdapter.notifyDataSetChanged()
        }

    }

    fun printFilteredNotesArray2() {
        Log.d("NotasArray", "Contenido de NotasArray:")
        for (note in notes) {
            Log.d("NotasArray", "Título: ${note.fileTitle}, Contenido: ${note.fileContent}, isChecked: ${note.isChecked}")
        }
    }

    //Inicializar objetos
    private fun initializeObjects(){
        notes = ArrayList()
        filteredNotesArray = ArrayList()
        //Para que el adapter este conciente del interface agragar this (que es OnItemClick Listener)
        mAdapter = Adapter(filteredNotesArray,this)

        bottomSheetBehavior2 = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN

        setSupportActionBar(toolbar)
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener{
            onBackPressed()
        }

        //Inflar y actualizar items
        GlobalScope.launch {
            notes.clear()
            loadNotesFromJsonAndRefreshList()
            mAdapter.notifyDataSetChanged()
        }
        imagenFondo = findViewById(R.id.imageView2)



        //Inicializar el recylerview
        recyclerview.apply{
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    //Guardar notas a json
    private fun saveNotesToJson() {
        val gson = Gson()
        val jsonString = gson.toJson(notes)
        val fileName = "notes.json"
        val fileContents = jsonString

        try {
            openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(fileContents.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Inicializar Listeners
    private fun inicializeListeners() {
        bottomSheetBG.setOnClickListener {
            hideBotomSheet()
        }

        btnTextBS.setOnClickListener{

            hideBotomSheet()
            var intent = Intent(this,TextNoteActivity::class.java)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
            startActivity(intent, options.toBundle())

        }

        btnChecklistBS.setOnClickListener{
            var intent = Intent(this,CheckboxActivity::class.java)
            startActivity(intent)
            hideBotomSheet()
        }

        btnSelectAll.setOnClickListener {
            allCkecked = !allCkecked
            filteredNotesArray.map{it.isChecked = allCkecked}
            mAdapter.notifyDataSetChanged()
            if(allCkecked){
                disableRename()
                enableDelete()
            }else{
                disableRename()
                disableDelete()
            }
        }



        btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Eliminar nota?")
            val nbNotes = filteredNotesArray.count { it.isChecked }
            builder.setMessage("Seguro que quieres eliminar $nbNotes nota(s) ?")
            builder.setPositiveButton("Delete") { _, _ ->
                val toDelete = filteredNotesArray.filter { it.isChecked }.toTypedArray()

                GlobalScope.launch {
                    runOnUiThread {

                            //filteredNotesArray.clear()
                            filteredNotesArray.removeAll(toDelete)
                            notes.removeAll(toDelete)
                            printFilteredNotesArray()
                            printFilteredNotesArray2()

                        // Reemplazar json
                        saveNotesToJson()
                        if(notes.isNullOrEmpty()){
                            imagenFondo.visibility = View.VISIBLE
                        }else{
                            imagenFondo.visibility = View.GONE
                        }
                        leaveEditMode()

                        // Notify the adapter after removing notes
                        mAdapter.notifyDataSetChanged()
                    }
                }
            }
            builder.setNegativeButton("Cancel") { _, _ ->
                // No hacer nada
            }

            val dialog = builder.create()
            dialog.show()
        }

    }
    override fun onResume() {
        super.onResume()
        searchInput.clearFocus()
        runOnUiThread {
            notes.clear()
            loadNotesFromJsonAndRefreshList()
            mAdapter.notifyDataSetChanged()
        }


        if(notes.isNullOrEmpty()){
            imagenFondo.visibility = View.VISIBLE
        }else{
            imagenFondo.visibility = View.GONE
        }
    }

    //Inicializar Id's
    private fun inicializeIds(){

        recyclerview = findViewById<RecyclerView>(R.id.RvNotes)
        bottomSheetBG = findViewById(R.id.bottomSheetBG)
        btnTextBS = findViewById(R.id.btnTextBS)
        btnChecklistBS = findViewById(R.id.btnChecklistBS)
        bottomSheet = findViewById(R.id.bottomSheet2)
        btnRename = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDelete)
        tvDelete = findViewById(R.id.tvDelete)
        tvRename = findViewById(R.id.tvEdit)
        fab = findViewById(R.id.fab)
        toolbar = findViewById(R.id.toolbar)
        editBar = findViewById(R.id.editBar)
        btnSelectAll = findViewById(R.id.btnSelectAll)

    }

    fun callCreate(view: View) {
        showBottomSheet()
    }

    //Esconder menu bottom
    private fun hideBotomSheet(){
        var botomSheet = findViewById<LinearLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(botomSheet)
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        var bottomSheetBG : View = findViewById(R.id.bottomSheetBG)
        bottomSheetBG.visibility = View.GONE
    }

    //Mostrar menu bottom
    private fun showBottomSheet(){
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        var bottomSheetBG = findViewById<View>(R.id.bottomSheetBG)
        bottomSheetBG.visibility = View.VISIBLE
    }

    //Cargar Notas y actualizar ArrayList
    private fun loadNotesFromJsonAndRefreshList() {
        val fileName = "notes.json"
        val gson = Gson()

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

            val listType = object : TypeToken<ArrayList<Notes>>() {}.type
            val existingNotes: ArrayList<Notes> = gson.fromJson(stringBuilder.toString(), listType)

            inputStream.close()

            notes.clear() // Limpiar el ArrayList actual
            notes.addAll(existingNotes) // Agregar las notas cargadasve

            filteredNotesArray.clear() // Limpiar el ArrayList actual
            filteredNotesArray.addAll(existingNotes) // Cargar las notas en filteredNotesArray

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Se activa cuanda se da click a un item del itemview
    override fun onItemClickListener(position: Int) {
        if(mAdapter.isEditMode()){
            filteredNotesArray[position].isChecked = !filteredNotesArray[position].isChecked
            mAdapter.notifyItemChanged(position)

            var nbSelected = filteredNotesArray.count{it.isChecked}

            when(nbSelected){
                0 -> {
                    disableDelete()
                    disableRename()
                }
                1 -> {
                    enableDelete()
                    disableRename()
                }
                else ->{
                    disableRename()
                    enableDelete()
                }
            }
        }else{
            var notesEdit = filteredNotesArray[position]
            var intent = Intent(this,TextNoteActivity::class.java)
            intent.putExtra("fileTitle",notesEdit.fileTitle)
            intent.putExtra("fileContent",notesEdit.fileContent)
            intent.putExtra("color", notesEdit.color)
            intent.putExtra("fecha", notesEdit.creationDateString)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
            startActivity(intent, options.toBundle())


        }
    }

    fun printFilteredNotesArray() {
        Log.d("FilteredNotesArray", "Contenido de filteredNotesArray:")
        for (note in filteredNotesArray) {
            Log.d("FilteredNotesArray", "Título: ${note.fileTitle}, Contenido: ${note.fileContent}, isChecked: ${note.isChecked}")
        }
    }

    //Se activa cuanda se da Long click a un item del itemview
    override fun onItemLongClickListener(position: Int) {
        mAdapter.setEditMode(true)
        fab.visibility = View.GONE
        filteredNotesArray[position].isChecked = !filteredNotesArray[position].isChecked
        mAdapter.notifyItemChanged(position)

        bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED


        if(mAdapter.isEditMode() && editBar.visibility == View.GONE){
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
            editBar.visibility = View.VISIBLE

            disableRename()
            enableDelete()
        }
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

    //Desactivar btnRename
    private fun disableRename(){
        btnRename.isClickable = false
        btnRename.backgroundTintList = ResourcesCompat.getColorStateList(resources,R.color.black_light2,theme)
        tvRename.setTextColor(ResourcesCompat.getColorStateList(resources,R.color.black_light2,theme))
    }

    //Desactivar btnDelete
    private fun disableDelete(){
        btnDelete.isClickable = false
        btnDelete.backgroundTintList = ResourcesCompat.getColorStateList(resources,R.color.black_light2,theme)
        tvDelete.setTextColor(ResourcesCompat.getColorStateList(resources,R.color.black_light2,theme))
    }

    //Activar Rename
    private fun enableRename(){
        btnRename.isClickable = true
        btnRename.backgroundTintList = ResourcesCompat.getColorStateList(resources,R.color.white,theme)
        tvRename.setTextColor(ResourcesCompat.getColorStateList(resources,R.color.white,theme))
    }

    //Activar Delete
    private fun enableDelete(){
        btnDelete.isClickable = true
        btnDelete.backgroundTintList = ResourcesCompat.getColorStateList(resources,R.color.white,theme)
        tvDelete.setTextColor(ResourcesCompat.getColorStateList(resources,R.color.white,theme))
    }

    //Salir del modo editor
    private fun leaveEditMode(){
        supportActionBar?.setDisplayShowHomeEnabled(true)
        editBar.visibility = View.GONE
        bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
        fab.visibility = View.VISIBLE
        notes.map{it.isChecked = false}
        mAdapter.setEditMode(false)
    }




    override fun onBackPressed() {

        if (mAdapter.isEditMode()) {
            leaveEditMode()

        }

        else if (bottomSheetBG.visibility == View.VISIBLE) {
            hideBotomSheet()
        }
        else {
            AlertDialog.Builder(this)
                .setTitle("Cerrar Aplicación?")
                .setMessage("Seguro que quieres cerrar la aplicación")
                .setPositiveButton(android.R.string.ok,
                    DialogInterface.OnClickListener{ dialog, which ->
                        super.onBackPressed()
                        //finish()
                        //android.os.Process.killProcess(android.os.Process.myPid())
                    })
                .setNegativeButton(android.R.string.cancel,
                    DialogInterface.OnClickListener{ dialog, which ->

                    })
                .setCancelable(true)
                .show()
        }

    }

}
