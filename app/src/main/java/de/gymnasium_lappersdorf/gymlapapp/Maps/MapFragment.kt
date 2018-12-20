package de.gymnasium_lappersdorf.gymlapapp.Maps

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.gymnasium_lappersdorf.gymlapapp.R

/**
 * 04.05.2018 | created by Lukas S
 */

class MapFragment : Fragment(), OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private var v: View? = null
    private var mapView: MapView? = null
    private var map: GoogleMap? = null

    private var optionSpinner: Spinner? = null
    private val OPTIONS = arrayOf("Gebäude", "Sekretariat")

    private val GYMLAP = LatLng(49.0453185, 12.0823808)
    private val GYMLAP_SEK = LatLng(49.045420, 12.082991)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_map, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.title = "Karte"

        //floor selection spinner

        optionSpinner = v!!.findViewById(R.id.option_spinner)
        val adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, OPTIONS)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        optionSpinner!!.adapter = adapter
        optionSpinner!!.onItemSelectedListener = this

        //map view

        mapView = v!!.findViewById(R.id.map)

        if (mapView != null) {
            mapView!!.onCreate(null)
            mapView!!.onResume()
            mapView!!.getMapAsync(this)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        MapsInitializer.initialize(context!!)

        map = googleMap

        map!!.clear()
        showBuilding()
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {

        when (position) {
            0 -> {
                // Gebäude
                map!!.clear()
                showBuilding()
            }
            1 -> {
                // Sekretariat
                map!!.clear()
                showSek()
            }
        }

    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {}

    private fun showBuilding() {
        map!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
        map!!.addMarker(MarkerOptions().position(GYMLAP).title("Gymnasium Lappersdorf"))
        val gymlap = CameraPosition.builder().target(GYMLAP).zoom(17.25f).build()
        map!!.moveCamera(CameraUpdateFactory.newCameraPosition(gymlap))
    }

    private fun showSek() {
        map!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        map!!.addMarker(MarkerOptions().position(GYMLAP_SEK).title("Sekretariat"))
        val sek = CameraPosition.builder().target(GYMLAP_SEK).zoom(18.50f).build()
        map!!.moveCamera(CameraUpdateFactory.newCameraPosition(sek))
    }
}
