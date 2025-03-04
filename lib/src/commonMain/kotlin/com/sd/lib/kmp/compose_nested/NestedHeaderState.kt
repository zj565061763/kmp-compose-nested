package com.sd.lib.kmp.compose_nested

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.splineBasedDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlin.math.absoluteValue

@Composable
fun rememberNestedHeaderState(
  initialOffset: Float? = null,
): NestedHeaderState {
  return rememberSaveable(saver = NestedHeaderState.Saver) {
    NestedHeaderState(initialOffset = initialOffset)
  }
}

class NestedHeaderState internal constructor(
  private var initialOffset: Float? = null,
) {
  var isReady by mutableStateOf(false)
    private set

  var offset by mutableFloatStateOf(0f)

  private var _minOffset = 0f
  private val _maxOffset = 0f
  private val _anim = Animatable(0f)

  private var _headerFlingJob: Job? = null
  private var _contentFlingJob: Job? = null

  internal val headerNestedScrollConnection = object : NestedScrollConnection {}
  internal val headerNestedScrollDispatcher = NestedScrollDispatcher()

  internal val contentNestedScrollConnection: NestedScrollConnection = NestedScrollConnectionY(
    onPreScroll = { scrollToHide(it) },
    onPostScroll = { scrollToShow(it) },
    onPreFling = {
      _contentFlingJob = currentCoroutineContext()[Job]
      false
    }
  )

  fun scrollToContent() {
    cancelHeaderFling()
    cancelContentFling()
    offset = _minOffset
  }

  internal fun setSize(header: Int, content: Int, container: Int) {
    _minOffset = if (content < container) {
      val bottom = header + content
      val delta = container - bottom
      delta.toFloat().coerceAtMost(0f)
    } else {
      -header.toFloat()
    }

    initialOffset?.also {
      initialOffset = null
      offset = it.coerceIn(_minOffset, _maxOffset)
    }

    isReady = header > 0
  }

  private fun scrollToHide(value: Float): Boolean {
    if (!isReady) return false
    if (value < 0 && offset > _minOffset) {
      val newOffset = offset + value
      offset = newOffset.coerceAtLeast(_minOffset)
      return true
    }
    return false
  }

  private fun scrollToShow(value: Float): Boolean {
    if (!isReady) return false
    if (value > 0 && offset < _maxOffset) {
      val newOffset = offset + value
      offset = newOffset.coerceAtMost(_maxOffset)
      return true
    }
    return false
  }

  internal suspend fun dispatchHeaderFling(velocity: Float, density: Density) {
    if (!isReady) return

    @Suppress("NAME_SHADOWING")
    val velocity = velocity.takeIf { it.absoluteValue > 300 } ?: 0f

    _headerFlingJob = currentCoroutineContext()[Job]

    val available = Velocity(0f, velocity)
    val preConsumed = headerNestedScrollDispatcher.dispatchPreFling(available).consumedCoerceIn(available)

    val left = available - preConsumed

    try {
      if (left != Velocity.Zero) {
        _anim.updateBounds(lowerBound = _minOffset, upperBound = _maxOffset)
        _anim.snapTo(offset)

        var lastValue = _anim.value
        _anim.animateDecay(
          initialVelocity = left.y,
          animationSpec = splineBasedDecay(density),
        ) {
          val delta = value - lastValue
          lastValue = value
          dispatchHeaderNestedScroll(
            available = delta,
            source = NestedScrollSource.SideEffect,
          )
        }
      }
    } finally {
      headerNestedScrollDispatcher.dispatchPostFling(left, Velocity.Zero)
    }
  }

  internal fun dispatchHeaderNestedScroll(
    available: Float,
    source: NestedScrollSource,
  ) {
    if (available == 0f) return
    headerNestedScrollDispatcher.dispatchScroll(
      available = Offset(0f, available),
      source = source,
    ) { left ->
      val leftValue = left.y
      when {
        leftValue < 0 -> scrollToHide(leftValue)
        leftValue > 0 -> scrollToShow(leftValue)
        else -> false
      }
    }
  }

  internal fun cancelHeaderFling(): Boolean {
    val job = _headerFlingJob ?: return false
    _headerFlingJob = null
    return job.isActive.also { isActive ->
      if (isActive) {
        job.cancel()
      }
    }
  }

  internal fun cancelContentFling(): Boolean {
    val job = _contentFlingJob ?: return false
    _contentFlingJob = null
    return job.isActive.also { isActive ->
      if (isActive) {
        job.cancel()
      }
    }
  }

  companion object {
    internal val Saver = listSaver(
      save = { listOf(it.offset) },
      restore = { NestedHeaderState(initialOffset = it[0]) }
    )
  }
}

private inline fun NestedScrollDispatcher.dispatchScroll(
  available: Offset,
  source: NestedScrollSource,
  onScroll: (Offset) -> Boolean,
) {
  val preConsumed = dispatchPreScroll(
    available = available,
    source = source,
  ).consumedCoerceIn(available)

  val left = available - preConsumed
  val isConsumed = if (left != Offset.Zero) {
    onScroll(left)
  } else {
    false
  }

  dispatchPostScroll(
    consumed = if (isConsumed) left else Offset.Zero,
    available = if (isConsumed) Offset.Zero else left,
    source = source,
  )
}

private fun Offset.consumedCoerceIn(available: Offset): Offset {
  val consumedX = x.consumedCoerceIn(available.x)
  val consumedY = y.consumedCoerceIn(available.y)
  return if (x == consumedX && y == consumedY) {
    this
  } else {
    this.copy(x = consumedX, y = consumedY)
  }
}

private fun Velocity.consumedCoerceIn(available: Velocity): Velocity {
  val consumedX = x.consumedCoerceIn(available.x)
  val consumedY = y.consumedCoerceIn(available.y)
  return if (x == consumedX && y == consumedY) {
    this
  } else {
    this.copy(x = consumedX, y = consumedY)
  }
}

private fun Float.consumedCoerceIn(available: Float): Float {
  return when {
    available > 0f -> coerceIn(0f, available)
    available < 0f -> coerceIn(available, 0f)
    else -> 0f
  }
}

private class NestedScrollConnectionY(
  val onPreScroll: (Float) -> Boolean,
  val onPostScroll: (Float) -> Boolean,
  val onPreFling: suspend (Float) -> Boolean,
) : NestedScrollConnection {
  override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
    return if (onPreScroll(available.y)) {
      available.copy(x = 0f)
    } else {
      super.onPreScroll(available, source)
    }
  }

  override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
    return if (onPostScroll(available.y)) {
      available.copy(x = 0f)
    } else {
      super.onPostScroll(consumed, available, source)
    }
  }

  override suspend fun onPreFling(available: Velocity): Velocity {
    return if (onPreFling(available.y)) {
      available.copy(x = 0f)
    } else {
      super.onPreFling(available)
    }
  }
}