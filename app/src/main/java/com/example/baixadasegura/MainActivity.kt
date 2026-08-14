package com.example.baixadasegura

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale

data class AlertaMarkerData(
    val idFirebase: String?,
    val circulo: Polygon
)

class MainActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val osmdroidConfig = Configuration.getInstance()

        osmdroidConfig.load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        osmdroidConfig.setUserAgentValue(
            "BaixadaSegura/1.0 (contato: j.alves@unisantos.br)"
        )

        osmdroidConfig.getAdditionalHttpRequestProperties()
            .put(
                "User-Agent",
                "BaixadaSegura/1.0 (contato: j.alves@unisantos.br)"
            )

        Log.d(
            "OSM",
            "UA configurado: ${osmdroidConfig.userAgentValue}"
        )
        setContentView(R.layout.activity_main)

        map = findViewById(R.id.map)

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.setBuiltInZoomControls(false)

        database = FirebaseDatabase.getInstance().reference

        val mapEventsReceiver = object : MapEventsReceiver {

            override fun singleTapConfirmedHelper(
                p: GeoPoint?
            ): Boolean {

                InfoWindow.closeAllInfoWindowsOn(map)
                return false
            }

            @SuppressLint("UseSwitchCompatOrMaterialCode")
            override fun longPressHelper(
                p: GeoPoint?
            ): Boolean {

                val swtPinLocal = findViewById<Switch>(R.id.swtPinLocal)

                if (p != null && swtPinLocal.isChecked) {
                    adicionarPin(p, true)
                }

                return true
            }
        }

        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)

        map.overlays.add(mapEventsOverlay)

        val btnLocalizacao = findViewById<Button>(R.id.btnLocalizacao)

        btnLocalizacao.setOnClickListener {
            centralizarNoUsuario()
        }

        val btnPin = findViewById<Button>(R.id.btnPin)

        btnPin.setOnClickListener {

            val local = locationOverlay.myLocation ?: return@setOnClickListener

            adicionarPin(local, true)
        }

        val btnCord = findViewById<Button>(R.id.btnCord)

        btnCord.setOnClickListener {

            val intent = Intent(
                this, CoordenadaActivity::class.java
            )

            startActivityForResult(intent, 100)
        }

        map.setOnClickListener {
            InfoWindow.closeAllInfoWindowsOn(map)
        }

        verificarPermissaoLocalizacao()

        carregarAlertas()
    }

    private fun verificarPermissaoLocalizacao() {

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 1
            )

        } else {
            ativarLocalizacao()
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {

        super.onActivityResult(
            requestCode, resultCode, data
        )

        if (requestCode == 100 && resultCode == RESULT_OK) {

            val latitude = data?.getDoubleExtra(
                "latitude", 0.0
            )

            val longitude = data?.getDoubleExtra(
                "longitude", 0.0
            )

            if (latitude != null && longitude != null) {

                val ponto = GeoPoint(
                    latitude, longitude
                )

                adicionarPin(
                    ponto, true
                )

                map.controller.animateTo(ponto)
                map.controller.setZoom(18.0)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )

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

    private fun pegarEndereco(
        local: GeoPoint
    ): String {

        return try {

            val geocoder = Geocoder(this, Locale("pt", "BR"))

            val enderecos = geocoder.getFromLocation(
                local.latitude, local.longitude, 1
            )

            if (!enderecos.isNullOrEmpty()) {

                val endereco = enderecos[0]

                val rua = endereco.thoroughfare ?: "Local desconhecido"

                val bairro = endereco.subLocality ?: ""

                "$rua, $bairro"

            } else {
                "Local desconhecido"
            }

        } catch (e: Exception) {
            "Local desconhecido"
        }
    }

    fun adicionarPin(
        local: GeoPoint,
        salvarNoBanco: Boolean,
        idFirebase: String? = null
    ) {

        val marker = Marker(map)

        marker.position = local

        marker.setAnchor(
            Marker.ANCHOR_CENTER,
            Marker.ANCHOR_BOTTOM
        )

        val endereco = pegarEndereco(local)

        marker.title =
            "Alagamento reportado\n$endereco"

        marker.infoWindow =
            AlagamentoInfoWindow(map)

        val circulo = Polygon()

        circulo.points =
            Polygon.pointsAsCircle(local, 80.0)

        circulo.fillColor =
            android.graphics.Color.argb(
                100,
                255,
                0,
                0
            )

        circulo.strokeColor =
            android.graphics.Color.YELLOW

        circulo.strokeWidth = 4f

        // Guarda o ID do Firebase + o círculo
        marker.relatedObject = AlertaMarkerData(
            idFirebase,
            circulo
        )

        map.overlays.add(circulo)
        map.overlays.add(marker)

        map.invalidate()

        if (salvarNoBanco) {

            val alerta = hashMapOf(
                "latitude" to local.latitude,
                "longitude" to local.longitude,
                "titulo" to "Alagamento",
                "endereco" to endereco
            )

            database.child("alertas")
                .get()
                .addOnSuccessListener { snapshot ->

                    val quantidade =
                        snapshot.childrenCount + 1

                    val id =
                        "alagamento_$quantidade"

                    // Atualiza o ID do Firebase no Marker
                    marker.relatedObject =
                        AlertaMarkerData(
                            id,
                            circulo
                        )

                    database
                        .child("alertas")
                        .child(id)
                        .setValue(alerta)
                }
        }
    }

    private fun carregarAlertas() {

        database.child("alertas")
            .addValueEventListener(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        val remover =
                            mutableListOf<Any>()

                        for (overlay in map.overlays) {

                            if (
                                overlay is Marker ||
                                overlay is Polygon
                            ) {

                                remover.add(overlay)
                            }
                        }

                        map.overlays.removeAll(remover)

                        for (item in snapshot.children) {

                            val latitude =
                                item.child("latitude")
                                    .getValue(Double::class.java)

                            val longitude =
                                item.child("longitude")
                                    .getValue(Double::class.java)

                            // ID do alerta no Firebase
                            val idFirebase =
                                item.key

                            if (
                                latitude != null &&
                                longitude != null
                            ) {

                                val ponto =
                                    GeoPoint(
                                        latitude,
                                        longitude
                                    )

                                adicionarPin(
                                    ponto,
                                    false,
                                    idFirebase
                                )
                            }
                        }

                        map.invalidate()
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {
                    }
                }
            )
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