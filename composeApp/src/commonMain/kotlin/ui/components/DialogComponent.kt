package ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.font.FontWeight
import ui.theme.Red800
import utils.ErrorMsg

@Composable
fun ErrorDialog(
    showDialog: MutableState<Boolean>,
    errorMsg: ErrorMsg
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
                    text = errorMsg.title,
                    fontWeight = FontWeight.SemiBold,
                    color = Red800
                )
            },
            text = {
                Text(text = errorMsg.msg)
            },
        )
    }
}