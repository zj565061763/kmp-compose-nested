package com.sd.demo.kmp.compose_nested

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteScaffold(
  modifier: Modifier = Modifier,
  title: String,
  onClickBack: () -> Unit,
  isLight: Boolean = true,
  contentPadding: PaddingValues = PaddingValues(0.dp),
  content: @Composable ColumnScope.() -> Unit,
) {
  AppTheme(isLight = isLight) {
    Scaffold(
      modifier = modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text(text = title) },
          navigationIcon = {
            IconButton(onClick = onClickBack) {
              Text(text = "<", fontSize = 24.sp)
            }
          },
        )
      },
    ) { padding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
      )
    }
  }
}