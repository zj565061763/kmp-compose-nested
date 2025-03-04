package com.sd.demo.kmp.compose_nested

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.sd.lib.kmp.compose_nested.NestedHeaderScrollBehavior
import com.sd.lib.kmp.compose_nested.rememberNestedHeaderState

@Composable
fun SampleShortHeader(
  onClickBack: () -> Unit,
) {
  val state = rememberNestedHeaderState()
  RouteScaffold(
    title = "SampleShortHeader",
    onClickBack = onClickBack,
  ) {
    NestedHeader(
      modifier = Modifier.fillMaxSize(),
      scrollBehavior = NestedHeaderScrollBehavior.Fixed,
      header = {
        HeaderView(modifier = Modifier.clickable {
          state.scrollToContent()
        })
      },
    ) {
      VerticalListView(count = 50)
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
      .height(300.dp)
      .background(Color.Red),
    contentAlignment = Alignment.Center,
  ) {
    Text(text = "header")
  }
}