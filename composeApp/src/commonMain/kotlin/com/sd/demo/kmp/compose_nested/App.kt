package com.sd.demo.kmp.compose_nested

import androidx.compose.foundation.ComposeFoundationFlags
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun App() {
  ComposeFoundationFlags.NewNestedFlingPropagationEnabled = false
  MaterialTheme {
    val navController = rememberNavController()
    NavHost(
      navController = navController,
      startDestination = AppRoute.Home,
    ) {
      composable<AppRoute.Home> {
        RouteHome(
          onClickSample = { navController.navigate(AppRoute.Sample) },
          onClickSampleShortHeader = { navController.navigate(AppRoute.SampleShortHeader) },
          onClickSampleInHorizontalPager = { navController.navigate(AppRoute.SampleInHorizontalPager) },
        )
      }
      composable<AppRoute.Sample> { Sample(onClickBack = { navController.popBackStack() }) }
      composable<AppRoute.SampleShortHeader> { SampleShortHeader(onClickBack = { navController.popBackStack() }) }
      composable<AppRoute.SampleInHorizontalPager> { SampleInHorizontalPager(onClickBack = { navController.popBackStack() }) }
    }
  }
}

expect fun logMsg(tag: String = "kmp-compose-nested", block: () -> String)