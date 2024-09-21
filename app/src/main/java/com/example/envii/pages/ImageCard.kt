package com.example.envii.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.flowlayout.FlowRow
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

@ExperimentalMaterial3Api
@Composable
fun ImageCard(
    title: String,
    description: String,
    folderPath: String, // Add folder path as a parameter
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    var imageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(folderPath) {
        getLatestImage(folderPath) { url ->
            imageUrl = url
        }
    }

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
                model = imageUrl ?: "https://picsum.photos/seed/${Random.nextInt()}/300/200" // Fallback image
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

// Function to get the latest image URL
fun getLatestImage(folderPath: String, callback: (String?) -> Unit) {
    val storage: FirebaseStorage = Firebase.storage
    val storageRef: StorageReference = storage.reference.child(folderPath)

    storageRef.listAll().addOnSuccessListener { listResult: ListResult ->
        val items = listResult.items
        if (items.isNotEmpty()) {
            // Get the latest image
            val latestImage = items.maxByOrNull { it.name } // Adjust sorting based on your needs
            latestImage?.downloadUrl?.addOnSuccessListener { uri ->
                callback(uri.toString()) // Return the URL of the latest image
            }?.addOnFailureListener {
                callback(null) // Handle error
            }
        } else {
            callback(null) // No images found
        }
    }.addOnFailureListener {
        callback(null) // Handle error
    }
}
