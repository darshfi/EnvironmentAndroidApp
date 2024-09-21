package com.example.envii.pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

@Composable
fun ImageFeed(navController: NavHostController) {
    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        fetchRandomImages { urls ->
            imageUrls = urls
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(imageUrls.size) { index ->
                ImageCard(
                    title = "Random Image ${index + 1}",
                    description = "This is a random image from Firebase.",
                    imageUrl = imageUrls[index],
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .size(45.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {

                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Open Settings",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .size(45.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {

                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Shopping Cart",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
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
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Open Camera",
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
        }
    }
}


@Composable
fun ImageCard(
    title: String,
    description: String,
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(
                    model = imageUrl ?: "https://picsum.photos/seed/${Random.nextInt()}/300/200",
                    onError = { Log.e("ImageCard", "Failed to load image: ${it.result.throwable}") }
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
                        onClick = { /* Do nothing */ },
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
                        onClick = { /* Do nothing */ },
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
}

fun fetchRandomImages(callback: (List<String>) -> Unit) {
    val storage: FirebaseStorage = Firebase.storage
    val storageRef = storage.reference.child("images/") // Adjust path as needed

    // Get all items in the folder
    storageRef.listAll().addOnSuccessListener { listResult ->
        val items = listResult.items
        if (items.isNotEmpty()) {
            // Get a random subset of images (e.g., 4 random images)
            val randomImages = items.shuffled().take(4) // Adjust the number as needed

            // Collect URLs
            val urls = mutableListOf<String>()
            var processedCount = 0

            // Fetch the URLs for each random image asynchronously
            randomImages.forEach { storageReference ->
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    urls.add(uri.toString())
                    processedCount++
                    if (processedCount == randomImages.size) {
                        // Once all URLs are fetched, invoke the callback
                        callback(urls)
                    }
                }.addOnFailureListener {
                    processedCount++
                    if (processedCount == randomImages.size) {
                        // Invoke callback even if some URLs fail to fetch
                        callback(urls)
                    }
                }
            }
        } else {
            callback(emptyList()) // No images found
        }
    }.addOnFailureListener {
        callback(emptyList())
    }
}
