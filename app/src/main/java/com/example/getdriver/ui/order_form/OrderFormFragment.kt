package com.example.getdriver.ui.order_form

import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.getdriver.R
import com.example.getdriver.viewmodels.YandexMapViewModel
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.FilteringMode
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderFormFragment : Fragment(), UserLocationObjectListener {

    private val PERMISSIONS_REQUEST_FINE_LOCATION = 1

    private var mapView: MapView? = null
    private var userLocationLayer: UserLocationLayer? = null
    private lateinit var viewModel: YandexMapViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        var view = inflater.inflate(R.layout.order_form_map, null)

        mapView = view.findViewById<MapView>(R.id.mapview)
        mapView!!.map.isRotateGesturesEnabled = false
        mapView!!.map.move(CameraPosition(Point(0.0, 0.0), 17f, 0f, 0f))

        requestLocationPermission()

        val mapKit = MapKitFactory.getInstance()
        mapKit.resetLocationManagerToDefault()
        userLocationLayer = mapKit.createUserLocationLayer(mapView!!.mapWindow)
        userLocationLayer!!.setVisible(true)
        userLocationLayer!!.setHeadingEnabled(true)

        userLocationLayer!!.setObjectListener(this)


        val viewModel: YandexMapViewModel by viewModels()


        val scope = MainScope() // the scope of MyUIClass, uses Dispatchers.Main


        /** Setting the array adapter to the listview  */
        mapKit.createLocationManager().subscribeForLocationUpdates(
            0.0,
            0,
            0.0,
            true,
            FilteringMode.ON,
            object : LocationListener {
                override fun onLocationUpdated(@NonNull location: Location) {
                    Log.d("TagCheck", "LocationUpdated " + location.getPosition().getLongitude())
                    Log.d("TagCheck", "LocationUpdated " + location.getPosition().getLatitude())
                    var longlat = ""+location.getPosition().getLongitude()+","+location.getPosition().getLatitude()


                    scope.launch {
                        viewModel.getAddress(longlat).collect {
                                text ->
                            println(text)
                        }
                    }

                }

                override fun onLocationStatusUpdated(p0: LocationStatus) {

                }

            })


        return view

    }


    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                "android.permission.ACCESS_FINE_LOCATION"
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf<String>("android.permission.ACCESS_FINE_LOCATION"),
                this.PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }
    }

    override fun onStop() {
        mapView!!.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView!!.onStart()
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationLayer!!.setAnchor(
            PointF((mapView!!.width * 0.5).toFloat(), (mapView!!.height * 0.5).toFloat()),
            PointF((mapView!!.width * 0.5).toFloat(), (mapView!!.height * 0.83).toFloat())
        )


        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                requireActivity(), R.drawable.user_arrow
            )
        )

        //userLocationView.arrow.setText("Привет")


        /*
        val pinIcon = userLocationView.pin.useCompositeIcon()
        pinIcon.setIcon(
            "icon",
            ImageProvider.fromResource(requireActivity(), R.drawable.icon),
            IconStyle().setAnchor(PointF(0f, 0f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(1f)

        )
        pinIcon.setIcon(
            "pin",
            ImageProvider.fromResource(requireActivity(), R.drawable.search_result),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(0.5f)
        )*/
        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

/*
    fun drawSimpleBitmap(number: String): Bitmap {
        val picSize = 640;
        val bitmap = Bitmap.createBitmap(picSize, picSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        // ????????? ??????????
        val paint = Paint()
        paint.setColor(Color.BLUE)
        paint.setStyle(Paint.Style.FILL)
        canvas.drawCircle(picSize / 2, picSize / 2, picSize / 2, paint)
        // ????????? ??????
        paint.setColor(Color.WHITE)
        paint.setAntiAlias(true)
        paint.setTextSize(20)
        paint.setTextAlign(Paint.Align.CENTER)
        canvas.drawText(number, picSize / 2,
            picSize / 2 - ((paint.descent() + paint.ascent()) / 2), paint)
        return bitmap
    }
*/

    override fun onObjectRemoved(p0: UserLocationView) {}

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}



}