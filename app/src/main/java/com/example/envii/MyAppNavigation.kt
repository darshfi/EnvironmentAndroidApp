package com.example.envii

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.envii.pages.presentation.CameraScreen
import com.example.envii.pages.HomePage
import com.example.envii.pages.LoginPage
import com.example.envii.pages.SignupPage
import com.example.envii.pages.presentation.CameraViewModel
import com.example.envii.pages.presentation.PreviewScreen

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel, activity: Activity) {
    val navController = rememberNavController()
    val cameraViewModel: CameraViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignupPage(modifier, navController, authViewModel)
        }
        composable("home") {
            HomePage(modifier, navController, authViewModel)
        }
        composable("camera") {
            CameraScreen(modifier, navController, cameraViewModel, activity)
        }
        composable("preview"){
            PreviewScreen(modifier, navController)
        }
    })
}