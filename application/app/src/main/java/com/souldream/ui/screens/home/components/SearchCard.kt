package com.souldream.ui.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.souldream.R
import com.souldream.model.TripCategory
import com.souldream.ui.animation.pulse
import com.souldream.ui.animation.pressScale
import com.souldream.ui.animation.staggeredEntrance
import com.souldream.ui.components.SkeletonLine
import com.souldream.ui.components.skeletonSemantics
import com.souldream.ui.components.asyncItems
import com.souldream.ui.state.Async
import com.souldream.ui.theme.GlassWhiteStrong
import com.souldream.ui.theme.DividerSoft
import com.souldream.ui.theme.button_colour
import com.souldream.ui.theme.selected_icon
import com.souldream.ui.theme.selected_slider

/**
 * The AI prompt box: sparkle + text field + send button, with category chips below.
 * The card lifts and gets an accent border when focused; the send button springs to
 * life once there is something to send.
 */
@Composable
fun SearchCard(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    categories: Async<List<TripCategory>>,
    selectedCategoryId: String?,
    onCategoryClick: (TripCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(32.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val elevation by animateDpAsState(if (focused) 28.dp else 18.dp, label = "searchElevation")
    val borderColor by animateColorAsState(
        targetValue = if (focused) button_colour.copy(alpha = 0.6f) else Color.Transparent,
        label = "searchBorder",
    )
    val canSubmit = query.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation, shape, ambientColor = selected_icon, spotColor = selected_icon)
            .clip(shape)
            .background(GlassWhiteStrong)
            .border(1.5.dp, borderColor, shape)
            .padding(top = 12.dp, bottom = 14.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(selected_slider),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = selected_icon,
                    modifier = Modifier.size(24.dp).pulse(),
                )
            }
            Spacer(Modifier.width(14.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                interactionSource = interactionSource,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                cursorBrush = SolidColor(button_colour),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { if (canSubmit) onSubmit() }),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.home_search_hint),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            Spacer(Modifier.width(8.dp))
            SubmitButton(enabled = canSubmit, onClick = onSubmit)
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            color = DividerSoft,
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            asyncItems(
                state = categories,
                skeletonCount = 4,
                key = { it.id },
                skeleton = {
                    SkeletonLine(Modifier.width(112.dp).skeletonSemantics(), height = 42.dp)
                },
            ) { index, category ->
                CategoryChip(
                    category = category,
                    selected = category.id == selectedCategoryId,
                    onClick = { onCategoryClick(category) },
                    modifier = Modifier.staggeredEntrance(index = index, offsetY = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun SubmitButton(enabled: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.88f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "submitScale",
    )
    val container by animateColorAsState(
        targetValue = if (enabled) button_colour else button_colour.copy(alpha = 0.55f),
        label = "submitContainer",
    )
    val nudge by animateFloatAsState(
        targetValue = if (enabled) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow),
        label = "submitNudge",
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pressScale(onClick, enabled = enabled)
            .size(52.dp)
            .clip(CircleShape)
            .background(container),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = stringResource(R.string.cd_plan_trip),
            tint = Color.White,
            modifier = Modifier
                .size(26.dp)
                .graphicsLayer { translationX = nudge * 2.dp.toPx() },
        )
    }
}
