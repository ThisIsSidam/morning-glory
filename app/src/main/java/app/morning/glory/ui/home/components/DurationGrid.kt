package app.morning.glory.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DurationGrid(
    modifier: Modifier = Modifier,
    onDurationSelected: (Int) -> Unit
) {
    val durations = listOf(
        15 to "15 min",
        30 to "30 min",
        60 to "1 hr",
        120 to "2 hr"
    )

    val cornerRadius = 12.dp

    Column (
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.weight(1f)
        ) {
            DurationButton(
                text = durations[0].second,
                minutes = durations[0].first,
                onClick = onDurationSelected,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(topStart = cornerRadius)
            )
            Spacer(Modifier.width(2.dp))
            DurationButton(
                text = durations[1].second,
                minutes = durations[1].first,
                onClick = onDurationSelected,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(topEnd = cornerRadius)
            )
        }
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier.weight(1f)
        ) {
            DurationButton(
                text = durations[2].second,
                minutes = durations[2].first,
                onClick = onDurationSelected,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(bottomStart = cornerRadius)
            )
            Spacer(Modifier.width(2.dp))
            DurationButton(
                text = durations[3].second,
                minutes = durations[3].first,
                onClick = onDurationSelected,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(bottomEnd = cornerRadius)
            )
        }
    }
}

@Composable
private fun DurationButton(
    text: String,
    minutes: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(0.dp)
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary, shape)
            .clickable { onClick(minutes) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}
