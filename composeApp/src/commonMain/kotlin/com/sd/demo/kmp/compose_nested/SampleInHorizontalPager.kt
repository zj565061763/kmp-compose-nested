package com.sd.demo.kmp.compose_nested

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.sd.lib.kmp.compose_nested.NestedHeader

@Composable
fun SampleInHorizontalPager(
  onClickBack: () -> Unit,
) {
  RouteScaffold(
    title = "SampleInHorizontalPager",
    onClickBack = onClickBack,
  ) {
    val pagerState = rememberPagerState { 2 }
    HorizontalPager(
      modifier = Modifier.fillMaxSize(),
      state = pagerState,
      beyondViewportPageCount = pagerState.pageCount,
    ) { index ->
      if (index == 0) {
        FirstPageView(
          modifier = Modifier.nestedScroll(TestNestedScrollConnection)
        )
      } else {
        VerticalListView(
          count = 100,
          modifier = Modifier.nestedScroll(TestNestedScrollConnection)
        )
      }
    }
  }
}

@Composable
private fun FirstPageView(
  modifier: Modifier = Modifier,
) {
  NestedHeader(
    modifier = modifier.fillMaxSize(),
    header = { TestHeaderView() }
  ) {
    VerticalListView(count = 50)
  }
}