package feature.extractor.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import feature.extractor.mapper.KeystoreDto
import feature.extractor.model.ExtractorFormData
import feature.extractor.model.ExtractorFormDataCallback
import feature.extractor.state.ExtractorIntent
import feature.extractor.viewmodel.ExtractorViewModel
import utils.InputPathType
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ui.components.*
import ui.theme.MarginPaddingSizeMedium
import ui.theme.MarginPaddingSizeSmall
import utils.SuccessMsgType

@Composable
fun ExtractorScreen(snackbarHostState: SnackbarHostState) {
    val extractorViewModel: ExtractorViewModel = koinViewModel()

    val extractorUiState by extractorViewModel.extractorState.collectAsState()

    var showFilePicker by remember { mutableStateOf(false) }
    var inputType by remember { mutableStateOf(InputPathType.NONE) }
    var fileType by remember { mutableStateOf(listOf("")) }
    var currentKeystoreDto by remember { mutableStateOf(KeystoreDto()) }

    val showErrorDialog = remember { mutableStateOf(false) }
    var extractorFormData by remember { mutableStateOf(ExtractorFormData()) }

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

    LaunchedEffect(extractorUiState.settingsData) {
        extractorFormData = extractorFormData.copy(
            settingsData = extractorUiState.settingsData
        )
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
        onItemChanged = { keystoreDto ->
            extractorFormData = extractorFormData.copy(
                keystoreDto = keystoreDto
            )
        }
    )

    /*extractorFormData = extractorFormData.copy(
        keystorePath = "/home/felipe/Downloads/teste.jks",
        keystorePassword = "testeteste",
        keystoreAlias = "teste",
        keyPassword = "testeteste",
        aabPath = "/home/felipe/Downloads/8.6.0-1939.aab",
        isOverwriteApks = true
    )*/

    FilePicker(show = showFilePicker, fileExtensions = fileType) { platformFile ->
        showFilePicker = false
        when (inputType) {
            InputPathType.KEYSTORE_PATH ->
                extractorFormData = extractorFormData.copy(
                    keystoreDto = extractorFormData.keystoreDto.copy(
                        path = platformFile?.path ?: ""
                    )
                )
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
            keystoreDtoList = extractorUiState.keystoreDtoList,
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

                extractorFormData.keystoreDto.let { keystoreDto ->
                    println("ID FORA -> ${keystoreDto?.id}")
                    //if (keystoreDto.name.isNotEmpty()) {
                        extractorViewModel.sendIntent(
                            ExtractorIntent.SaveKeystore(
                                keystoreDto = keystoreDto
                            )
                        )
                    //}
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
                WarningCard(
                    msg = "Some settings need to be configured to use this function, go to \"Settings\" and set"
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
    keystoreDtoList: List<KeystoreDto>,
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
            keystoreDtoList = keystoreDtoList,
            extractorFormData = extractorFormData,
            onFormDataChange = onFormDataChange,
            isLoading = isLoading,
            onKeystorePathIconClick = extractorFormDataCallback.onKeystorePathIconClick,
            onItemChanged = extractorFormDataCallback.onItemChanged
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
    keystoreDtoList: List<KeystoreDto>,
    extractorFormData: ExtractorFormData,
    onFormDataChange: (ExtractorFormData) -> Unit,
    onItemChanged: (keystoreDto: KeystoreDto) -> Unit,
    isLoading: Boolean,
    onKeystorePathIconClick: () -> Unit
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
        onActionClick = {

        }
    ) {
        SpinnerTextInput(
            modifier = inputModifier,
            title = "Name",
            items = spinnerItems,
            supportingText = "Select a name to save keystore information, leave it empty to not save",
            onItemChanged = { spinnerItem: SpinnerItem ->
                val keystoreDto = (spinnerItem.data as KeystoreDto?)?.copy(
                    name = spinnerItem.name,
                    id = if (spinnerItem.name.isEmpty()) {
                        null
                    } else {
                        spinnerItem.data?.id
                    }
                )
                keystoreDto?.let { onItemChanged(it) }
            }
        )

        OutlinedTextField(
            modifier = inputModifier.padding(top = MarginPaddingSizeMedium),
            value = extractorFormData.keystoreDto.path,
            enabled = !isLoading,
            onValueChange = {
                onFormDataChange(
                    extractorFormData.copy(
                        keystoreDto = extractorFormData.keystoreDto.copy(
                            path = it
                        )
                    )
                )
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
fun FormCard(
    modifier: Modifier = Modifier,
    title: String,
    actionIcon: ImageVector? = null,
    onActionClick: () -> Unit = {},
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = MarginPaddingSizeSmall)
                        .fillMaxWidth(),
                    fontWeight = FontWeight.W500,
                    text = title
                )
                actionIcon?.let {
                    IconButton(
                        onClick = onActionClick
                    ) {
                        Icon(
                            imageVector = it,
                            contentDescription = null
                        )
                    }
                }
            }

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
        keystoreDtoList = emptyList(),
        onFormDataChange = {},
        extractorFormData = ExtractorFormData(),
        onKeystorePathIconClick = {},
        isLoading = false,
        onItemChanged = {}
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