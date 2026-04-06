package app.morning.glory.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.morning.glory.core.extensions.friendly
import kotlin.time.Duration

@Composable
fun NapDurationGrid(
    durations: List<Duration>,
    onTap: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedIndex: Int = -1
) {
    val gap = 4.dp
    Column(
        verticalArrangement = Arrangement.spacedBy(gap),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(gap),
            modifier = Modifier.fillMaxWidth()
        ) {
            NapButton(
                duration = durations[0],
                onClick = { onTap(0) },
                isSelected = selectedIndex == 0,
                modifier = Modifier.weight(1f)
            )
            NapButton(
                duration = durations[1],
                onClick = { onTap(1) },
                isSelected = selectedIndex == 1,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(gap),
            modifier = Modifier.fillMaxWidth()
        ) {
            NapButton(
                duration = durations[2],
                onClick = { onTap(2) },
                isSelected = selectedIndex == 2,
                modifier = Modifier.weight(1f)
            )
            NapButton(
                duration = durations[3],
                onClick = { onTap(3) },
                isSelected = selectedIndex == 3,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NapButton(
    duration: Duration,
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RectangleShape,
        colors = if (isSelected) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            ButtonDefaults.buttonColors()
        }
    ) {
        Text(duration.friendly(), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}
