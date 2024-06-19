package feature.extractor.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import feature.extractor.model.ExtractorFormData
import feature.extractor.model.ExtractorFormDataCallback
import feature.extractor.state.ExtractorIntent
import feature.extractor.viewmodel.ExtractorViewModel
import utils.InputPathType
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.components.*
import ui.theme.Black
import ui.theme.MarginPaddingSizeMedium
import ui.theme.MarginPaddingSizeSmall
import utils.SuccessMsgType

@Composable
fun ExtractorScreen(
    extractorViewModel: ExtractorViewModel = viewModel { ExtractorViewModel() },
    snackbarHostState: SnackbarHostState
) {
    val extractorUiState by extractorViewModel.extractorState.collectAsState()

    var showFilePicker by remember { mutableStateOf(false) }
    var inputType by remember { mutableStateOf(InputPathType.NONE) }
    var fileType by remember { mutableStateOf(listOf("")) }

    val showErrorDialog = remember { mutableStateOf(false) }
    var extractorFormData by remember { mutableStateOf(ExtractorFormData()) }

    val onFormDataChange: (ExtractorFormData) -> Unit = { newFormData ->
        extractorFormData = newFormData
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
                        actionLabel = "INSTALL APKS",
                        duration = SnackbarDuration.Indefinite
                    )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        extractorViewModel.sendIntent(
                            ExtractorIntent.InstallApks(
                                extractorFormData = extractorFormData
                            )
                        )
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

    ErrorDialog(
        showDialog = showErrorDialog,
        errorMsg = extractorUiState.errorMsg
    )

    val extractorFormDataCallback = ExtractorFormDataCallback(
        onKeystorePathIconClick = {
            showFilePicker = true
            inputType = InputPathType.KEYSTORE_PATH
            fileType = listOf("keystore", "jks")
        },
        onAabPathIconClick = {
            showFilePicker = true
            inputType = InputPathType.AAB_PATH
            fileType = listOf("aab")
        },
    )

    extractorFormData = extractorFormData.copy(
        adbPath = "/home/felipe/Development/Android/Sdk/platform-tools/adb",
        keystorePath = "/home/felipe/Downloads/teste.jks",
        keystorePassword = "testeteste",
        keystoreAlias = "teste",
        keyPassword = "testeteste",
        aabPath = "/home/felipe/Downloads/8.6.0-1939.aab",
        outputApksPath = "/home/felipe/Downloads",
        isOverwriteApks = true
    )

    FilePicker(show = showFilePicker, fileExtensions = fileType) { platformFile ->
        showFilePicker = false
        when (inputType) {
            InputPathType.KEYSTORE_PATH ->
                extractorFormData = extractorFormData.copy(keystorePath = platformFile?.path ?: "")
            InputPathType.AAB_PATH ->
                extractorFormData = extractorFormData.copy(aabPath = platformFile?.path ?: "")
            else -> {}
        }
        inputType = InputPathType.NONE
    }

    Column(
        modifier = Modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExtractorContent(
            extractorFormData = extractorFormData,
            extractorFormDataCallback = extractorFormDataCallback,
            onFormDataChange = onFormDataChange,
            isLoading = extractorUiState.loading,
            onExtractApksButtonClick = {
                extractorViewModel.sendIntent(
                    ExtractorIntent.ExtractAab(
                        extractorFormData = extractorFormData
                    )
                )
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
    extractorFormDataCallback: ExtractorFormDataCallback,
    isLoading: Boolean,
    onFormDataChange: (ExtractorFormData) -> Unit,
    onExtractApksButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(MarginPaddingSizeMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExtractorForm(
            isLoading = isLoading,
            extractorFormData = extractorFormData,
            extractorFormDataCallback = extractorFormDataCallback,
            onFormDataChange = onFormDataChange
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
    extractorFormData: ExtractorFormData,
    extractorFormDataCallback: ExtractorFormDataCallback,
    isLoading: Boolean,
    onFormDataChange: (ExtractorFormData) -> Unit,
) {
    val spacerModifier = Modifier
        .fillMaxWidth()
        .padding(top = MarginPaddingSizeMedium)

    Column {
        KeystoreSignForm(
            extractorFormData = extractorFormData,
            onFormDataChange = onFormDataChange,
            isLoading = isLoading,
            onKeystorePathIconClick = extractorFormDataCallback.onKeystorePathIconClick
        )
        Spacer(modifier = spacerModifier)
        OutputForm(
            extractorFormData = extractorFormData,
            onFormDataChange = onFormDataChange,
            isLoading = isLoading,
            onAabPathIconClick = extractorFormDataCallback.onAabPathIconClick
        )
    }
}

@Composable
fun KeystoreSignForm(
    extractorFormData: ExtractorFormData,
    onFormDataChange: (ExtractorFormData) -> Unit,
    isLoading: Boolean,
    onKeystorePathIconClick: () -> Unit
) {
    val inputModifier = Modifier.fillMaxWidth()

    val spinnerItems = listOf(
        SpinnerItem(
            "Aaaaaaa",
            0
        ),
        SpinnerItem(
            "dasf afs",
            0
        ),
        SpinnerItem(
            "f asf as2",
            0
        )
    )

    FormCard(
        modifier = Modifier.fillMaxWidth(),
        title = "Kesytore Sign"
    ) {
        SpinnerTextInput(
            modifier = inputModifier,
            title = "Name",
            items = spinnerItems,
            supportingText = "Select a name to save keystore information, leave it empty to not save"
        )

        OutlinedTextField(
            modifier = inputModifier.padding(top = MarginPaddingSizeMedium),
            value = extractorFormData.keystorePath,
            enabled = !isLoading,
            onValueChange = {
                onFormDataChange(extractorFormData.copy(keystorePath = it))
            },
            label = {
                Text("Keystore Path")
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable { onKeystorePathIconClick() },
                    imageVector = Icons.Rounded.FolderOpen,
                    contentDescription = null
                )
            }
        )

        OutlinedTextField(
            modifier = inputModifier
                .padding(top = MarginPaddingSizeSmall),
            value = extractorFormData.keystorePassword,
            enabled = !isLoading,
            onValueChange = {
                onFormDataChange(extractorFormData.copy(keystorePassword = it))
            },
            label = {
                Text("Keystore Password")
            }
        )

        OutlinedTextField(
            modifier = inputModifier
                .padding(top = MarginPaddingSizeSmall),
            value = extractorFormData.keystoreAlias,
            enabled = !isLoading,
            onValueChange = {
                onFormDataChange(extractorFormData.copy(keystoreAlias = it))
            },
            label = {
                Text("Alias")
            }
        )

        OutlinedTextField(
            modifier = inputModifier
                .padding(top = MarginPaddingSizeSmall),
            value = extractorFormData.keyPassword,
            enabled = !isLoading,
            onValueChange = {
                onFormDataChange(extractorFormData.copy(keyPassword = it))
            },
            label = {
                Text("Key Password")
            }
        )
    }
}

@Composable
fun FormCard(
    modifier: Modifier = Modifier,
    title: String,
    formCardContent: @Composable () -> Unit
) {
    Card(
        elevation = 8.dp
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(MarginPaddingSizeMedium)
        ) {
            Text(
                modifier = Modifier
                    .padding(bottom = MarginPaddingSizeSmall)
                    .fillMaxWidth(),
                fontWeight = FontWeight.W500,
                text = title
            )
            formCardContent()
        }
    }
}

@Composable
fun OutputForm(
    extractorFormData: ExtractorFormData,
    onFormDataChange: (ExtractorFormData) -> Unit,
    isLoading: Boolean,
    onAabPathIconClick: () -> Unit
) {
    val inputModifier = Modifier.fillMaxWidth()

    FormCard(
        modifier = Modifier.fillMaxWidth(),
        title = "AAB Extract / Install"
    ) {
        OutlinedTextField(
            modifier = inputModifier,
            value = extractorFormData.aabPath,
            enabled = !isLoading,
            onValueChange = {
                onFormDataChange(extractorFormData.copy(aabPath = it))
            },
            label = {
                Text("AAB Path")
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable {
                        if (!isLoading) {
                            onAabPathIconClick()
                        }
                    },
                    imageVector = Icons.Rounded.FolderOpen,
                    contentDescription = null
                )
            }
        )



        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Checkbox(
                checked = extractorFormData.isOverwriteApks,
                onCheckedChange = {
                    onFormDataChange(extractorFormData.copy(isOverwriteApks = it))
                },
                enabled = !isLoading
            )
            Text(
                text = "Overwrite APKS"
            )
        }
    }
}

@Composable
@Preview
private fun KeystoreSignFormPreview() {
    KeystoreSignForm(
        onFormDataChange = {},
        extractorFormData = ExtractorFormData(),
        onKeystorePathIconClick = {},
        isLoading = false
    )
}

@Composable
@Preview
private fun OutputFormPreview() {
    OutputForm(
        onFormDataChange = {},
        extractorFormData = ExtractorFormData(),
        onAabPathIconClick = {},
        isLoading = false
    )
}