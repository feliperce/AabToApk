package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot.Companion.withMutableSnapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import io.github.vinceglb.filekit.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformDirectory
import io.github.vinceglb.filekit.core.PlatformFile
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

@Composable
fun FilePickerTextField(
    modifier: Modifier = Modifier,
    initialText: String = "",
    label: String? = null,
    enabled: Boolean = true,
    onFileResult: (fileResult: PlatformFile) -> Unit = {},
    fileType: PickerType = PickerType.File(),
    selectionMode: PickerMode<PlatformFile> = PickerMode.Single,
    pickerTitle: String? = null
) {
    var text by remember { mutableStateOf(initialText) }

    LaunchedEffect(initialText) {
        withMutableSnapshot {
            text = initialText
        }
    }

    val pickerLauncher = rememberFilePickerLauncher(
        mode = selectionMode,
        type = fileType,
        title = pickerTitle
    ) { file ->
        file?.let{
            onFileResult(it)
            text = it.path ?: it.name
        }
    }

    val clickAction = {
        if (enabled) {
            pickerLauncher.launch()
        }
    }

    PickerOutlineTextField(modifier, text, label, enabled, clickAction)
}

@Composable
fun DirectoryPickerTextField(
    modifier: Modifier = Modifier,
    initialText: String = "",
    label: String? = null,
    enabled: Boolean = true,
    onDirectoryResult: (fileResult: PlatformDirectory) -> Unit = {},
    pickerTitle: String? = null
) {
    var text by remember { mutableStateOf(initialText) }

    LaunchedEffect(initialText) {
        withMutableSnapshot {
            text = initialText
        }
    }

    val pickerLauncher = rememberDirectoryPickerLauncher(
        title = pickerTitle
    ) { directory ->
        directory?.let {
            onDirectoryResult(it)
            text = it.path ?: ""
        }
    }

    val clickAction = {
        if (enabled) {
            pickerLauncher.launch()
        }
    }

    PickerOutlineTextField(modifier, text, label, enabled, clickAction)
}

@Composable
private fun PickerOutlineTextField(
    modifier: Modifier,
    text: String,
    label: String?,
    enabled: Boolean,
    clickAction: () -> Unit
) {
    var pickerText = text

    OutlinedTextField(
        modifier = modifier,
        value = pickerText,
        label = {
            label?.let {
                Text(it)
            }
        },
        onValueChange = {
            pickerText = it
        },
        maxLines = 1,
        singleLine = true,
        enabled = enabled,
        readOnly = true,
        trailingIcon = {
            Icon(
                modifier = Modifier.clickable { clickAction() },
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null
            )
        },
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            clickAction()
                        }
                    }
                }
            }
    )
}

data class SpinnerItem(
    val name: String,
    val data: Any?
)

@OptIn(ExperimentalUuidApi::class)
data class RadioItem(
    val id: String = Uuid.random().toHexString(),
    val text: String,
    val data: Any? = null,
    var isSelected: Boolean = false
)

val aabInputType: PickerType = PickerType.File(
    extensions = listOf("aab")
)

val keystoreInputType: PickerType = PickerType.File(
    extensions = listOf("keystore", "jks")
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

@Preview
@Composable
private fun FilePickerTextFieldPreview() {
    FilePickerTextField(
        onFileResult = {}
    )
}
