package cl.duoc.dsy1105.mastermartini.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Esquema de color oscuro accesible (Material 3 - IE 2.1.1)
private val DarkColorScheme = darkColorScheme(
    primary = MartiniRedLight,
    secondary = MartiniCacaoLight,
    tertiary = MartiniGoldLight
)

// Esquema de color corporativo claro con alto contraste para adultos mayores (Punto 3.3)
private val LightColorScheme = lightColorScheme(
    primary = MartiniBurgundy,
    onPrimary = Color.White,
    primaryContainer = MartiniPrimaryContainer,
    onPrimaryContainer = MartiniOnPrimaryContainer,
    secondary = MartiniDarkCacao,
    onSecondary = Color.White,
    tertiary = MartiniGold,
    background = MartiniCreamBg,
    surface = Color.White
)

/**
 * Tema principal Material 3 de MasterMartiniApp.
 * Se desactiva dynamicColor para garantizar la identidad visual del socio formador (Punto 6).
 */
@Composable
fun MasterMartiniAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}