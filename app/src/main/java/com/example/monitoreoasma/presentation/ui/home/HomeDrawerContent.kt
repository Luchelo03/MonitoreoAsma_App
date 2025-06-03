package com.example.monitoreoasma.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.List


@Composable
fun HomeDrawerContent(
    userName: String,
    onOptionSelected: (String) -> Unit
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val drawerWidth = (screenWidthDp * 2 / 3).dp

    Column(
        modifier = Modifier
            .width(drawerWidth) // ✅ Ahora solo ocupa 2/3 de la pantalla
            .fillMaxHeight()
            .background(Color(0xFFE1F5FE)) // Fondo celeste claro
            .padding(WindowInsets.statusBars.asPaddingValues()) // Respeta status bar
            .padding(16.dp)
    ) {
        // 🔵 Encabezado con fondo coloreado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFB3E5FC), shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberVectorPainter(Icons.Default.Person),
                    contentDescription = "Usuario",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp)) // Separación debajo del perfil

        DrawerItem("Inicio", Icons.Default.Home) { onOptionSelected("home") }
        DrawerItem("Realizar Test ahora", Icons.Default.Star) { onOptionSelected("test") }
        DrawerItem("Ver historial a detalle", Icons.AutoMirrored.Filled.List) { onOptionSelected("historial") }
        DrawerItem("Recomendaciones médicas", Icons.Default.Info) { onOptionSelected("recomendaciones") }
        DrawerItem("Configuración de la app", Icons.Default.Settings) { onOptionSelected("configuracion") }
    }
}


@Composable
private fun DrawerItem(text: String, icon: ImageVector, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp) // ✔️ Más separación
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)) // fondo suave
            .clickable(
                interactionSource = interactionSource,
                indication = null, // usa ripple por defecto del tema Material 3
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeDrawerContentPreview() {
    HomeDrawerContent(
        userName = "usuario",
        onOptionSelected = {}
    )
}
