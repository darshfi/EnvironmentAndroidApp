package com.example.envii.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.envii.AuthState
import com.example.envii.AuthViewModel
import com.example.envii.ui.theme.*

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.UnAuthenticated -> {
                navController.navigate("login")
            }
            else -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            authViewModel.signout()
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Green80,
                contentColor = Color.Black,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f)
            )
        ) {
            Text(text = "Sign Out")
        }

        Spacer(modifier = Modifier.weight(1f))

        FloatingActionButton(
            onClick = {
                // Navigate to the camera screen
                navController.navigate("camera")
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt, // Use a camera icon
                contentDescription = "Open Camera",
                tint = Color.White
            )
        }
    }
}