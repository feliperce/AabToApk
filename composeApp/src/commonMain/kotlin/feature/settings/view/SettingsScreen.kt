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
import aabtoapk.composeapp.generated.resources.Res
import aabtoapk.composeapp.generated.resources.adb_dir_path
import aabtoapk.composeapp.generated.resources.adb_directory
import aabtoapk.composeapp.generated.resources.build_tools_dir_path
import aabtoapk.composeapp.generated.resources.build_tools_directory
import aabtoapk.composeapp.generated.resources.output_dir_path
import aabtoapk.composeapp.generated.resources.output_directory
import aabtoapk.composeapp.generated.resources.save_settings
import aabtoapk.composeapp.generated.resources.settings_changed_success
import io.github.feliperce.aabtoapk.utils.extractor.SuccessMsgType
import org.jetbrains.compose.resources.stringResource

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

    val successMsg = getSuccessMessage(
        successType = settingsUiState.successMsg.type
    )

    LaunchedEffect(settingsUiState.successMsg.id) {

        if (successMsg.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = successMsg,
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
            label = stringResource(Res.string.adb_dir_path),
            pickerTitle = stringResource(Res.string.adb_directory)
        )

        DirectoryPickerTextField(
            modifier = defaultModifier,
            initialText = settingsFormData.buildToolsPath,
            onDirectoryResult = {
                onSettingsFormChange(settingsFormData.copy(buildToolsPath = it.path ?: ""))
            },
            label = stringResource(Res.string.build_tools_dir_path),
            pickerTitle = stringResource(Res.string.build_tools_directory)
        )

        DirectoryPickerTextField(
            modifier = defaultModifier,
            initialText = settingsFormData.outputApksPath,
            onDirectoryResult = {
                onSettingsFormChange(settingsFormData.copy(outputApksPath = it.path ?: ""))
            },
            label = stringResource(Res.string.output_dir_path),
            pickerTitle = stringResource(Res.string.output_directory)
        )

        Button(
            modifier = defaultModifier,
            content = {
                Text(stringResource(Res.string.save_settings))
            },
            onClick = settingsFormDataCallback.onSaveButtonClick,
            enabled = isFormValid
        )
    }
}

@Composable
fun getSuccessMessage(successType: SuccessMsgType): String {
    return when (successType) {
        SuccessMsgType.SETTINGS_CHANGED -> stringResource(Res.string.settings_changed_success)
        else -> ""
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
