package com.example.envii.pages.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ConfirmationScreen(navController: NavController) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        /*
            Image(
                //bitmap = it.asImageBitmap(),
                contentDescription = "Photo Preview",
                modifier = Modifier.size(300.dp)
            )
        */

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ){
                // Buttons for Decline, Go Home, Accept Photo
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .size(45.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            navController.navigate("camera")
                        },
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = "Decline Photo",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(1.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .size(45.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            navController.navigate("home")
                        },
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Go Home",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(1.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .size(45.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            // Logic for accepting the photo
                        },
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Accept Photo",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
