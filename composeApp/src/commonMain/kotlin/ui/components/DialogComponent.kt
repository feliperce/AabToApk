package ui.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.font.FontWeight
import ui.theme.Red800

@Composable
fun ErrorDialog(
    showDialog: MutableState<Boolean>,
    errorTitle: String,
    errorMsg: String
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text("OK")
                }
            },
            title = {
                Text(
                    text = errorTitle,
                    fontWeight = FontWeight.SemiBold,
                    color = Red800
                )
            },
            text = {
                Text(text = errorMsg)
            },
        )
    }
}