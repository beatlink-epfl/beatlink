package com.epfl.beatlink.model.map

import androidx.lifecycle.ViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.Mockito.mock

class MapViewModelFactoryTest {

  private val mockRepository: MapLocationRepository = mock(MapLocationRepository::class.java)

  @Test
  fun `provideFactory creates MapViewModel instance`() {
    val factory = MapViewModel.provideFactory(mockRepository)

    val viewModel = factory.create(MapViewModel::class.java)

    val mapViewModel = viewModel
    assertEquals(mockRepository, mapViewModel.mapLocationRepository)
  }

  @Test
  fun `provideFactory throws IllegalArgumentException for unknown ViewModel class`() {
    val factory = MapViewModel.provideFactory(mockRepository)

    // Verify that requesting an unknown ViewModel class throws an exception
    assertThrows(IllegalArgumentException::class.java) {
      factory.create(UnknownViewModel::class.java)
    }
  }

  // Dummy ViewModel class to test unknown class exception
  class UnknownViewModel : ViewModel()
}
