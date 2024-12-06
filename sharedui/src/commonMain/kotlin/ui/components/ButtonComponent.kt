package ui.components

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TweenButton(
    modifier: Modifier = Modifier,
    text: String,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    onClick: () -> Unit
) {
    var rotation by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    Button(
        modifier = modifier
        .graphicsLayer {
            rotationZ = rotation
        },
        onClick = onClick,
        colors = colors
    ) {
        Row {
            Text(text)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            coroutineScope.launch {
                animate(initialValue = 0f, targetValue = (-4..4).random().toFloat(), animationSpec = tween(durationMillis = 100)) { value, _ ->
                    rotation = value
                }
                animate(initialValue = rotation, targetValue = 0f, animationSpec = tween(durationMillis = 100)) { value, _ ->
                    rotation = value
                }
            }
        }
    }
}