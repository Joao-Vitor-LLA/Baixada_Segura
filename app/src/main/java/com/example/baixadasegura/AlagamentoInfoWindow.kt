package com.example.baixadasegura

import android.view.View
import android.widget.Button
import android.widget.TextView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.infowindow.InfoWindow

class AlagamentoInfoWindow(
    private val mapView: MapView
) : InfoWindow(R.layout.info_alagamento, mapView) {

    private var likes = 0
    private var dislikes = 0

    override fun onOpen(item: Any?) {

        val marker = item as Marker

        val titulo = mView.findViewById<TextView>(R.id.txtTitulo)
        val votos = mView.findViewById<TextView>(R.id.txtVotos)
        val btnLike = mView.findViewById<Button>(R.id.btnLike)
        val btnDislike = mView.findViewById<Button>(R.id.btnDislike)
        val btnConfirmar = mView.findViewById<Button>(R.id.btnConfirmar)

        titulo.text = marker.title

        fun atualizarTexto() {
            votos.text = "👍 $likes   👎 $dislikes"
        }

        atualizarTexto()

        btnLike.setOnClickListener {
            likes++
            atualizarTexto()
        }

        btnDislike.setOnClickListener {
            dislikes++
            atualizarTexto()
        }

        btnConfirmar.setOnClickListener {

            marker.title = "Alagamento confirmado"
            titulo.text = marker.title

            val circulo = marker.relatedObject as Polygon

            circulo.fillColor = android.graphics.Color.argb(80, 255, 0, 0)
            circulo.strokeColor = android.graphics.Color.RED

            btnLike.visibility = View.GONE
            btnDislike.visibility = View.GONE
            btnConfirmar.visibility = View.GONE

            mapView.invalidate()
        }
    }

    override fun onClose() {}
}