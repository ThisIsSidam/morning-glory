package app.morning.glory.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.morning.glory.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeManagerSheetBody() {
    val savedQRs = remember { mutableListOf<String>() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(400.dp) // Adjust height as needed
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (savedQRs.isEmpty()) {
                "Scan QR Codes to save them here"
            } else {
                "Manage your saved QR Codes"
            },
        )
        Spacer(modifier = Modifier.height(16.dp))

        savedQRs.forEach { qrCode ->
            QRCodeTile(qrCode = qrCode)
        }

        AddNewQRCode()
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun QRCodeTile(
    qrCode: String,
) {
    ListItem(
        headlineContent = { Text(text = qrCode) },
        trailingContent = { IconButton(
            onClick = { /* Handle QR Code deletion */ }
        )  {
            Icon(
                painter = painterResource(id = R.drawable.outline_delete_24),
                contentDescription = "Delete QR Code Data")
            }
        },

    )
}

@Composable
fun AddNewQRCode() {
    Button(
        onClick = { /* Handle adding a new QR Code */ },
        modifier = Modifier.height(48.dp)
    ) {
        Text(text = "Add New QR Code")
    }
}