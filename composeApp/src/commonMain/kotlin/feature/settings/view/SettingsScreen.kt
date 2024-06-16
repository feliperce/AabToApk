package feature.settings.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import feature.settings.model.SettingsFormData
import feature.settings.model.SettingsFormDataCallback
import feature.settings.state.SettingsIntent
import feature.settings.viewmodel.SettingsViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.theme.MarginPaddingSizeMedium
import utils.InputPathType

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel { SettingsViewModel() }
) {
    val settingsUiState by settingsViewModel.settingsState.collectAsState()

    var showDirPicker by remember { mutableStateOf(false) }
    var settingsFormData by remember { mutableStateOf(SettingsFormData()) }
    var inputType by remember { mutableStateOf(InputPathType.NONE) }

    val onFormDataChange: (SettingsFormData) -> Unit = { newFormData ->
        settingsFormData = newFormData
        settingsViewModel.sendIntent(
            SettingsIntent.ValidateForm(newFormData)
        )
    }

    val settingsFormDataCallback = SettingsFormDataCallback(
        onAdbPathIconClick = {
            inputType = InputPathType.ADB_DIR_PATH
            showDirPicker = true
        },
        onBuildToolsPathIconClick = {
            inputType = InputPathType.BUILD_TOOL_DIR_PATH
            showDirPicker = true
        },
        onOutputPathIconClick = {
            inputType = InputPathType.OUTPUT_DIR_PATH
            showDirPicker = true
        },
        onSaveButtonClick = {
            settingsViewModel.sendIntent(
                SettingsIntent.SaveSettings(settingsFormData)
            )
        }
    )

    DirectoryPicker(showDirPicker) { path ->
        when (inputType) {
            InputPathType.ADB_DIR_PATH ->
                settingsFormData = settingsFormData.copy(adbPath = path ?: "")
            InputPathType.OUTPUT_DIR_PATH ->
                settingsFormData = settingsFormData.copy(outputApksPath = path ?: "")
            InputPathType.BUILD_TOOL_DIR_PATH ->
                settingsFormData = settingsFormData.copy(buildToolsPath = path ?: "")
            else -> {}
        }
        inputType = InputPathType.NONE
        showDirPicker = false
        settingsViewModel.sendIntent(
            SettingsIntent.ValidateForm(settingsFormData)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Settings")
                }
            )
        },
        content = {
            Column(
                modifier = Modifier.padding(it)
            ) {
                SettingsContent(
                    settingsFormData = settingsFormData,
                    settingsFormDataCallback = settingsFormDataCallback,
                    isFormValid = settingsUiState.isFormValid,
                    onSettingsFormChange = onFormDataChange
                )
            }
        }
    )

}

@Composable
fun SettingsContent(
    isFormValid: Boolean,
    settingsFormData: SettingsFormData,
    settingsFormDataCallback: SettingsFormDataCallback,
    onSettingsFormChange: (settingsFormData: SettingsFormData) -> Unit
) {
    val defaultModifier = Modifier
        .fillMaxWidth()
        .padding(top = MarginPaddingSizeMedium)

    Column(
        modifier = Modifier.padding(MarginPaddingSizeMedium)
    ) {
        OutlinedTextField(
            modifier = defaultModifier,
            value = settingsFormData.adbPath,
            onValueChange = {
                onSettingsFormChange(settingsFormData.copy(adbPath = it))
            },
            label = {
                Text("ADB Dir Path")
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable {
                        settingsFormDataCallback.onAdbPathIconClick()
                    },
                    imageVector = Icons.Rounded.FolderOpen,
                    contentDescription = null
                )
            }
        )

        OutlinedTextField(
            modifier = defaultModifier,
            value = settingsFormData.buildToolsPath,
            onValueChange = {
                onSettingsFormChange(settingsFormData.copy(buildToolsPath = it))
            },
            label = {
                Text("Build Tools Dir Path")
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable {
                        settingsFormDataCallback.onBuildToolsPathIconClick()
                    },
                    imageVector = Icons.Rounded.FolderOpen,
                    contentDescription = null
                )
            }
        )

        OutlinedTextField(
            modifier = defaultModifier,
            value = settingsFormData.outputApksPath,
            onValueChange = {
                onSettingsFormChange(settingsFormData.copy(outputApksPath = it))
            },
            label = {
                Text("Output Dir Path")
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable {
                        settingsFormDataCallback.onOutputPathIconClick()
                    },
                    imageVector = Icons.Rounded.FolderOpen,
                    contentDescription = null
                )
            }
        )

        Button(
            modifier = defaultModifier,
            content = {
                Text("SAVE SETTINGS")
            },
            onClick = settingsFormDataCallback.onSaveButtonClick,
            enabled = isFormValid
        )
    }
}

@Preview
@Composable
fun SettingsContentPreview() {
    SettingsContent(
        settingsFormData = SettingsFormData(),
        settingsFormDataCallback = SettingsFormDataCallback({},{},{},{}),
        isFormValid = true,
        onSettingsFormChange = {}
    )
}