package com.example.myapplication.ui.screens.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.animation.staggeredEntrance

/** Big title + subtitle; each line enters with its own stagger. */
@Composable
fun HeroHeadline(modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = stringResource(R.string.home_headline),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.staggeredEntrance(index = 1),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.home_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.staggeredEntrance(index = 2),
        )
    }
}
