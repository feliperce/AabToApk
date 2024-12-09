package ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ui.theme.MarginPaddingSizeMedium
import ui.theme.MarginPaddingSizeSmall

@Composable
fun FormCard(
    modifier: Modifier = Modifier,
    title: String,
    actionIcon: ImageVector? = null,
    onActionClick: () -> Unit = {},
    isActionButtonEnabled: Boolean = false,
    formCardContent: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MarginPaddingSizeMedium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = MarginPaddingSizeSmall)
                        .fillMaxWidth(),
                    fontWeight = FontWeight.W500,
                    text = title
                )
                actionIcon?.let {
                    IconButton(
                        onClick = onActionClick,
                        enabled = isActionButtonEnabled
                    ) {
                        Icon(
                            imageVector = it,
                            contentDescription = null
                        )
                    }
                }
            }
            formCardContent()
        }
    }
}