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
import com.android.ddmlib.Log
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import feature.home.model.ExtractorFormData
import feature.home.model.ExtractorFormDataCallback
import feature.home.model.InputPathType
import kotlinx.coroutines.launch
import ui.components.ErrorDialog
import ui.theme.MarginPaddingSizeMedium
import ui.theme.MarginPaddingSizeSmall
import utils.ApkExtractor
import java.io.File

@Composable
fun HomeScreen() {
    var showFilePicker by remember { mutableStateOf(false) }
    var showDirPicker by remember { mutableStateOf(false) }
    var inputType by remember { mutableStateOf(InputPathType.NONE) }
    var fileType by remember { mutableStateOf(listOf("")) }

    var extractorFormData by remember { mutableStateOf(ExtractorFormData()) }

    val showDialog = remember { mutableStateOf(false) }
    var errorTxt by remember { mutableStateOf(Pair("", "")) }

    val onFormDataChange: (ExtractorFormData) -> Unit = { newFormData ->
        extractorFormData = newFormData
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ErrorDialog(
        showDialog = showDialog,
        errorTitle = errorTxt.first,
        errorMsg = errorTxt.second
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

    val apkExtractor = ApkExtractor(
        adbPath = extractorFormData.adbPath,
        aabPath = extractorFormData.aabPath,
        outputApksPath = extractorFormData.outputApksPath
    )

    FilePicker(show = showFilePicker, fileExtensions = fileType) { platformFile ->
        showFilePicker = false
        when (inputType) {
            InputPathType.KEYSTORE_PATH -> extractorFormData = extractorFormData.copy(keystorePath = platformFile?.path ?: "")
            InputPathType.AAB_PATH -> extractorFormData = extractorFormData.copy(aabPath = platformFile?.path ?: "")
            else -> {}
        }
        inputType = InputPathType.NONE
    }

    DirectoryPicker(showDirPicker) { path ->
        showDirPicker = false
        when (inputType) {
            InputPathType.ADB_DIR_PATH -> extractorFormData = extractorFormData.copy(adbPath = path ?: "")
            InputPathType.OUTPUT_DIR_PATH -> extractorFormData = extractorFormData.copy(outputApksPath = path ?: "")
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
                onExtractApksButtonClick = {
                    scope.launch {
                        apkExtractor.setSignConfig(
                            keystorePath = extractorFormData.keystorePath,
                            keyAlias = extractorFormData.keystoreAlias,
                            keystorePassword = extractorFormData.keystorePassword,
                            keyPassword = extractorFormData.keyPassword,
                            onFailure = { errorTitle, errorMsg ->
                                showDialog.value = true
                                errorTxt = Pair(errorTitle, errorMsg)
                            }
                        )

                        apkExtractor.aabToApks(
                            apksFileName = File(extractorFormData.aabPath).nameWithoutExtension,
                            overwriteApks = extractorFormData.isOverwriteApks,
                            onSuccess = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Apks extracted with success!"
                                    )
                                }
                                Log.d("HOME-SCREEN", "SUCCESSS!!!!!!!!")
                            },
                            onFailure = { errorTitle, errorMsg ->
                                showDialog.value = true
                                errorTxt = Pair(errorTitle, errorMsg)
                            }
                        )
                    }
                },
                onInstallExtractedApksButtonClick = {

                }
            )
        }
    }
}

@Composable
fun ExtractorContent(
    extractorFormData: ExtractorFormData,
    extractorFormDataCallback: ExtractorFormDataCallback,
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
                onClick = onExtractApksButtonClick
            )

            Spacer(
                modifier = Modifier.width(MarginPaddingSizeSmall)
            )

            Button(
                modifier = Modifier.weight(1f),
                content = {
                    Text("INSTALL EXTRACTED APKS")
                },
                onClick = onInstallExtractedApksButtonClick
            )
        }
    }
}

@Composable
fun ExtractorForm(
    extractorFormData: ExtractorFormData,
    extractorFormDataCallback: ExtractorFormDataCallback,
    onFormDataChange: (ExtractorFormData) -> Unit,
) {
    val spacerModifier = Modifier
        .fillMaxWidth()
        .padding(top = MarginPaddingSizeMedium)

    Column {
        AdbForm(
            extractorFormData = extractorFormData,
            onFormDataChange = onFormDataChange,
            onAdbPathIconClick = extractorFormDataCallback.onAdbPathIconClick
        )
        Spacer(modifier = spacerModifier)
        KeystoreSignForm(
            extractorFormData = extractorFormData,
            onFormDataChange = onFormDataChange,
            onKeystorePathIconClick = extractorFormDataCallback.onKeystorePathIconClick
        )
        Spacer(modifier = spacerModifier)
        OutputForm(
            extractorFormData = extractorFormData,
            onFormDataChange = onFormDataChange,
            onAabPathIconClick = extractorFormDataCallback.onAabPathIconClick,
            onOutputPathIconClick = extractorFormDataCallback.onOutputPathIconClick
        )
    }
}

@Composable
fun AdbForm(
    extractorFormData: ExtractorFormData,
    onFormDataChange: (ExtractorFormData) -> Unit,
    onAdbPathIconClick: () -> Unit
) {
    FormCard(
        title = "ADB"
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = extractorFormData.adbPath,
            onValueChange = {
                onFormDataChange(extractorFormData.copy(adbPath = it))
            },
            label = {
                Text("ADB Dir Path")
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable { onAdbPathIconClick() },
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
            onValueChange = {
                onFormDataChange(extractorFormData.copy(aabPath = it))
            },
            label = {
                Text("AAB Path")
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable { onAabPathIconClick() },
                    imageVector = Icons.Rounded.FolderOpen,
                    contentDescription = null
                )
            }
        )

        OutlinedTextField(
            modifier = inputModifier
                .padding(top = MarginPaddingSizeSmall),
            value = extractorFormData.outputApksPath,
            onValueChange = {
                onFormDataChange(extractorFormData.copy(outputApksPath = it))
            },
            label = {
                Text("Output Dir Path (apks)")
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable { onOutputPathIconClick() },
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
        onAdbPathIconClick = {}
    )
}

@Composable
@Preview
private fun KeystoreSignFormPreview() {
    KeystoreSignForm(
        onFormDataChange = {},
        extractorFormData = ExtractorFormData(),
        onKeystorePathIconClick = {}
    )
}

@Composable
@Preview
private fun OutputFormPreview() {
    OutputForm(
        onFormDataChange = {},
        extractorFormData = ExtractorFormData(),
        onAabPathIconClick = {},
        onOutputPathIconClick = {}
    )
}