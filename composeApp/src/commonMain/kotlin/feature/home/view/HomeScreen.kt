package feature.home.view

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import feature.home.model.ExtractorFormData
import feature.home.model.ExtractorFormDataCallback
import feature.home.model.InputPathType
import feature.home.state.HomeIntent
import feature.home.viewmodel.HomeViewModel
import ui.components.ErrorDialog
import ui.theme.MarginPaddingSizeMedium
import ui.theme.MarginPaddingSizeSmall

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel { HomeViewModel() }
) {

    val homeUiState by homeViewModel.homeState.collectAsState()

    var showFilePicker by remember { mutableStateOf(false) }
    var showDirPicker by remember { mutableStateOf(false) }
    var inputType by remember { mutableStateOf(InputPathType.NONE) }
    var fileType by remember { mutableStateOf(listOf("")) }


    val showErrorDialog = remember { mutableStateOf(false) }
    var extractorFormData by remember { mutableStateOf(ExtractorFormData()) }

    val onFormDataChange: (ExtractorFormData) -> Unit = { newFormData ->
        extractorFormData = newFormData
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(homeUiState.errorMsg.id) {
        if (homeUiState.errorMsg.title.isNotEmpty() && homeUiState.errorMsg.msg.isNotEmpty()) {
            showErrorDialog.value = true
        } else {
            showErrorDialog.value = false
        }
    }

    LaunchedEffect(homeUiState.successMsg.id) {
        if (homeUiState.successMsg.msg.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = homeUiState.successMsg.msg
            )
        }
    }

    ErrorDialog(
        showDialog = showErrorDialog,
        errorMsg = homeUiState.errorMsg
    )

    val extractorFormDataCallback = ExtractorFormDataCallback(
        onAdbPathIconClick = {
            showDirPicker = true
            inputType = InputPathType.ADB_DIR_PATH
        },
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
        onOutputPathIconClick = {
            showDirPicker = true
            inputType = InputPathType.OUTPUT_DIR_PATH
        }
    )

    extractorFormData = extractorFormData.copy(
        adbPath = "/home/felipe/Development/Android/Sdk/platform-tools",
        keystorePath = "/home/felipe/Downloads/teste.jks",
        keystorePassword = "testeteste",
        keystoreAlias = "teste",
        keyPassword = "testeteste",
        aabPath = "/home/felipe/Downloads/8.4.1-1936.aab",
        outputApksPath = "/home/felipe/Downloads",
        isOverwriteApks = false
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

    DirectoryPicker(showDirPicker) { path ->
        showDirPicker = false
        when (inputType) {
            InputPathType.ADB_DIR_PATH ->
                extractorFormData = extractorFormData.copy(adbPath = path ?: "")
            InputPathType.OUTPUT_DIR_PATH ->
                extractorFormData = extractorFormData.copy(outputApksPath = path ?: "")
            else -> {}
        }
        inputType = InputPathType.NONE
    }

    Scaffold(
        scaffoldState = rememberScaffoldState(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExtractorContent(
                extractorFormData = extractorFormData,
                extractorFormDataCallback = extractorFormDataCallback,
                onFormDataChange = onFormDataChange,
                isLoading = homeUiState.loading,
                onExtractApksButtonClick = {
                    homeViewModel.sendIntent(
                        HomeIntent.ExtractAab(
                            extractorFormData = extractorFormData
                        )
                    )
                },
                onInstallExtractedApksButtonClick = {

                }
            )
        }

        if (homeUiState.loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ExtractorContent(
    extractorFormData: ExtractorFormData,
    extractorFormDataCallback: ExtractorFormDataCallback,
    isLoading: Boolean,
    onFormDataChange: (ExtractorFormData) -> Unit,
    onExtractApksButtonClick: () -> Unit,
    onInstallExtractedApksButtonClick: () -> Unit
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

            Button(
                modifier = Modifier.weight(1f),
                content = {
                    Text("INSTALL EXTRACTED APKS")
                },
                onClick = onInstallExtractedApksButtonClick,
                enabled = !isLoading
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
        AdbForm(
            extractorFormData = extractorFormData,
            onFormDataChange = onFormDataChange,
            isLoading = isLoading,
            onAdbPathIconClick = extractorFormDataCallback.onAdbPathIconClick
        )
        Spacer(modifier = spacerModifier)
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
            onAabPathIconClick = extractorFormDataCallback.onAabPathIconClick,
            onOutputPathIconClick = extractorFormDataCallback.onOutputPathIconClick
        )
    }
}

@Composable
fun AdbForm(
    extractorFormData: ExtractorFormData,
    onFormDataChange: (ExtractorFormData) -> Unit,
    isLoading: Boolean,
    onAdbPathIconClick: () -> Unit
) {
    FormCard(
        title = "ADB"
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = extractorFormData.adbPath,
            enabled = !isLoading,
            onValueChange = {
                onFormDataChange(extractorFormData.copy(adbPath = it))
            },
            label = {
                Text("ADB Dir Path")
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable {
                        if (!isLoading) {
                            onAdbPathIconClick()
                        }
                    },
                    imageVector = Icons.Rounded.FolderOpen,
                    contentDescription = null
                )
            }
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

    FormCard(
        modifier = Modifier.fillMaxWidth(),
        title = "Kesytore Sign"
    ) {
        OutlinedTextField(
            modifier = inputModifier,
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
    onAabPathIconClick: () -> Unit,
    onOutputPathIconClick: () -> Unit
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

        OutlinedTextField(
            modifier = inputModifier
                .padding(top = MarginPaddingSizeSmall),
            value = extractorFormData.outputApksPath,
            enabled = !isLoading,
            onValueChange = {
                onFormDataChange(extractorFormData.copy(outputApksPath = it))
            },
            label = {
                Text("Output Dir Path (apks)")
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable {
                        if (!isLoading) {
                            onOutputPathIconClick()
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
                enabled = isLoading
            )
            Text(
                text = "Overwrite APKS"
            )
        }
    }
}

@Composable
fun ConverterForm() {
    Column {
        
    }
}

@Composable
@Preview
private fun AdbFormPreview() {
    AdbForm(
        onFormDataChange = {},
        extractorFormData = ExtractorFormData(),
        onAdbPathIconClick = {},
        isLoading = false
    )
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
        onOutputPathIconClick = {},
        isLoading = false
    )
}