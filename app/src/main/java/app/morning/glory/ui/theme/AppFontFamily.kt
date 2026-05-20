package app.morning.glory.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import app.morning.glory.R

class AppFontFamily {
    companion object {
        val JosefinSans = FontFamily(
            Font(R.font.josefin_sans_regular, FontWeight.Normal),
            Font(R.font.josefin_sans_medium, FontWeight.Medium),
            Font(R.font.josefin_sans_semibold, FontWeight.SemiBold),
            Font(R.font.josefin_sans_bold, FontWeight.Bold)
        )

        val Orbitron = FontFamily(
            Font(R.font.orbitron_regular, FontWeight.Normal),
            Font(R.font.orbitron_bold, FontWeight.Bold),
        )

    }
}