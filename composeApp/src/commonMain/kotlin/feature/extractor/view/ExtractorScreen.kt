package feature.extractor.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import aabtoapk.composeapp.generated.resources.Res
import aabtoapk.composeapp.generated.resources.apks
import aabtoapk.composeapp.generated.resources.universal_apk
import aabtoapk.composeapp.generated.resources.install
import aabtoapk.composeapp.generated.resources.settings_need_configuration
import aabtoapk.composeapp.generated.resources.pay_attention
import aabtoapk.composeapp.generated.resources.extract
import aabtoapk.composeapp.generated.resources.keystore_sign
import aabtoapk.composeapp.generated.resources.name
import aabtoapk.composeapp.generated.resources.select_name_to_save_keystore
import aabtoapk.composeapp.generated.resources.keystore_path
import aabtoapk.composeapp.generated.resources.keystore
import aabtoapk.composeapp.generated.resources.keystore_password
import aabtoapk.composeapp.generated.resources.hide_password
import aabtoapk.composeapp.generated.resources.show_password
import aabtoapk.composeapp.generated.resources.alias
import aabtoapk.composeapp.generated.resources.key_password
import aabtoapk.composeapp.generated.resources.aab_extract_install
import aabtoapk.composeapp.generated.resources.aab_path
import aabtoapk.composeapp.generated.resources.aab
import aabtoapk.composeapp.generated.resources.remove_keystore_data
import aabtoapk.composeapp.generated.resources.remove_keystore
import aabtoapk.composeapp.generated.resources.remove

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import feature.extractor.mapper.KeystoreDto
import feature.extractor.model.ExtractorFormData
import feature.extractor.model.ExtractorFormDataCallback
import feature.extractor.state.ExtractorIntent
import feature.extractor.viewmodel.ExtractorViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ui.components.*
import ui.theme.MarginPaddingSizeMedium
import ui.theme.MarginPaddingSizeSmall
import io.github.feliperce.aabtoapk.utils.extractor.ApksExtractor
import io.github.feliperce.aabtoapk.utils.extractor.ErrorMsg
import io.github.feliperce.aabtoapk.utils.extractor.ErrorType
import io.github.feliperce.aabtoapk.utils.extractor.SuccessMsg
import io.github.feliperce.aabtoapk.utils.extractor.SuccessMsgType
import org.jetbrains.compose.resources.stringResource
import aabtoapk.composeapp.generated.resources.extracted_with_success
import aabtoapk.composeapp.generated.resources.apks_installed_success
import aabtoapk.composeapp.generated.resources.apk_installed_success
import aabtoapk.composeapp.generated.resources.invalid_settings
import aabtoapk.composeapp.generated.resources.go_to_settings
import aabtoapk.composeapp.generated.resources.sign_failure
import aabtoapk.composeapp.generated.resources.keystore_sign_error
import aabtoapk.composeapp.generated.resources.aab_extract_failure
import aabtoapk.composeapp.generated.resources.error_extract_aab
import aabtoapk.composeapp.generated.resources.install_apk_error
import aabtoapk.composeapp.generated.resources.error_install_apks

