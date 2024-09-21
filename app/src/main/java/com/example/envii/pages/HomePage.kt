package com.example.envii.pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.envii.AuthState
import com.example.envii.AuthViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

@ExperimentalMaterial3Api
@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()
    var imageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authState.value, Unit) {
        when (authState.value) {
            is AuthState.UnAuthenticated -> {
                navController.navigate("login")
            }
            else -> Unit
        }

        getLatestImage { url ->
            imageUrl = url
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the latest image
        ImageCard(
            title = "Latest Upload",
            description = "This is the most recent image uploaded.",
            imageUrl = imageUrl,
            onClick = {
                navController.navigate("ImageFeed")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            authViewModel.signout()
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "Sign Out")
        }

        Spacer(modifier = Modifier.weight(1f))

        FloatingActionButton(
            onClick = {
                navController.navigate("camera")
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Open Camera",
                tint = Color.White
            )
        }
    }
}

@Composable
fun ImageCard(
    title: String,
    description: String,
    imageUrl: String?, // Accept imageUrl as a parameter
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = imageUrl ?: "https://picsum.photos/seed/${Random.nextInt()}/300/200"
            ),
            contentDescription = null,
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .fillMaxWidth()
                .aspectRatio(3f / 2f)
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 8.dp,
                mainAxisSize = SizeMode.Wrap
            ) {
                AssistChip(
                    onClick = { },
                    colors = AssistChipDefaults.assistChipColors(
                        leadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = "Mark as favorite")
                    }
                )
                AssistChip(
                    onClick = { },
                    colors = AssistChipDefaults.assistChipColors(
                        leadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = "Share with others")
                    }
                )
            }
        }
    }
}

fun getLatestImage(callback: (String?) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid
    val storage: FirebaseStorage = Firebase.storage
    val folderPath = "images/$userId/"
    val storageRef: StorageReference = storage.reference.child(folderPath)

    storageRef.listAll().addOnSuccessListener { listResult: ListResult ->
        val items = listResult.items
        if (items.isNotEmpty()) {
            val latestImage = items.maxByOrNull { it.name }
            latestImage?.downloadUrl?.addOnSuccessListener { uri ->
                callback(uri.toString())
            }?.addOnFailureListener {
                callback(null)
            }
        } else {
            callback(null)
        }
    }.addOnFailureListener {
        callback(null)
    }
}
