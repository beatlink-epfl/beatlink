package com.android.sample

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.sample.ui.MainScreen
import com.android.sample.ui.theme.SampleAppTheme

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { MainScreen() }
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<String>,
      grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
      if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
        // Permission is granted, trigger the location fetching logic again
        Log.d("MainActivity", "Location permission granted, fetching location")
        fetchLocationAfterPermissionGranted()
      } else {
        Log.d("MainActivity", "Location permission denied")
      }
    }
  }

  private fun fetchLocationAfterPermissionGranted() {
    // This will re-trigger location fetching after permission is granted
    setContent {
      MainScreen() // Trigger composable re-composition after permission grant
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  MainScreen()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  SampleAppTheme { Greeting("Android") }
}
