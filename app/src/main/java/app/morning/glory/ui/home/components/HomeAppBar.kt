package app.morning.glory.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.morning.glory.R
import app.morning.glory.ui.home.HomeView
import app.morning.glory.ui.home.components.sheets.QRCodeManagerSheetBody
import app.morning.glory.ui.home.components.sheets.RingtoneManagerSheetBody
import kotlinx.coroutines.launch

enum class HomeSheet {
    QRSheet,
    RingtoneSheet,
    NONE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    pagerState: PagerState
) {
    var coroutineScope = rememberCoroutineScope()

    var showHomeSheet by remember { mutableStateOf(HomeSheet.NONE) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    if (showHomeSheet != HomeSheet.NONE)
        ModalBottomSheet(
            onDismissRequest = { showHomeSheet = HomeSheet.NONE },
            sheetState = sheetState,
        ) {
            when (showHomeSheet) {
                HomeSheet.QRSheet -> QRCodeManagerSheetBody()
                HomeSheet.RingtoneSheet -> RingtoneManagerSheetBody()
                HomeSheet.NONE -> {}
            }
        }

    Column {
        TopAppBar(
            title = {
                Text("Morning Glory")
            },
            actions = {
                if (pagerState.currentPage == 0) // Change index if HomeView order changed
                    IconButton(onClick = {
                        showHomeSheet = HomeSheet.QRSheet
                    }) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = stringResource(R.string.lock_icon_content_description)
                        )
                    }

                IconButton(onClick = {
                    showHomeSheet = HomeSheet.RingtoneSheet
                }) {
                    Icon(
                        painter = painterResource(R.drawable.outline_queue_music_24),
                        contentDescription = stringResource(R.string.lock_icon_content_description)
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
            divider = {}

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