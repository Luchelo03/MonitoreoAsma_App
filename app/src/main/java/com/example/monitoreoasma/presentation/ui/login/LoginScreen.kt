package com.example.monitoreoasma.presentation.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> },
    onRegisterClick: () -> Unit = {},
    isLoading: Boolean = false,
    errorMsg: String? = null // <--- nuevo
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Contenido principal del Login
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Títulos
            Text(
                text = "Predicción y monitoreo",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(
                text = "de crisis asmáticas",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico:") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña:") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Ingresar
            Button(
                onClick = { onLoginClick(email, password) },
                shape = MaterialTheme.shapes.small,
                enabled = !isLoading // <--- evita dobles clicks
            ) {
                Text("Ingresar")
            }
            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMsg,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto inferior
            Text(
                buildAnnotatedString {
                    append("¿No tienes una cuenta? presiona ")
                    withStyle(style = SpanStyle(color = Color.Blue, fontWeight = FontWeight.Bold)) {
                        append("aquí")
                    }
                    append(" para configurar una nueva.")
                },
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { onRegisterClick() }
                    .padding(top = 8.dp)
            )
        }

        // Capa gris de carga (overlay)
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Cargando perfil,\nespere un momento.",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color.White,
                        trackColor = Color.Gray
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(isLoading = false) // Puedes poner en false para ver la vista normal
}
