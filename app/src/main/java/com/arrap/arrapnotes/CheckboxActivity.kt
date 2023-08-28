package com.arrap.arrapnotes
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView


class CheckboxActivity : AppCompatActivity() {

    private lateinit var arrowBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkbox)

        inicializarIDS()
        inicializarListeners()
    }

    private fun inicializarListeners() {
        arrowBack.setOnClickListener {
            onBackPressed()
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun inicializarIDS() {
        arrowBack = findViewById(R.id.arrowBack)
    }




}
