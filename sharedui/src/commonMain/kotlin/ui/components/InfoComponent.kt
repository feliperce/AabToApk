package ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ui.theme.MarginPaddingSizeMedium
import ui.theme.MarginPaddingSizeSmall
import ui.theme.Red200
import ui.theme.Red800

@Composable
fun WarningCard(
    msg: String
) {
    Card(
        colors = CardColors(
            containerColor = Red200,
            contentColor = Color.White,
            disabledContentColor = Red800,
            disabledContainerColor = Red800
        ),
        border = BorderStroke(2.dp, Red800)
    ) {
        Row(
            modifier = Modifier.padding(
                top = MarginPaddingSizeSmall,
                start = MarginPaddingSizeMedium,
                end = MarginPaddingSizeMedium
            ),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(start = MarginPaddingSizeSmall),
                text = "WARNING",
                fontWeight = FontWeight.SemiBold
            )
        }
        Column(
            modifier = Modifier.padding(MarginPaddingSizeMedium)
        ) {

            Text(msg)
        }
    }
}