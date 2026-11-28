package com.spiderbiggen.manga.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.spiderbiggen.manga.presentation.R

private val AvatarOutlineWidth = 1.dp
private const val AVATAR_OUTLINE_ALPHA = 0.2f

/**
 * A round avatar. A picture gets a faint outline in the text colour. It shows where the picture is
 * close to the page colour and all but disappears where the picture already stands out. The
 * fallback icon has no outline, because it draws its own circle.
 */
@Composable
fun AvatarImage(
    model: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    var isPictureShown by remember(model) { mutableStateOf(false) }
    val outlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = AVATAR_OUTLINE_ALPHA)
    val outline =
        if (isPictureShown) {
            Modifier.border(AvatarOutlineWidth, outlineColor, CircleShape)
        } else {
            Modifier
        }
    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        error = painterResource(R.drawable.account_circle),
        onLoading = { isPictureShown = false },
        onSuccess = { isPictureShown = true },
        onError = { isPictureShown = false },
        modifier = modifier.clip(CircleShape).then(outline),
    )
}
