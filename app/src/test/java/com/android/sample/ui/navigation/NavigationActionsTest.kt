package com.android.sample.ui.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
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
}
