package feature.extractor.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
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
import io.github.feliperce.aabtoapk.utils.extractor.SuccessMsgType

@Composable
fun ExtractorScreen(snackbarHostState: SnackbarHostState) {
    val extractorViewModel: ExtractorViewModel = koinViewModel()

    val extractorUiState by extractorViewModel.extractorState.collectAsState()

    val extractorOptionsList = listOf(
        RadioItem(
            id = ApksExtractor.ExtractorOption.APKS.name,
            text = "APKS",
            data = ApksExtractor.ExtractorOption.APKS,
            isSelected = true
        ),
        RadioItem(
            id = ApksExtractor.ExtractorOption.UNIVERSAL_APK.name,
            data = ApksExtractor.ExtractorOption.UNIVERSAL_APK,
            text = "Universal APK"
        )
    )

    var showKeystoreRemoveDialog by remember { mutableStateOf(false) }

    val showErrorDialog = remember { mutableStateOf(false) }
    var extractorFormData by remember {
        mutableStateOf(
            ExtractorFormData(
                extractOptions = extractorOptionsList,
                selectedExtractOption = extractorOptionsList[0]
            )
        )
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
        showErrorDialog.value = extractorUiState.errorMsg.title.isNotEmpty() && extractorUiState.errorMsg.msg.isNotEmpty()
    }

    LaunchedEffect(extractorUiState.successMsg.id) {
        val successMsg = extractorUiState.successMsg

        if (successMsg.msg.isNotEmpty()) {
            if (successMsg.type == SuccessMsgType.EXTRACT_AAB) {
                val result = snackbarHostState
                    .showSnackbar(
                        message = successMsg.msg,
                        actionLabel = "INSTALL",
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
                        message = successMsg.msg,
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

    ErrorDialog(
        showDialog = showErrorDialog,
        msg = extractorUiState.errorMsg.msg
    )

    val extractorFormDataCallback = ExtractorFormDataCallback(
        onKeystoreSpinnerItemChanged = { keystoreDto ->
            extractorFormData = extractorFormData.copy(
                keystoreDto = keystoreDto
            )
        },
        onKeystoreRemoveClick = {
            showKeystoreRemoveDialog = true
        },
        onItemSelected = { item ->
            extractorFormData = extractorFormData.copy(
                selectedExtractOption = item
            )
        }
    )

    if (showKeystoreRemoveDialog) {
        KeystoreRemovalDialog(
            keystoreName = extractorFormData.keystoreDto.name,
            onDismiss = { showKeystoreRemoveDialog = false },
            onPositiveButtonClick = {
                extractorViewModel.sendIntent(
                    ExtractorIntent.RemoveKeystore(extractorFormData.keystoreDto)
                )

                extractorFormData = extractorFormData.copy(
                    keystoreDto = KeystoreDto()
                )
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
                        extractorViewModel.sendIntent(
                            ExtractorIntent.SaveKeystore(
                                keystoreDto = keystoreDto
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
                    msg = "Some settings need to be configured to use this function, go to \"Settings\" and set",
                    title = "PAY ATTENTION",
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
                    Text("AAB TO APKS")
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

    val spinnerItems = keystoreDtoList.map {
        SpinnerItem(
            name = it.name,
            data = it
        )
    }

    FormCard(
        modifier = Modifier.fillMaxWidth(),
        title = "Kesytore Sign",
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
            title = "Name",
            items = spinnerItems,
            isEnabled = !isLoading,
            supportingText = "Select a name to save keystore information, leave it empty to not save",
            onItemChanged = { spinnerItem: SpinnerItem ->
                var keystoreDto: KeystoreDto? = (spinnerItem.data as KeystoreDto?)

                keystoreDto = keystoreDto?.copy(
                    name = spinnerItem.name
                )?: KeystoreDto(
                    name = spinnerItem.name,
                    path = extractorFormData.keystoreDto.path,
                    password = extractorFormData.keystoreDto.password,
                    keyAlias = extractorFormData.keystoreDto.keyAlias,
                    keyPassword = extractorFormData.keystoreDto.keyPassword
                )

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
            label = "Keystore Path",
            fileType = keystoreInputType,
            pickerTitle = "Keystore"
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
                Text("Keystore Password")
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
                Text("Alias")
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
                Text("Key Password")
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
        title = "AAB Extract / Install"
    ) {
        FilePickerTextField(
            modifier = inputModifier,
            initialText = extractorFormData.aabPath,
            enabled = !isLoading,
            onFileResult = {
                onFormDataChange(extractorFormData.copy(aabPath = it.path ?: ""))
            },
            label = "AAB Path",
            fileType = aabInputType,
            pickerTitle = "AAB"
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
            Text("REMOVE KEYSTORE DATA")
        },
        text = {
            Text("Remove $keystoreName?")
        },
        confirmButton = {
            Button(
                onClick = {
                    onPositiveButtonClick()
                    onDismiss()
                },
                content = {
                    Text("REMOVE")
                }
            )
        }
    )
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