@Composable
fun ExtractorScreen(snackbarHostState: SnackbarHostState) {
    val extractorViewModel: ExtractorViewModel = koinViewModel()

    val extractorUiState by extractorViewModel.extractorState.collectAsState()

    var extractorFormData by remember { 
        mutableStateOf(
            ExtractorFormData(
                selectedExtractOption = extractorUiState.selectedExtractOption ?: RadioItem(text = "")
            )
        ) 
    }

    val showErrorDialog = remember { mutableStateOf(false) }
    var showKeystoreRemoveDialog by remember { mutableStateOf(false) }

    val apksText = stringResource(Res.string.apks)
    val universalApkText = stringResource(Res.string.universal_apk)

    LaunchedEffect(Unit) {
        val extractorOptionsList = listOf(
            RadioItem(
                id = ApksExtractor.ExtractorOption.APKS.name,
                text = apksText,
                data = ApksExtractor.ExtractorOption.APKS,
                isSelected = true
            ),
            RadioItem(
                id = ApksExtractor.ExtractorOption.UNIVERSAL_APK.name,
                data = ApksExtractor.ExtractorOption.UNIVERSAL_APK,
                text = universalApkText
            )
        )
        extractorViewModel.sendIntent(ExtractorIntent.UpdateExtractOptions(extractorOptionsList))
        extractorViewModel.sendIntent(ExtractorIntent.UpdateSelectedExtractOption(extractorOptionsList[0]))
    }

    val onFormDataChange: (ExtractorFormData) -> Unit = { newFormData ->
        extractorFormData = newFormData
    }

    SideEffect {
        extractorViewModel.sendIntent(
            ExtractorIntent.GetSettingsData
        )
    }

    LaunchedEffect(Unit) {
        extractorViewModel.sendIntent(
            ExtractorIntent.GetKeystoreData
        )
    }

    LaunchedEffect(extractorUiState.errorMsg.id) {
        showErrorDialog.value = extractorUiState.errorMsg.type != ErrorType.NONE
    }

    LaunchedEffect(extractorUiState.showKeystoreRemoveDialog) {
        showKeystoreRemoveDialog = extractorUiState.showKeystoreRemoveDialog
    }

    val installText = stringResource(Res.string.install)

    val successMsg = getSuccessMessage(
        successType = extractorUiState.successMsg.type,
        extractedPath = extractorUiState.extractedApksPath,
        isApks = extractorUiState.selectedExtractOption?.data == ApksExtractor.ExtractorOption.APKS
    )

    LaunchedEffect(extractorUiState.successMsg.id) {
        if (successMsg.isNotEmpty()) {
            if (extractorUiState.successMsg.type == SuccessMsgType.EXTRACT_AAB) {
                val result = snackbarHostState
                    .showSnackbar(
                        message = successMsg,
                        actionLabel = installText,
                        duration = SnackbarDuration.Indefinite
                    )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        if (extractorFormData.selectedExtractOption.data == ApksExtractor.ExtractorOption.APKS) {
                            extractorViewModel.sendIntent(
                                ExtractorIntent.InstallApks(
                                    extractorFormData = extractorFormData
                                )
                            )
                        } else {
                            extractorViewModel.sendIntent(
                                ExtractorIntent.InstallApk(
                                    extractorFormData = extractorFormData
                                )
                            )
                        }
                    }
                    SnackbarResult.Dismissed -> { }
                }
            } else {
                snackbarHostState
                    .showSnackbar(
                        message = successMsg,
                        duration = SnackbarDuration.Long
                    )
            }
        }
    }

    LaunchedEffect(extractorUiState.settingsData) {
        extractorFormData = extractorFormData.copy(
            settingsData = extractorUiState.settingsData
        )
    }

    LaunchedEffect(extractorUiState.selectedExtractOption) {
        extractorUiState.selectedExtractOption?.let { option ->
            extractorFormData = extractorFormData.copy(
                selectedExtractOption = option,
                extractOptions = extractorUiState.extractOptions
            )
        }
    }

    LaunchedEffect(extractorUiState.keystoreDto) {
        extractorFormData = extractorFormData.copy(
            keystoreDto = extractorUiState.keystoreDto
        )
    }

    LaunchedEffect(extractorUiState.aabPath) {
        if (extractorUiState.aabPath.isNotEmpty()) {
            extractorFormData = extractorFormData.copy(
                aabPath = extractorUiState.aabPath
            )
        }
    }

    ErrorDialog(
        showDialog = showErrorDialog,
        title = getErrorTitle(extractorUiState.errorMsg.type),
        msg = getErrorMessage(extractorUiState.errorMsg.type)
    )

    val extractorFormDataCallback = ExtractorFormDataCallback(
        onKeystoreSpinnerItemChanged = { keystoreDto ->
            extractorViewModel.sendIntent(ExtractorIntent.UpdateKeystoreDto(keystoreDto))
        },
        onKeystoreRemoveClick = {
            extractorViewModel.sendIntent(ExtractorIntent.SetShowKeystoreRemoveDialog(true))
        },
        onItemSelected = { item ->
            extractorViewModel.sendIntent(ExtractorIntent.UpdateSelectedExtractOption(item))
        }
    )

    if (showKeystoreRemoveDialog) {
        KeystoreRemovalDialog(
            keystoreName = extractorFormData.keystoreDto.name,
            onDismiss = { 
                extractorViewModel.sendIntent(ExtractorIntent.SetShowKeystoreRemoveDialog(false))
            },
            onPositiveButtonClick = {
                extractorViewModel.sendIntent(
                    ExtractorIntent.RemoveKeystore(extractorFormData.keystoreDto)
                )
                extractorViewModel.sendIntent(
                    ExtractorIntent.UpdateKeystoreDto(KeystoreDto())
                )
                extractorViewModel.sendIntent(ExtractorIntent.SetShowKeystoreRemoveDialog(false))
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExtractorContent(
            keystoreDtoList = extractorUiState.keystoreDtoList,
            extractorFormData = extractorFormData,
            extractorFormDataCallback = extractorFormDataCallback,
            onFormDataChange = onFormDataChange,
            isLoading = extractorUiState.loading,
            selectedExtractOption = extractorFormData.selectedExtractOption,
            onExtractApksButtonClick = {
                extractorViewModel.sendIntent(
                    ExtractorIntent.ExtractAab(
                        extractorFormData = extractorFormData
                    )
                )

                extractorFormData.keystoreDto.let { keystoreDto ->
                    if (keystoreDto.name.isNotEmpty()) {
                        val existingKeystore = extractorUiState.keystoreDtoList.find { it.name == keystoreDto.name }

                        val keystoreToSave = if (existingKeystore != null && keystoreDto.id == null) {
                            keystoreDto.copy(id = existingKeystore.id)
                        } else {
                            keystoreDto
                        }

                        extractorViewModel.sendIntent(
                            ExtractorIntent.SaveKeystore(
                                keystoreDto = keystoreToSave
                            )
                        )
                    }
                }
            }
        )
    }

    if (extractorUiState.loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ExtractorContent(
    extractorFormData: ExtractorFormData,
    keystoreDtoList: List<KeystoreDto>,
    extractorFormDataCallback: ExtractorFormDataCallback,
    selectedExtractOption: RadioItem,
    isLoading: Boolean,
    onFormDataChange: (ExtractorFormData) -> Unit,
    onExtractApksButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(MarginPaddingSizeMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        extractorFormData.settingsData?.let {
            if (it.adbPath.isEmpty() || it.outputPath.isEmpty() || it.buildToolsPath.isEmpty()) {
                MessageCard(
                    msg = stringResource(Res.string.settings_need_configuration),
                    title = stringResource(Res.string.pay_attention),
                    cardType = CardType.ERROR
                )
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MarginPaddingSizeMedium)
        )

        ExtractorForm(
            keystoreDtoList = keystoreDtoList,
            isLoading = isLoading,
            extractorFormData = extractorFormData,
            extractorFormDataCallback = extractorFormDataCallback,
            onFormDataChange = onFormDataChange,
            selectedExtractOption = selectedExtractOption
        )

        Row(
            modifier = Modifier.padding(top = MarginPaddingSizeMedium)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                content = {
                    Text(stringResource(Res.string.extract))
                },
                onClick = onExtractApksButtonClick,
                enabled = !isLoading
            )

            Spacer(
                modifier = Modifier.width(MarginPaddingSizeSmall)
            )
        }
    }
}

@Composable
fun ExtractorForm(
    keystoreDtoList: List<KeystoreDto>,
    extractorFormData: ExtractorFormData,
    extractorFormDataCallback: ExtractorFormDataCallback,
    selectedExtractOption: RadioItem,
    isLoading: Boolean,
    onFormDataChange: (ExtractorFormData) -> Unit,
) {
    val spacerModifier = Modifier
        .fillMaxWidth()
        .padding(top = MarginPaddingSizeMedium)

    Column {
        KeystoreSignForm(
            keystoreDtoList = keystoreDtoList,
            extractorFormData = extractorFormData,
            onFormDataChange = onFormDataChange,
            isLoading = isLoading,
            onItemChanged = extractorFormDataCallback.onKeystoreSpinnerItemChanged,
            onKeystoreRemoveIconClick = extractorFormDataCallback.onKeystoreRemoveClick
        )
        Spacer(modifier = spacerModifier)
        OutputForm(
            extractorFormData = extractorFormData,
            onFormDataChange = onFormDataChange,
            isLoading = isLoading,
            selectedExtractOption = selectedExtractOption,
            onItemSelected = extractorFormDataCallback.onItemSelected
        )
    }
}

@Composable
fun KeystoreSignForm(
    keystoreDtoList: List<KeystoreDto>,
    extractorFormData: ExtractorFormData,
    onFormDataChange: (ExtractorFormData) -> Unit,
    onItemChanged: (keystoreDto: KeystoreDto) -> Unit,
    onKeystoreRemoveIconClick: () -> Unit,
    isLoading: Boolean
) {
    val inputModifier = Modifier.fillMaxWidth()
    var passwordVisible by remember { mutableStateOf(false) }
    var keyPasswordVisible by remember { mutableStateOf(false) }

    val spinnerItems = keystoreDtoList.map {
        SpinnerItem(
            name = it.name,
            data = it
        )
    }

    FormCard(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(Res.string.keystore_sign),
        actionIcon = if (extractorFormData.keystoreDto.id == null) {
            null
        } else {
            Icons.Rounded.Delete
        },
        isActionButtonEnabled = !isLoading,
        onActionClick = onKeystoreRemoveIconClick
    ) {
        SpinnerTextInput(
            modifier = inputModifier,
            title = stringResource(Res.string.name),
            items = spinnerItems,
            isEnabled = !isLoading,
            supportingText = stringResource(Res.string.select_name_to_save_keystore),
            onItemChanged = { spinnerItem: SpinnerItem ->
                var keystoreDto: KeystoreDto? = (spinnerItem.data as KeystoreDto?)

                keystoreDto = keystoreDto?.copy(
                    name = spinnerItem.name
                ) ?: run {
                    val existingKeystore = keystoreDtoList.find { it.name == spinnerItem.name }

                    if (existingKeystore != null) {
                        existingKeystore.copy()
                    } else {
                        KeystoreDto(
                            name = spinnerItem.name,
                            path = extractorFormData.keystoreDto.path,
                            password = extractorFormData.keystoreDto.password,
                            keyAlias = extractorFormData.keystoreDto.keyAlias,
                            keyPassword = extractorFormData.keystoreDto.keyPassword
                        )
                    }
                }

                onItemChanged(keystoreDto)
            }
        )

        FilePickerTextField(
            modifier = inputModifier.padding(top = MarginPaddingSizeMedium),
            initialText = extractorFormData.keystoreDto.path,
            enabled = !isLoading,
            onFileResult = {
                onFormDataChange(
                    extractorFormData.copy(
                        keystoreDto = extractorFormData.keystoreDto.copy(
                            path = it.path ?: ""
                        )
                    )
                )
            },
            label = stringResource(Res.string.keystore_path),
            fileType = keystoreInputType,
            pickerTitle = stringResource(Res.string.keystore)
        )

        OutlinedTextField(
            modifier = inputModifier
                .padding(top = MarginPaddingSizeSmall),
            value = extractorFormData.keystoreDto.password,
            enabled = !isLoading,
            onValueChange = {
                onFormDataChange(
                    extractorFormData.copy(
                        keystoreDto = extractorFormData.keystoreDto.copy(
                            password = it
                        )
                    )
                )
            },
            label = {
                Text(stringResource(Res.string.keystore_password))
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) stringResource(Res.string.hide_password) else stringResource(Res.string.show_password)
                    )
                }
            }
        )

        OutlinedTextField(
            modifier = inputModifier
                .padding(top = MarginPaddingSizeSmall),
            value = extractorFormData.keystoreDto.keyAlias,
            enabled = !isLoading,
            onValueChange = {
                onFormDataChange(
                    extractorFormData.copy(
                        keystoreDto = extractorFormData.keystoreDto.copy(
                            keyAlias = it
                        )
                    )
                )
            },
            label = {
                Text(stringResource(Res.string.alias))
            }
        )

        OutlinedTextField(
            modifier = inputModifier
                .padding(top = MarginPaddingSizeSmall),
            value = extractorFormData.keystoreDto.keyPassword,
            enabled = !isLoading,
            onValueChange = {
                onFormDataChange(
                    extractorFormData.copy(
                        keystoreDto = extractorFormData.keystoreDto.copy(
                            keyPassword = it
                        )
                    )
                )
            },
            label = {
                Text(stringResource(Res.string.key_password))
            },
            visualTransformation = if (keyPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { keyPasswordVisible = !keyPasswordVisible }) {
                    Icon(
                        imageVector = if (keyPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (keyPasswordVisible) stringResource(Res.string.hide_password) else stringResource(Res.string.show_password)
                    )
                }
            }
        )
    }
}

