package com.android.sample.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.android.sample.R
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.sample.ui.navigation.NavigationActions
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

val defaultLocation = LatLng(46.51915277948766, 6.566736625776037)
const val defaultZoom = 15f

enum class CameraAction {
    NO_ACTION,
    MOVE,
    ANIMATE
}

@Composable
fun MapScreen(navigationActions: NavigationActions, currentMusicPlayed: String? = null) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val locationPermitted: MutableState<Boolean?> = remember { mutableStateOf(null) }
    val locationPermissionsAlreadyGranted =
        (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED)
    val locationPermissions =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        locationPermitted.value = true
                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        locationPermitted.value = true
                    }
                    else -> {
                        locationPermitted.value = false
                    }
                }
            })
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val currentPosition = remember { mutableStateOf(defaultLocation) }
    var isMapLoaded by remember { mutableStateOf(false) }
    val moveToCurrentLocation = remember { mutableStateOf(CameraAction.NO_ACTION) }
    LaunchedEffect(Unit) {
        if (locationPermissionsAlreadyGranted) {
            locationPermitted.value = true
        } else {
            locationPermissionLauncher.launch(locationPermissions)
            Log.d("MapScreen", "Requesting location permission")
        }

        // wait for user input
        while (locationPermitted.value == null) {
            delay(100)
            Log.d("MapScreen", "Waiting for location permission")
        }

        while (true) {
            coroutineScope.launch {
                if (locationPermitted.value == true) {
                    Log.d("MapScreen", "Fetching current location")
                    val priority = PRIORITY_BALANCED_POWER_ACCURACY
                    val result =
                        locationClient
                            .getCurrentLocation(
                                priority,
                                CancellationTokenSource().token,
                            )
                            .await()
                    result.let { fetchedLocation ->
                        currentPosition.value = LatLng(fetchedLocation.latitude, fetchedLocation.longitude)
                        isMapLoaded = true
                    }
                } else if (locationPermitted.value == false) {
                    isMapLoaded = true
                }
            }
            delay(5000) // map is updated every 5s
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { route -> navigationActions.navigateTo(route) },
                tabList = LIST_TOP_LEVEL_DESTINATION,
                selectedItem = navigationActions.currentRoute())
        },
        modifier = Modifier.fillMaxSize().testTag("MapScreen"),
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Map fills most of the screen
            Box(modifier = Modifier.weight(1f).testTag("MapContainer")) {
                if (isMapLoaded) {
                    moveToCurrentLocation.value = CameraAction.MOVE
                    GoogleMapView(
                        currentPosition = currentPosition,
                        moveToCurrentLocation = moveToCurrentLocation,
                        modifier = Modifier.testTag("Map"),
                        locationPermitted = locationPermitted.value!!)
                } else {
                    Text("Loading map...", modifier = Modifier.padding(16.dp))
                }
            }

            // Player is placed just above the bottom bar
            Row(
                modifier =
                Modifier.fillMaxWidth()
                    .height(76.dp)
                    .background(color = Color(0x215F2A83))
                    .padding(horizontal = 32.dp, vertical = 26.dp)
                    .testTag("playerContainer"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PlayerCurrentMusicItem(currentMusicPlayed)
            }
        }
    }
}

@Composable
fun GoogleMapView(
    currentPosition: MutableState<LatLng>,
    moveToCurrentLocation: MutableState<CameraAction>,
    modifier: Modifier,
    locationPermitted: Boolean,
    radius: Double = 1000.0
) {
    val coroutineScope = rememberCoroutineScope()

    val mapVisible by remember { mutableStateOf(true) }

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(moveToCurrentLocation.value, Unit) {
        if (moveToCurrentLocation.value == CameraAction.MOVE) {
            coroutineScope.launch {
                cameraPositionState.move(
                    update =
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(currentPosition.value, defaultZoom)))
                moveToCurrentLocation.value = CameraAction.NO_ACTION
            }
        } else if (moveToCurrentLocation.value == CameraAction.ANIMATE) {
            coroutineScope.launch {
                cameraPositionState.animate(
                    update =
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(currentPosition.value, defaultZoom)),
                    durationMs = 1000)
                moveToCurrentLocation.value = CameraAction.NO_ACTION
            }
        }
    }

    val context = LocalContext.current

    if (mapVisible) {
        Box(Modifier.fillMaxSize()) {
            GoogleMap(modifier = modifier, cameraPositionState = cameraPositionState) {
                currentPosition.value.let { location ->
                    Marker(
                        state = MarkerState(position = location),
                        title = "You are here",
                        icon = getBitmapDescriptorFromDrawableResource(R.drawable.me_position, context),
                        anchor = Offset(0.5f, 0.5f))
                    Circle(
                        center = location,
                        radius = radius,
                        strokeColor = Color(0xFF5F2A83),
                        strokeWidth = 3f,
                        fillColor = Color(0x215F2A83))
                }
            }
            Box(modifier = Modifier.align(Alignment.TopEnd).testTag("currentLocationButton")) {
                CurrentLocationCenterButton(currentPosition.value, cameraPositionState)
            }
        }
    }
}

fun getBitmapDescriptorFromDrawableResource(resourceId: Int, context: Context): BitmapDescriptor {
    val drawable = ContextCompat.getDrawable(context, resourceId)
    val canvas = android.graphics.Canvas()
    val bitmap =
        Bitmap.createBitmap(
            drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    canvas.setBitmap(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
fun PlayerCurrentMusicItem(musique: String?) {
    if (musique != null) {
        /*to do : musique is showing*/
    } else {
        Text(
            modifier =
            Modifier.fillMaxWidth().height(24.dp).padding(horizontal = 16.dp).testTag("playerText"),
            text = "not listening yet",
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight(400),
            color = Color(0xFF5F2A83),
            textAlign = TextAlign.Center)
    }
}

@Composable
fun CurrentLocationCenterButton(
    currentLocation: LatLng?,
    cameraPositionState: CameraPositionState
) {
    FloatingActionButton(
        containerColor = Color.White,
        onClick = {
            currentLocation?.let { location ->
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(location, defaultZoom))
            }
        },
        modifier =
        Modifier.padding(16.dp)
            .size(48.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp), clip = false)
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .testTag("currentLocationFab")) {
        Icon(
            painter = painterResource(id = R.drawable.location_arrow_1),
            contentDescription = "Current Position",
            modifier = Modifier.size(30.dp).background(Color.White).testTag("currentLocationIcon"),
            tint = Color.Unspecified)
    }
}
