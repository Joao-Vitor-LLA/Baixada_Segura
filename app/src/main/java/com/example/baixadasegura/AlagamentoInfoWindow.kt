package com.example.baixadasegura

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class AlagamentoInfoWindow(
    private val mapView: MapView
) : InfoWindow(R.layout.info_alagamento, mapView) {

    private val database =
        FirebaseDatabase.getInstance().reference

    private var likes = 0
    private var dislikes = 0

    private var isvoted = 0

    override fun onOpen(item: Any?) {

        val marker = item as Marker

        val titulo =
            mView.findViewById<TextView>(R.id.txtTitulo)

        val votos =
            mView.findViewById<TextView>(R.id.txtVotos)

        val btnLike =
            mView.findViewById<Button>(R.id.btnLike)

        val btnDislike =
            mView.findViewById<Button>(R.id.btnDislike)

        val btnConfirmar =
            mView.findViewById<Button>(R.id.btnConfirmar)

        val btnExcluir =
            mView.findViewById<Button>(R.id.btnExcluir)

        titulo.text = marker.title

        fun atualizarTexto() {
            votos.text = "👍 $likes   👎 $dislikes"
        }

        atualizarTexto()

        // LIKE
        btnLike.setOnClickListener {

            if (isvoted == 0) {

                isvoted = 1

                btnLike.visibility = View.GONE
                btnDislike.visibility = View.GONE

                likes++

                atualizarTexto()
            }
        }

        // DISLIKE
        btnDislike.setOnClickListener {

            if (isvoted == 0) {

                isvoted = 1

                btnLike.visibility = View.GONE
                btnDislike.visibility = View.GONE

                dislikes++

                atualizarTexto()
            }
        }

        // CONFIRMAR
        btnConfirmar.setOnClickListener {

            marker.title =
                "Alagamento confirmado"

            titulo.text =
                marker.title

            val dados =
                marker.relatedObject as? AlertaMarkerData
                    ?: return@setOnClickListener

            val circulo =
                dados.circulo

            circulo.fillColor =
                android.graphics.Color.argb(
                    80,
                    255,
                    0,
                    0
                )

            circulo.strokeColor =
                android.graphics.Color.RED

            btnLike.visibility =
                View.GONE

            btnDislike.visibility =
                View.GONE

            btnConfirmar.visibility =
                View.GONE

            mapView.invalidate()
        }

        // EXCLUIR
        btnExcluir.setOnClickListener {

            val dados =
                marker.relatedObject as? AlertaMarkerData
                    ?: return@setOnClickListener

            val idFirebase =
                dados.idFirebase

            if (idFirebase != null) {

                database
                    .child("alertas")
                    .child(idFirebase)
                    .removeValue()
                    .addOnSuccessListener {

                        marker.closeInfoWindow()

                        mapView.overlays.remove(
                            marker
                        )

                        mapView.overlays.remove(
                            dados.circulo
                        )

                        mapView.invalidate()
                    }
            }
        }
    }

    override fun onClose() {
    }
}