@Composable
fun OutputForm(
    extractorFormData: ExtractorFormData,
    onFormDataChange: (ExtractorFormData) -> Unit,
    onItemSelected: (item: RadioItem) -> Unit,
    selectedExtractOption: RadioItem,
    isLoading: Boolean
) {
    val inputModifier = Modifier.fillMaxWidth()

    FormCard(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(Res.string.aab_extract_install)
    ) {
        FilePickerTextField(
            modifier = inputModifier,
            initialText = extractorFormData.aabPath,
            enabled = !isLoading,
            onFileResult = {
                onFormDataChange(extractorFormData.copy(aabPath = it.path ?: ""))
            },
            label = stringResource(Res.string.aab_path),
            fileType = aabInputType,
            pickerTitle = stringResource(Res.string.aab)
        )

        RadioGroup(
            radioOptions = extractorFormData.extractOptions,
            isEnabled = !isLoading,
            onItemSelected = onItemSelected,
            selectedOption = selectedExtractOption
        )
    }
}

@Composable
fun KeystoreRemovalDialog(
    keystoreName: String,
    onPositiveButtonClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(Res.string.remove_keystore_data))
        },
        text = {
            Text(stringResource(Res.string.remove_keystore).format(keystoreName))
        },
        confirmButton = {
            Button(
                onClick = {
                    onPositiveButtonClick()
                    onDismiss()
                },
                content = {
                    Text(stringResource(Res.string.remove))
                }
            )
        }
    )
}

