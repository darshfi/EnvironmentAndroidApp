package com.example.envii.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.envii.AuthState
import com.example.envii.AuthViewModel
import com.example.envii.R
import com.example.envii.ui.theme.Green80
import com.example.envii.ui.theme.GreenGrey30
import com.example.envii.ui.theme.GreenGrey50
import com.example.envii.ui.theme.GreenGrey60


@Composable
fun LoginPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    var email by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }

    val authState = authViewModel.authState.observeAsState()

    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                navController.navigate("home")
            }
            is AuthState.Error -> {
                val errorMessage = (authState.value as AuthState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.border_image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.FillWidth,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Login", fontSize = 44.sp)

        Spacer(modifier = Modifier.height(20.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "Email")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenGrey30,
                focusedLabelColor = GreenGrey50
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = "Password")
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenGrey30,
                focusedLabelColor = GreenGrey50
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            authViewModel.login(email, password)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Green80,
            contentColor = Color.Black,
            disabledContainerColor = Green80.copy(alpha = 0.38f),
            disabledContentColor = GreenGrey60.copy(alpha = 0.38f)
        ),
            enabled = email.isNotEmpty() && password.isNotEmpty()
        ){
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(29.dp))

        TextButton(onClick = {
            navController.navigate("signup")
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = GreenGrey30
        )) {
            Text(text = "Don't have an account? Sign up")
        }
    }
}