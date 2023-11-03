package com.example.storysubmissionapp.view.maps

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storysubmissionapp.R
import com.example.storysubmissionapp.data.Result
import com.example.storysubmissionapp.data.model.Story
import com.example.storysubmissionapp.data.model.UserModel
import com.example.storysubmissionapp.databinding.ActivityMapsBinding
import com.example.storysubmissionapp.view.ViewModelFactory
import com.example.storysubmissionapp.view.detail.DetailActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var loc: List<Story>
    private val boundsBuilder = LatLngBounds.Builder()
    private var theToast: Toast? = null
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mMap.setOnInfoWindowClickListener {
            val toDetail = Intent(this@MapsActivity, DetailActivity::class.java)
            toDetail.putExtra(DetailActivity.DETAIL_STORY, it.tag.toString())
            startActivity(toDetail)
        }

        mMap.setOnMarkerClickListener {
            it.showInfoWindow()
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it.position, 5f))
            true
        }

        setMapStyle()
        viewModel.getSessionData().observe(this) { user ->
            getStories(user)
        }
    }

    private fun getStories(user: UserModel) {
        viewModel.getStories(user.token).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showToast(getString(R.string.gettingData))
                    }

                    is Result.Success -> {
                        loc = result.data.listStory
                        showToast(getString(R.string.successLoc))
                        addMarkers()
                    }

                    is Result.Error -> {
                        showSnackBar(result.error)
                    }

                }

            }

        }
    }

    private fun addMarkers() {
        mMap.clear()
        loc.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            val marker = mMap.addMarker(
                MarkerOptions().position(latLng).title(story.name)
                    .snippet(story.description)
            )
            boundsBuilder.include(latLng)
            marker?.tag = story.id
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )

    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun showSnackBar(msg: String) {
        theToast?.cancel()
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.close)) { }
            .show()
    }

    private fun showToast(msg: String) {
        theToast?.cancel()
        theToast = Toast.makeText(this, msg, Toast.LENGTH_LONG)
        theToast?.show()
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}