@Composable
fun getErrorMessage(errorType: ErrorType): String {
    return when (errorType) {
        ErrorType.SIGN_FAILURE -> stringResource(Res.string.keystore_sign_error)
        ErrorType.AAB_EXTRACT_FAILURE -> stringResource(Res.string.error_extract_aab)
        ErrorType.INSTALL_APK_ERROR -> stringResource(Res.string.error_install_apks)
        ErrorType.INVALID_SETTINGS -> stringResource(Res.string.go_to_settings)
        else -> ""
    }
}

@Composable
fun getErrorTitle(errorType: ErrorType): String {
    return when (errorType) {
        ErrorType.SIGN_FAILURE -> stringResource(Res.string.sign_failure)
        ErrorType.AAB_EXTRACT_FAILURE -> stringResource(Res.string.aab_extract_failure)
        ErrorType.INSTALL_APK_ERROR -> stringResource(Res.string.install_apk_error)
        ErrorType.INVALID_SETTINGS -> stringResource(Res.string.invalid_settings)
        else -> ""
    }
}

@Composable
fun getSuccessMessage(successType: SuccessMsgType, extractedPath: String, isApks: Boolean): String {
    return when (successType) {
        SuccessMsgType.EXTRACT_AAB -> stringResource(Res.string.extracted_with_success, extractedPath)
        SuccessMsgType.INSTALL_APKS -> if (isApks) stringResource(Res.string.apks_installed_success) else stringResource(Res.string.apk_installed_success)
        else -> ""
    }
}

@Composable
@Preview
private fun KeystoreSignFormPreview() {
    KeystoreSignForm(
        keystoreDtoList = emptyList(),
        onFormDataChange = {},
        extractorFormData = ExtractorFormData(selectedExtractOption = RadioItem(text = "aaaa")),
        isLoading = false,
        onItemChanged = {},
        onKeystoreRemoveIconClick = {}
    )
}

@Composable
@Preview
private fun OutputFormPreview() {
    OutputForm(
        onFormDataChange = {},
        extractorFormData = ExtractorFormData(selectedExtractOption = RadioItem(text = "aaaa")),
        isLoading = false,
        selectedExtractOption = RadioItem(text = "aaaa"),
        onItemSelected = {}
    )
}
