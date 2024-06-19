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
    onTextChanged: (item: SpinnerItem) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }

    var selectedItem by remember {
        if (items.isEmpty()) {
            mutableStateOf(SpinnerItem("", null))
        } else {
            mutableStateOf(items[0])
        }
    }



    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = modifier.menuAnchor(),
            value = selectedOptionText,
            onValueChange = {
                selectedOptionText = it
                onTextChanged(selectedItem)
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

        val filteringOptions = items.filter { it.name.contains(selectedOptionText, ignoreCase = true) }

        if (filteringOptions.isNotEmpty()) {
            DropdownMenu(
                modifier = Modifier
                    .background(Color.White)
                    .exposedDropdownSize(true),
                properties = PopupProperties(focusable = false),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                filteringOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.name) },
                        onClick = {
                            selectedItem = selectionOption
                            selectedOptionText = selectionOption.name
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}

data class  SpinnerItem(
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