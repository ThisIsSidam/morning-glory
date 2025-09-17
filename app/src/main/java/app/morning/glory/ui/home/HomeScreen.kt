package app.morning.glory.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import app.morning.glory.ui.home.components.HomeAppBar

/// Handles notification permission requests and displays the HomeView.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current

    var showSettingsDialog by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                context.findActivity(),
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (!shouldShowRationale) { showSettingsDialog = true }
        }
    }

    // Request permission on first load
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME && showSettingsDialog) {

                    // Re-check permission
                    val granted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (granted) {
                        showSettingsDialog = false
                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { /* block outside dismiss */ },
            title = { Text("Permission Required") },
            text = { Text("We need to show you notifications to properly wake you up from your sleep. The app can't run without it.") },
            confirmButton = {
                TextButton(onClick = { openAppSettings(context) }) {
                    Text("Allow")
                }
            },
            dismissButton = {
                TextButton(onClick = { context.findActivity().finish() }) {
                    Text("Close")
                }
            }
        )
    }

    HomeView()
}

private fun openAppSettings(context: android.content.Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

// Helper extension: get Activity from Context inside Compose
fun android.content.Context.findActivity(): android.app.Activity {
    var ctx = this
    while (ctx is android.content.ContextWrapper) {
        if (ctx is android.app.Activity) return ctx
        ctx = ctx.baseContext
    }
    throw IllegalStateException("Activity not found in context chain.")
}

enum class HomeViewType(val title: String) {
    SLEEP("Sleep"),
    NAP("Nap")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView() {
    val pagerState = rememberPagerState { HomeViewType.entries.size }

    Scaffold(
        topBar = { HomeAppBar(pagerState) }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (HomeViewType.entries[page]) {
                HomeViewType.SLEEP -> SleepView()
                HomeViewType.NAP -> NapView()
            }
        }
    }
}