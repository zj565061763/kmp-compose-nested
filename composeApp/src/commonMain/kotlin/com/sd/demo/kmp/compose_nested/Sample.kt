package com.sd.demo.kmp.compose_nested

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sd.lib.kmp.compose_nested.NestedHeader

@Composable
fun Sample(
  onClickBack: () -> Unit,
) {
  RouteScaffold(
    title = "Sample",
    onClickBack = onClickBack,
  ) {
    NestedHeader(
      modifier = Modifier.fillMaxSize(),
      header = { TestHeaderView() }
    ) {
      VerticalListView(count = 50)
    }
  }
}