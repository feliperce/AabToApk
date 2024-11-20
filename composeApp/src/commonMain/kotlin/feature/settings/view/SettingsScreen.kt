package feature.settings.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import feature.settings.model.SettingsFormData
import feature.settings.model.SettingsFormDataCallback
import feature.settings.state.SettingsIntent
import feature.settings.viewmodel.SettingsViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ui.components.DirectoryPickerTextField
import ui.theme.MarginPaddingSizeMedium

@Composable
fun SettingsScreen(
    snackbarHostState: SnackbarHostState
) {
    val settingsViewModel = koinViewModel<SettingsViewModel>()

    val settingsUiState by settingsViewModel.settingsState.collectAsState()

    var settingsFormData by remember { mutableStateOf(SettingsFormData()) }

    val onFormDataChange: (SettingsFormData) -> Unit = { newFormData ->
        settingsFormData = newFormData
        settingsViewModel.sendIntent(
            SettingsIntent.ValidateForm(newFormData)
        )
    }

    LaunchedEffect(Unit) {
        settingsViewModel.sendIntent(
            SettingsIntent.GetSettings
        )
        settingsViewModel.sendIntent(
            SettingsIntent.GetIsFirstAccess
        )
    }

    LaunchedEffect(settingsUiState.settingsData) {
        settingsUiState.settingsData?.let {
            settingsFormData = settingsFormData.copy(
                adbPath = it.adbPath,
                outputApksPath = it.outputPath,
                buildToolsPath = it.buildToolsPath
            )
        }
    }

    LaunchedEffect(settingsUiState.successMsg.id) {
        val successMsg = settingsUiState.successMsg

        if (successMsg.msg.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = successMsg.msg,
                duration = SnackbarDuration.Short
            )
        }
    }

    val settingsFormDataCallback = SettingsFormDataCallback(
        onSaveButtonClick = {
            settingsViewModel.sendIntent(
                SettingsIntent.SaveSettings(settingsFormData)
            )
            settingsViewModel.sendIntent(
                SettingsIntent.SaveIsFirstAccess(false)
            )
        }
    )

    Column {
        SettingsContent(
            settingsFormData = settingsFormData,
            settingsFormDataCallback = settingsFormDataCallback,
            isFormValid = settingsUiState.isFormValid,
            onSettingsFormChange = onFormDataChange
        )
    }
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

        DirectoryPickerTextField(
            modifier = defaultModifier,
            initialText = settingsFormData.adbPath,
            onDirectoryResult = {
                onSettingsFormChange(settingsFormData.copy(adbPath = it.path ?: ""))
            },
            label = "ADB Dir Path",
            pickerTitle = "ADB Directory"
        )

        DirectoryPickerTextField(
            modifier = defaultModifier,
            initialText = settingsFormData.buildToolsPath,
            onDirectoryResult = {
                onSettingsFormChange(settingsFormData.copy(buildToolsPath = it.path ?: ""))
            },
            label = "Build Tools Dir Path",
            pickerTitle = "Build Tools Directory"
        )

        DirectoryPickerTextField(
            modifier = defaultModifier,
            initialText = settingsFormData.outputApksPath,
            onDirectoryResult = {
                onSettingsFormChange(settingsFormData.copy(outputApksPath = it.path ?: ""))
            },
            label = "Output Dir Path",
            pickerTitle = "Output Directory"
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
private fun SettingsContentPreview() {
    SettingsContent(
        settingsFormData = SettingsFormData(),
        settingsFormDataCallback = SettingsFormDataCallback {},
        isFormValid = true,
        onSettingsFormChange = {}
    )
}