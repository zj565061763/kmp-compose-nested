package com.sd.demo.kmp.compose_nested

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

@Composable
fun VerticalListView(
  modifier: Modifier = Modifier,
  count: Int,
) {
  LazyColumn(
    modifier = modifier
      .fillMaxWidth()
      .background(Color.Gray),
  ) {
    items(count) { index ->
      Button(
        onClick = { logMsg { "click $index" } },
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(text = index.toString())
      }
    }
  }
}

@Composable
fun HorizontalListView(
  modifier: Modifier = Modifier,
  count: Int = 50,
  height: Dp = 200.dp,
) {
  LazyRow(
    modifier = modifier
      .fillMaxWidth()
      .background(Color.Gray),
  ) {
    items(count) { index ->
      Button(
        onClick = { logMsg { "click $index" } },
        modifier = Modifier.height(height)
      ) {
        Text(text = index.toString())
      }
    }
  }
}

@Composable
fun TestHeaderView(
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .background(Color.Red)
        .clickable {
          logMsg { "click Red" }
        }
    )

    HorizontalListView()

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(500.dp)
        .background(Color.Green)
    )

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(500.dp)
        .background(Color.Blue)
        .clickable {
          logMsg { "click Blue" }
        }
    )
  }
}

val TestNestedScrollConnection = object : NestedScrollConnection {
  override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
    logMsg { "1111111111 (${available.y}) $source" }
    return super.onPreScroll(available, source)
  }

  override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
    logMsg { "2222222222 (${available.y}) (${consumed.y}) $source" }
    return super.onPostScroll(consumed, available, source)
  }

  override suspend fun onPreFling(available: Velocity): Velocity {
    logMsg { "++++++++++++++++++++++++++++++++++++++++++++++++++ (${available.y})" }
    return super.onPreFling(available)
  }

  override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
    logMsg { "-------------------------------------------------- (${available.y}) (${consumed.y})" }
    return super.onPostFling(consumed, available)
  }
}