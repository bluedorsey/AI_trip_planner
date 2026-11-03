package com.example.souldream.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.souldream.R
import com.example.souldream.ui.state.Async

/**
 * Non-lazy section: cross-fades between [loading] placeholders, [failure] and [content].
 * An empty list (when [T] is a Collection) can be handled inside [content] by the caller.
 */
@Composable
fun <T> AsyncContent(
    state: Async<T>,
    loading: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    failure: @Composable (message: String?) -> Unit = { SectionMessage(it ?: stringResource(R.string.section_error)) },
    content: @Composable (T) -> Unit,
) {
    AnimatedContent(
        targetState = state,
        modifier = modifier,
        // Key by state *kind* so a Success -> Success data refresh doesn't re-animate.
        contentKey = { it::class },
        transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(250)) },
        label = "asyncContent",
    ) { current ->
        when (current) {
            is Async.Loading -> loading()
            is Async.Failure -> failure(current.message)
            is Async.Success -> content(current.data)
        }
    }
}

/**
 * Lazy variant for use inside LazyColumn / LazyRow: emits [skeletonCount] placeholder
 * items while loading, then the real items (which typically animate in staggered).
 */
fun <T> LazyListScope.asyncItems(
    state: Async<List<T>>,
    skeletonCount: Int,
    key: (item: T) -> Any,
    skeleton: @Composable (index: Int) -> Unit,
    empty: (@Composable () -> Unit)? = null,
    failure: (@Composable (message: String?) -> Unit)? = null,
    content: @Composable (index: Int, item: T) -> Unit,
) {
    when (state) {
        is Async.Loading -> items(count = skeletonCount, key = { "skeleton-$it" }) { index ->
            skeleton(index)
        }

        is Async.Failure -> if (failure != null) {
            item(key = "failure") { failure(state.message) }
        }

        is Async.Success -> if (state.data.isEmpty()) {
            if (empty != null) item(key = "empty") { empty() }
        } else {
            items(
                count = state.data.size,
                key = { index -> key(state.data[index]) },
            ) { index -> content(index, state.data[index]) }
        }
    }
}

/** Small centred message used for empty / error states. */
@Composable
fun SectionMessage(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
