package com.sd.lib.kmp.compose_nested

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import kotlinx.coroutines.launch

@Composable
fun NestedHeader(
  modifier: Modifier = Modifier,
  state: NestedHeaderState = rememberNestedHeaderState(),
  scrollBehavior: NestedHeaderScrollBehavior = NestedHeaderScrollBehavior.Scroll,
  header: @Composable () -> Unit,
  content: @Composable () -> Unit,
) {
  Layout(
    modifier = modifier,
    content = {
      HeaderBox(state = state, header = header)
      ContentBox(state = state, content = content)
    },
  ) { measurables, constraints ->
    val cs = constraints.copy(minWidth = 0, minHeight = 0)

    val headerPlaceable = measurables[0].measure(cs.copy(maxHeight = Constraints.Infinity))
    val contentPlaceable = measurables[1].measure(cs)

    val layoutHeight = if (constraints.hasFixedHeight) {
      cs.maxHeight
    } else {
      cs.constrainHeight(headerPlaceable.height + contentPlaceable.height)
    }

    state.setSize(
      header = headerPlaceable.height,
      content = contentPlaceable.height,
      container = layoutHeight,
    )

    layout(constraints.maxWidth, layoutHeight) {
      val offset = state.offset.toInt()
      when (scrollBehavior) {
        NestedHeaderScrollBehavior.Scroll -> headerPlaceable.placeRelative(0, offset)
        NestedHeaderScrollBehavior.Fixed -> headerPlaceable.placeRelative(0, 0)
      }
      contentPlaceable.placeRelative(0, headerPlaceable.height + offset)
    }
  }
}

@Composable
private fun ContentBox(
  state: NestedHeaderState,
  content: @Composable () -> Unit,
) {
  val modifier = if (state.isReady) {
    Modifier
      .nestedScroll(state.contentNestedScrollConnection)
      .pointerInput(state) {
        awaitEachGesture {
          val down = awaitFirstDown(
            requireUnconsumed = false,
            pass = PointerEventPass.Initial,
          )
          if (state.cancelHeaderFling()) {
            down.consume()
          }
        }
      }
  } else Modifier

  Box(modifier = modifier) {
    content()
  }
}

@Composable
private fun HeaderBox(
  state: NestedHeaderState,
  header: @Composable () -> Unit,
) {
  val modifier = if (state.isReady) {
    Modifier
      .headerGesture(state)
      .nestedScroll(
        connection = state.headerNestedScrollConnection,
        dispatcher = state.headerNestedScrollDispatcher,
      )
  } else Modifier

  Box(modifier = modifier) {
    header()
  }
}

private fun Modifier.headerGesture(
  state: NestedHeaderState,
): Modifier = composed {
  val coroutineScope = rememberCoroutineScope()
  val velocityTracker = remember { VelocityTracker() }
  pointerInput(state) {
    detectVerticalDragGestures(
      onDragStart = { velocityTracker.resetTracking() },
      onDragEnd = {
        val velocity = velocityTracker.calculateVelocity().y
        coroutineScope.launch {
          state.dispatchHeaderFling(velocity, density = this@pointerInput)
        }
      },
    ) { change, dragAmount ->
      change.consume()
      velocityTracker.addPointerInputChange(change)
      state.dispatchHeaderNestedScroll(
        available = dragAmount,
        source = NestedScrollSource.UserInput,
      )
    }
  }
    .pointerInput(state) {
      awaitEachGesture {
        val down = awaitFirstDown(
          requireUnconsumed = false,
          pass = PointerEventPass.Initial,
        )

        val cancelHeaderFling = state.cancelHeaderFling()
        val cancelContentFling = state.cancelContentFling()
        if (cancelHeaderFling || cancelContentFling) {
          down.consume()
        }
      }
    }
}