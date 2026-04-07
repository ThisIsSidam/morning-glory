package app.morning.glory.ui.home.components.sheets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.morning.glory.core.extensions.friendly
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.shared.components.SaveCloseButtons
import app.morning.glory.ui.home.components.NapDurationGrid
import kotlin.time.Duration.Companion.minutes

@Composable
fun NapDurationsSheet(
    onDismiss: () -> Unit
) {
    val durations = remember {
        mutableStateListOf(
            AppPreferences.napDuration1,
            AppPreferences.napDuration2,
            AppPreferences.napDuration3,
            AppPreferences.napDuration4
        )
    }

    var selectedIndex by remember { mutableIntStateOf(-1) }

    val presetDurations = remember {
        listOf(
            5.minutes, 10.minutes, 15.minutes, 20.minutes, 25.minutes, 30.minutes,
            40.minutes, 45.minutes, 50.minutes, 60.minutes, 75.minutes, 90.minutes,
            105.minutes, 120.minutes, 150.minutes, 180.minutes
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (selectedIndex == -1) "Edit Nap Durations" else "Select Duration",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        )

        AnimatedContent(
            targetState = selectedIndex,
            transitionSpec = {
                // If transitioning from/to -1, use fade animation
                fadeIn() togetherWith fadeOut()
            },
            label = "NapDurationsTransition"
        ) { targetIndex ->
            if (targetIndex == -1) {
                NapDurationGrid(
                    durations = durations,
                    selectedIndex = targetIndex,
                    onTap = { index ->
                        selectedIndex = if (selectedIndex == index) -1 else index
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 80.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(presetDurations) { duration ->
                        OutlinedButton(
                            onClick = {
                                durations[targetIndex] = duration
                                selectedIndex = -1
                            },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                            colors = if (durations[targetIndex] == duration) {
                                ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            } else {
                                ButtonDefaults.outlinedButtonColors()
                            }
                        ) {
                            val text = duration.friendly()
                            Text(
                                text = text,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        SaveCloseButtons(
            onSave = {
                AppPreferences.napDuration1 = durations[0]
                AppPreferences.napDuration2 = durations[1]
                AppPreferences.napDuration3 = durations[2]
                AppPreferences.napDuration4 = durations[3]
                onDismiss()
            },
            onClose = onDismiss
        )
    }
}
