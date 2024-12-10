package ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.SentimentVerySatisfied
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.theme.*

@Composable
fun MessageCard(
    modifier: Modifier = Modifier,
    msg: String,
    title: String = "",
    cardType: CardType
) {
    Card(
        modifier = modifier,
        colors = CardColors(
            containerColor = cardType.bgColor,
            contentColor = Color.White,
            disabledContentColor = cardType.borderColor,
            disabledContainerColor = cardType.borderColor
        ),
        border = BorderStroke(2.dp, cardType.borderColor)
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
                imageVector = cardType.icon,
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(start = MarginPaddingSizeSmall),
                text = title,
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

enum class CardType(val bgColor: Color, val borderColor: Color, val icon: ImageVector) {
    INFO(Blue200, Blue800, Icons.Rounded.Info),
    WARNING(Yellow200, Yellow800, Icons.Rounded.Warning),
    ERROR(Red200, Red800, Icons.Rounded.Error),
    SUCCESS(Green200, Green800, Icons.Rounded.SentimentVerySatisfied)
}

@Composable
@Preview
private fun WarningCardPreview() {
    MessageCard(
        msg = "WARNING MSG!!!",
        title = "WARNING",
        cardType = CardType.WARNING
    )
}

@Composable
@Preview
private fun InfoCardPreview() {
    MessageCard(
        msg = "INFO MSG!!!",
        title = "INFO",
        cardType = CardType.INFO
    )
}

@Composable
@Preview
private fun ErrorCardPreview() {
    MessageCard(
        msg = "ERROR MSG!!!",
        cardType = CardType.ERROR
    )
}

@Composable
@Preview
private fun SuccessCardPreview() {
    MessageCard(
        msg = "SUCCESS MSG!!!",
        title = "SUCCESS",
        cardType = CardType.SUCCESS
    )
}