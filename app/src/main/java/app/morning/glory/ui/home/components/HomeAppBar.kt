package app.morning.glory.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.morning.glory.ui.home.HomeView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    pagerState: PagerState,
    onClockClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column {
        TopAppBar(
            title = {
                Text("Morning Glory")
            },
            actions = {
                IconButton(onClick = onClockClick) {
                    Icon(
                        imageVector = Icons.Default.AccessTimeFilled,
                        contentDescription = "Night Clock"
                    )
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.primary
            ),
        )

        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            divider = {},
            modifier = Modifier.padding(8.dp)
        ) {
            HomeView.entries.forEachIndexed { index, view ->

                val isSelected = pagerState.currentPage == index

                HomeTab(
                    isSelected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    view = view
                )
            }
        }
    }
}

@Composable
fun HomeTab(
    isSelected: Boolean,
    onClick: () -> Unit,
    view: HomeView,
) {
    Tab(
        selected = isSelected,
        onClick = onClick,
        text = {
            Text(
                view.title,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        },
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(24))
            .background(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                }
            )
    )
}