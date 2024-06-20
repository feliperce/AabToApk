package ui.components

import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.PopupProperties
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpinnerTextInput(
    modifier: Modifier = Modifier,
    title: String,
    supportingText: String? = null,
    items: List<SpinnerItem>,
    onItemChanged: (item: SpinnerItem) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }

    var selectedItem by remember(items) {
        mutableStateOf(SpinnerItem("", null))
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = modifier.menuAnchor(),
            value = selectedOptionText,
            onValueChange = {
                selectedItem = if (it.isBlank()) {
                    SpinnerItem("", null)
                } else {
                    selectedItem.copy(name = it)
                }
                selectedOptionText = it
                onItemChanged(selectedItem)
            },
            label = { Text(title) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
            ),
            supportingText = {
                supportingText?.let {
                    Text(
                        it
                    )
                }
            }
        )

        if (items.isNotEmpty()) {
            DropdownMenu(
                modifier = Modifier
                    .background(Color.White)
                    .exposedDropdownSize(true),
                properties = PopupProperties(focusable = false),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                items.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.name) },
                        onClick = {
                            selectedItem = selectionOption
                            selectedOptionText = selectionOption.name
                            onItemChanged(selectedItem.copy(name = selectedOptionText))
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}

data class SpinnerItem(
    val name: String,
    val data: Any?
)

@Preview
@Composable
private fun SpinnerTextInputPreview() {
    SpinnerTextInput(
        title = "title",
        items = listOf(SpinnerItem("aaaa", null), SpinnerItem("bbbb", null))
    )
}