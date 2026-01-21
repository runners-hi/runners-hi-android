package com.runnersHi.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.runnersHi.ui.theme.GradientEnd
import com.runnersHi.ui.theme.GradientStart
import com.runnersHi.ui.theme.OnBackgroundSecondary
import com.runnersHi.ui.theme.RunnersHiTheme
import com.runnersHi.ui.theme.SurfaceVariant

enum class AvatarSize(val size: Dp) {
    Small(32.dp),
    Medium(48.dp),
    Large(64.dp),
    ExtraLarge(80.dp)
}

@Composable
fun Avatar(
    imageUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Medium,
    showBorder: Boolean = false,
    borderColor: Color = GradientStart
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(GradientStart, GradientEnd)
    )

    Box(
        modifier = modifier
            .size(size.size)
            .then(
                if (showBorder) {
                    Modifier.border(
                        width = 2.dp,
                        brush = gradientBrush,
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )
            .clip(CircleShape)
            .background(SurfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "$name profile image",
                modifier = Modifier
                    .size(if (showBorder) size.size - 4.dp else size.size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.titleMedium,
                color = OnBackgroundSecondary
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun AvatarPreview() {
    RunnersHiTheme {
        Avatar(
            imageUrl = null,
            name = "John Doe",
            size = AvatarSize.Large
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun AvatarWithBorderPreview() {
    RunnersHiTheme {
        Avatar(
            imageUrl = null,
            name = "Runner",
            size = AvatarSize.Large,
            showBorder = true
        )
    }
}
