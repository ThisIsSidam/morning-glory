package app.morning.glory.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumberPicker(
    valueMax: Int,
    modifier: Modifier = Modifier,
    valueMin: Int = 0,
    onValueChange: (Int) -> Unit = {},
    currentValue: Int = 0,
) {
    if (currentValue !in valueMin..valueMax) {
        throw IllegalArgumentException("currentValue($currentValue) must be between valueMin($valueMin) and valueMax($valueMax)")
    }
    val selectedNumber = remember { mutableIntStateOf(currentValue) }

    LazyColumn(
        modifier = modifier.height(200.dp)) {
        items((valueMin..valueMax).toList()) { number ->
            Text(
                text = "$number",
                fontSize = 24.sp,
                modifier = Modifier.clickable {
                    selectedNumber.intValue = number
                    onValueChange(number)
                }
                    .background(color = if (selectedNumber.intValue == number) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    })
            )
        }
    }
}