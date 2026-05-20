package app.morning.glory.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.morning.glory.ui.home.components.HomeAppBar

enum class HomeView(val title: String) {
    SLEEP("Sleep"),
    NAP("Nap"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var showNightClock by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState { HomeView.entries.size }

    if (showNightClock) {
        NightClockView(onBack = { showNightClock = false })
    } else if (showSettings) {
        SettingsView(onBack = { showSettings = false })
    } else {
        Scaffold(
            topBar = {
                HomeAppBar(
                    pagerState = pagerState,
                    onClockClick = { showNightClock = true },
                    onSettingsClick = { showSettings = true }
                )
            }
        ) { innerPadding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.padding(innerPadding)
            ) { page ->

                val view = HomeView.entries[page]

                when (view) {
                    HomeView.SLEEP -> SleepView()
                    HomeView.NAP -> NapView()
                }
            }
        }
    }
}
