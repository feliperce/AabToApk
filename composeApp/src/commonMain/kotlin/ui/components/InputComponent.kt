package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpinnerTextInput(
    modifier: Modifier = Modifier,
    title: String,
    supportingText: String? = null,
    isEnabled: Boolean = false,
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
            },
            enabled = isEnabled
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
                        enabled = isEnabled,
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

@Composable
fun RadioGroup(
    radioOptions: List<RadioItem>,
    selectedOption: RadioItem,
    isEnabled: Boolean = true,
    onItemSelected: (radioItem: RadioItem) -> Unit
) {
    Row(Modifier.selectableGroup()) {
        radioOptions.forEach { item ->
            Row(
                Modifier
                    .height(56.dp)
                    .selectable(
                        selected = item == selectedOption,
                        onClick = {
                            onItemSelected(item)
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = item == selectedOption,
                    onClick = null,
                    enabled = isEnabled
                )
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

data class SpinnerItem(
    val name: String,
    val data: Any?
)

data class RadioItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val data: Any? = null,
    var isSelected: Boolean = false
)

@Preview
@Composable
private fun SpinnerTextInputPreview() {
    SpinnerTextInput(
        title = "title",
        items = listOf(SpinnerItem("aaaa", null), SpinnerItem("bbbb", null))
    )
}

@Preview
@Composable
private fun RadioGroupPreview() {
    RadioGroup(
        radioOptions = listOf(
            RadioItem(text = "APKS"),
            RadioItem(text = "Universal APK")
        ),
        onItemSelected = {},
        selectedOption = RadioItem(text = "APKS")
    )
}