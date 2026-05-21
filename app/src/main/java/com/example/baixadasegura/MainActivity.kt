package com.example.baixadasegura

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import android.widget.Button
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.infowindow.InfoWindow
import android.widget.Switch

class MainActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Configuração padrão do OSMDroid
        Configuration.getInstance().load(
            applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        setContentView(R.layout.activity_main)

        map = findViewById(R.id.map)
        map.setMultiTouchControls(true)
        map.setBuiltInZoomControls(false)

        val mapEventsReceiver = object : MapEventsReceiver {

            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                InfoWindow.closeAllInfoWindowsOn(map)
                return false
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                val swtPinLocal = findViewById<Switch>(R.id.swtPinLocal)
                if (p != null && swtPinLocal.isChecked) {
                    adicionarPin(p)
                }

                return true
            }
        }

        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        map.overlays.add(mapEventsOverlay)

        //botão de centralizar
        val btnLocalizacao = findViewById<Button>(R.id.btnLocalizacao)
        btnLocalizacao.setOnClickListener {
            centralizarNoUsuario()
        }
        verificarPermissaoLocalizacao()

        val btnPin = findViewById<Button>(R.id.btnPin)

        btnPin.setOnClickListener {

            val local = locationOverlay.myLocation ?: return@setOnClickListener

            adicionarPin(local)
        }

        map.setOnClickListener {
            InfoWindow.closeAllInfoWindowsOn(map)
        }
    }

    private fun verificarPermissaoLocalizacao() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
            )
        } else {
            ativarLocalizacao()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ativarLocalizacao()
        }
    }

    private fun ativarLocalizacao() {
        locationOverlay = MyLocationNewOverlay(
            GpsMyLocationProvider(this), map
        )

        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        map.overlays.add(locationOverlay)
        centralizarNoUsuario()
    }

    private fun centralizarNoUsuario() {
        val local = locationOverlay.myLocation
        map.controller.animateTo(local)
        map.controller.setZoom(18.0)
    }

    private fun adicionarPin(local: GeoPoint) {

        val marker = Marker(map)
        marker.position = local

        marker.setAnchor(
            Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM
        )

        marker.title = "Alagamento reportado\n(Não analisado)"

        marker.infoWindow = AlagamentoInfoWindow(map)

        val circulo = Polygon()

        marker.relatedObject = circulo

        circulo.points = Polygon.pointsAsCircle(local, 50.0)

        circulo.fillColor = android.graphics.Color.argb(80, 255, 0, 0)

        circulo.strokeColor = android.graphics.Color.YELLOW

        circulo.strokeWidth = 4f

        map.overlays.add(circulo)
        map.overlays.add(marker)

        map.invalidate()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}