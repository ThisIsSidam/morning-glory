package app.morning.glory.ui.home.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun RListItem(
    modifier: Modifier = Modifier,
    headlineContent: @Composable (() -> Unit),
    leadingContent: @Composable (() -> Unit)? = null,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    shapeDp: Int = 16,
) {
    ListItem(
        modifier = modifier
            .clip(RoundedCornerShape(shapeDp.dp)),
        headlineContent = headlineContent,
        leadingContent = leadingContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        trailingContent = trailingContent
    )
}
