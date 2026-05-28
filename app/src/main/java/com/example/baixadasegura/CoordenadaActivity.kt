package com.example.baixadasegura

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class CoordenadaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_coordenada)

        val edtLatitude =
            findViewById<EditText>(R.id.edtLatitude)

        val edtLongitude =
            findViewById<EditText>(R.id.edtLongitude)

        val btnAdicionar =
            findViewById<Button>(R.id.btnAdicionarCord)

        btnAdicionar.setOnClickListener {

            val latitude =
                edtLatitude.text.toString()
                    .toDoubleOrNull()

            val longitude =
                edtLongitude.text.toString()
                    .toDoubleOrNull()

            if (latitude != null && longitude != null) {

                val intent = Intent()

                intent.putExtra(
                    "latitude",
                    latitude
                )

                intent.putExtra(
                    "longitude",
                    longitude
                )

                setResult(
                    RESULT_OK,
                    intent
                )

                finish()
            }
        }
    }
}