package com.epfl.beatlink.ui.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class NavigationActionsTest {

  private lateinit var navigationDestination: NavDestination
  private lateinit var navHostController: NavHostController
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationDestination = mock(NavDestination::class.java)
    navHostController = mock(NavHostController::class.java)
    navigationActions = NavigationActions(navHostController)
  }

  @Test
  fun navigateToCallTopLevel() {
    navigationActions.navigateTo(TopLevelDestinations.HOME)
    verify(navHostController).navigate(eq(Route.HOME), any<NavOptionsBuilder.() -> Unit>())

    navigationActions.navigateTo(TopLevelDestinations.SEARCH)
    verify(navHostController).navigate(eq(Route.SEARCH), any<NavOptionsBuilder.() -> Unit>())

    navigationActions.navigateTo(TopLevelDestinations.PROFILE)
    verify(navHostController).navigate(eq(Route.PROFILE), any<NavOptionsBuilder.() -> Unit>())

    navigationActions.navigateTo(TopLevelDestinations.LIBRARY)
    verify(navHostController).navigate(eq(Route.LIBRARY), any<NavOptionsBuilder.() -> Unit>())
  }

  @Test
  fun navigateToCallScreen() {
    navigationActions.navigateTo(Screen.HOME)
    verify(navHostController).navigate(Screen.HOME)

    navigationActions.navigateTo(Screen.SEARCH)
    verify(navHostController).navigate(Screen.SEARCH)

    navigationActions.navigateTo(Screen.PROFILE)
    verify(navHostController).navigate(Screen.PROFILE)

    navigationActions.navigateTo(Screen.LIBRARY)
    verify(navHostController).navigate(Screen.LIBRARY)
  }

  @Test
  fun goBackCallsController() {
    navigationActions.goBack()
    verify(navHostController).popBackStack()
  }

  @Test
  fun currentRouteWorksWithDestination() {
    `when`(navHostController.currentDestination).thenReturn(navigationDestination)
    `when`(navigationDestination.route).thenReturn(Route.HOME)

    assertThat(navigationActions.currentRoute(), `is`(Route.HOME))
  }

  @Test
  fun currentRouteReturnsEmptyWhenNoDestination() {
    `when`(navHostController.currentDestination).thenReturn(null)

    assertThat(navigationActions.currentRoute(), `is`(""))
  }

  // Test for navigateToAndClearBackStack
  @Test
  fun navigateToAndClearBackStackClearsCorrectNumberOfEntries() {
    val mockBackStackEntry = mock(NavBackStackEntry::class.java)

    // Simulate previous entries in the back stack
    `when`(navHostController.previousBackStackEntry).thenReturn(mockBackStackEntry)

    // Call with entriesToClear = 1
    navigationActions.navigateToAndClearBackStack("NewScreen", 1)

    // Verify that popBackStack() was called once
    verify(navHostController).popBackStack()

    // Verify the navigation to the new screen
    verify(navHostController).navigate(eq("NewScreen"), any<NavOptionsBuilder.() -> Unit>())
  }

  @Test
  fun navigateToAndClearBackStackClearsCorrectNumberOfEntries2() {
    val mockBackStackEntry = mock(NavBackStackEntry::class.java)
    // Simulate that one more entry exists in the back stack
    `when`(navHostController.previousBackStackEntry).thenReturn(mockBackStackEntry)
    // Call with entriesToClear = 2
    navigationActions.navigateToAndClearBackStack("NewScreen", 2)

    // Verify that popBackStack() was called twice
    verify(navHostController, times(2)).popBackStack()

    // Verify the navigation to the new screen
    verify(navHostController).navigate(eq("NewScreen"), any<NavOptionsBuilder.() -> Unit>())
  }

  @Test
  fun navigateToAndClearBackStackWithZeroEntries() {
    // Call with entriesToClear = 0
    navigationActions.navigateToAndClearBackStack("NewScreen", 0)

    // Verify that popBackStack() was not called
    verify(navHostController, times(0)).popBackStack()

    // Verify the navigation to the new screen
    verify(navHostController).navigate(eq("NewScreen"), any<NavOptionsBuilder.() -> Unit>())
  }

  @Test
  fun navigateToAndClearAllBackStackClearsBackStack() {
    val mockBackStackEntry = mock(NavBackStackEntry::class.java)

    // Simulate previous entries in the back stack
    `when`(navHostController.previousBackStackEntry).thenReturn(mockBackStackEntry)

    navigationActions.navigateToAndClearAllBackStack("NewScreen")

    // Verify the navigation to the new screen
    verify(navHostController).navigate(eq("NewScreen"), any<NavOptionsBuilder.() -> Unit>())
  }
}
