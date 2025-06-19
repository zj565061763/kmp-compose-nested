package com.sd.demo.kmp.compose_nested

import kotlinx.serialization.Serializable

sealed interface AppRoute {
  @Serializable
  data object Home : AppRoute

  @Serializable
  data object Sample : AppRoute

  @Serializable
  data object SampleShortHeader : AppRoute

  @Serializable
  data object SampleShortContent : AppRoute

  @Serializable
  data object SampleInHorizontalPager : AppRoute
}