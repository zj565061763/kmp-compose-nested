package com.sd.demo.kmp.compose_nested

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sd.lib.kmp.compose_nested.NestedHeader

@Composable
fun SampleShortContent(
  onClickBack: () -> Unit,
) {
  RouteScaffold(
    title = "SampleShortContent",
    onClickBack = onClickBack,
  ) {
    NestedHeader(
      modifier = Modifier.fillMaxSize(),
      header = { HeaderView(modifier = Modifier.height(200.dp)) },
    ) {
      VerticalListView(count = 3)
    }
  }
}

@Composable
private fun HeaderView(
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(Color.Red),
    contentAlignment = Alignment.Center,
  ) {
    Text(text = "header")
  }
}