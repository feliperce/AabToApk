package io.github.feliperce.aabtoapk.feature.extractor.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.feature.extractor.model.ExtractorOption
import io.github.feliperce.aabtoapk.feature.extractor.model.ExtractorResponseDto
import io.github.feliperce.aabtoapk.feature.extractor.state.ExtractorIntent
import io.github.feliperce.aabtoapk.feature.extractor.viewmodel.ExtractorViewModel
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ui.components.*
import ui.theme.*

@Composable
fun ExtractorScreen() {
    val extractorViewModel: ExtractorViewModel = koinViewModel()

    val extractorUiState by extractorViewModel.extractorState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var showKeystoreForm by remember { mutableStateOf(false) }
    var passwordText by remember { mutableStateOf("") }
    var aliasText by remember { mutableStateOf("") }
    var keyPasswordText by remember { mutableStateOf("") }

    val uriHandler = LocalUriHandler.current

    val scope = rememberCoroutineScope()

    val extractorOptionsList = listOf(
        RadioItem(
            id = ExtractorOption.APKS.id,
            text = "APKS",
            data = ExtractorOption.APKS,
            isSelected = true
        ),
        RadioItem(
            id = ExtractorOption.APK.id,
            data = ExtractorOption.APK,
            text = "Universal APK"
        )
    )

    var selectedOption by remember { mutableStateOf(extractorOptionsList[0]) }

    LaunchedEffect(extractorUiState.errorMsg?.id) {
        extractorUiState.errorMsg?.let { error ->
            snackbarHostState
                .showSnackbar(
                    message = error.msg,
                    duration = SnackbarDuration.Long
                )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(1000.dp)
            ) {
                ExtractorContent(
                    isLoading = extractorUiState.loading,
                    extractOptions = extractorOptionsList,
                    extractorResponseDto = extractorUiState.extractorResponseDto,
                    onDebugKeystoreChecked = {
                        showKeystoreForm = !it
                        extractorUiState.keystore = extractorUiState.keystore.copy(
                            isDebugKeystore = it
                        )
                    },
                    onItemSelected = {
                        selectedOption = it
                        extractorUiState.aabFileDto = extractorUiState.aabFileDto.copy(
                            extractorOption = it.data as ExtractorOption
                        )
                    },
                    onFileResult = {
                        scope.launch {
                            extractorUiState.aabFileDto = extractorUiState.aabFileDto.copy(
                                aabByteArray = it.readBytes(),
                                fileName = it.name,
                                fileSize = it.getSize() ?: 0
                            )
                        }
                    },
                    selectedOption = selectedOption,
                    showKeystoreForm = showKeystoreForm,
                    onKeystoreFileResult = {
                        scope.launch {
                            extractorUiState.keystore = extractorUiState.keystore.copy(
                                keystoreFileName = it.name,
                                keystoreByteArray = it.readBytes()
                            )
                        }
                    },
                    onPasswordFieldChange = {
                        passwordText = it
                        extractorUiState.keystore = extractorUiState.keystore.copy(
                            password = it
                        )
                    },
                    onAliasFieldChange = {
                        aliasText = it
                        extractorUiState.keystore = extractorUiState.keystore.copy(
                            alias = it
                        )
                    },
                    onKeyPasswordFieldChange = {
                        keyPasswordText = it
                        extractorUiState.keystore = extractorUiState.keystore.copy(
                            keyPassword = it
                        )
                    },
                    onUploadButtonClick = {
                        extractorUiState.extractorResponseDto = null
                        extractorViewModel.sendIntent(
                            ExtractorIntent.UploadAndExtract(
                                keystoreDto = extractorUiState.keystore,
                                aabFileDto = extractorUiState.aabFileDto
                            )
                        )
                    },
                    onDownloadButtonClick = {
                        extractorUiState.extractorResponseDto?.let {
                            uriHandler.openUri(it.downloadUrl)
                        }
                    },
                    passwordText = passwordText,
                    aliasText = aliasText,
                    keyPasswordText = keyPasswordText
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

    }
}

@Composable
fun ExtractorContent(
    isLoading: Boolean,
    showKeystoreForm: Boolean,
    extractOptions: List<RadioItem>,
    selectedOption: RadioItem,
    extractorResponseDto: ExtractorResponseDto?,
    onDebugKeystoreChecked: (isChecked: Boolean) -> Unit,
    onItemSelected: (item: RadioItem) -> Unit,
    onFileResult: (platformFile: PlatformFile) -> Unit,
    onKeystoreFileResult: (platformFile: PlatformFile) -> Unit,
    onPasswordFieldChange: (text: String) -> Unit,
    onAliasFieldChange: (text: String) -> Unit,
    onKeyPasswordFieldChange: (text: String) -> Unit,
    onUploadButtonClick: () -> Unit,
    onDownloadButtonClick: () -> Unit,
    passwordText: String,
    aliasText: String,
    keyPasswordText: String
) {
    val buttonsWidth = 200.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MarginPaddingSizeMedium)
    ) {
        MessageBox(
            isLoading = isLoading,
            extractorResponseDto = extractorResponseDto,
        )

        UploadForm(
            modifier = Modifier.padding(top = MarginPaddingSizeMedium),
            isLoading = isLoading,
            extractOptions = extractOptions,
            selectedOption = selectedOption,
            onDebugKeystoreChecked = onDebugKeystoreChecked,
            onItemSelected = onItemSelected,
            onFileResult = onFileResult
        )

        AnimatedVisibility(showKeystoreForm) {
            KeystoreForm(
                modifier = Modifier.padding(top = MarginPaddingSizeMedium),
                isLoading = isLoading,
                onKeystoreFileResult = onKeystoreFileResult,
                onPasswordFieldChange = onPasswordFieldChange,
                onAliasFieldChange = onAliasFieldChange,
                onKeyPasswordFieldChange = onKeyPasswordFieldChange,
                passwordText = passwordText,
                aliasText = aliasText,
                keyPasswordText = keyPasswordText
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier
                    .padding(top = MarginPaddingSizeMedium)
                    .width(buttonsWidth),
                onClick = onUploadButtonClick,
                content = {
                    Text("UPLOAD AND EXTRACT")
                }
            )

            AnimatedVisibility(
                modifier = Modifier.width(buttonsWidth),
                visible = extractorResponseDto != null
            ) {
                TweenButton(
                    modifier = Modifier
                        .padding(
                            top = MarginPaddingSizeMedium,
                            start = MarginPaddingSizeMedium
                        ),
                    text = "DOWNLOAD",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple600
                    ),
                    onClick = onDownloadButtonClick
                )
            }
        }

    }
}

@Composable
fun MessageBox(
    isLoading: Boolean,
    extractorResponseDto: ExtractorResponseDto?
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        extractorResponseDto?.let { extractorResponse ->
            MessageCard(
                msg = "${extractorResponse.fileName} extracted with success!",
                title = "SUCCESS!",
                cardType = CardType.SUCCESS
            )
        } ?: run {
            if (isLoading) {
                MessageCard(
                    msg = "During upload there may be a \"stuck\", don't worry, wait for the upload process",
                    title = "Upload lag",
                    cardType = CardType.INFO
                )
            }
        }
    }
}

@Composable
fun UploadForm(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    extractOptions: List<RadioItem>,
    selectedOption: RadioItem,
    onDebugKeystoreChecked: (isChecked: Boolean) -> Unit,
    onItemSelected: (item: RadioItem) -> Unit,
    onFileResult: (platformFile: PlatformFile) -> Unit,
) {
    val inputModifier = modifier.fillMaxWidth()
    var isChecked by remember { mutableStateOf(true) }

    FormCard(
        modifier =  modifier,
        title = "Extractor",
        formCardContent = {
            FilePickerTextField(
                modifier = inputModifier,
                enabled = !isLoading,
                onFileResult = { platformFile ->
                    onFileResult(platformFile)
                },
                label = "AAB Path",
                fileType = aabInputType,
                pickerTitle = "AAB",
                supportingText = "Max size: ${ServerConstants.MAX_AAB_UPLOAD_MB} mb"
            )

            Row {
                RadioGroup(
                    radioOptions = extractOptions,
                    isEnabled = !isLoading,
                    onItemSelected = onItemSelected,
                    selectedOption = selectedOption
                )

                Row(
                    modifier = inputModifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            isChecked = it
                            onDebugKeystoreChecked(it)
                        },
                    )

                    Text(
                        modifier = Modifier.padding(start = MarginPaddingSizeSmall),
                        text = "Use debug keystore"
                    )
                }
            }
        }
    )
}

@Composable
fun KeystoreForm(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onKeystoreFileResult: (platformFile: PlatformFile) -> Unit,
    onPasswordFieldChange: (text: String) -> Unit,
    onAliasFieldChange: (text: String) -> Unit,
    onKeyPasswordFieldChange: (text: String) -> Unit,
    passwordText: String,
    aliasText: String,
    keyPasswordText: String
) {
    FormCard(
        modifier = modifier.fillMaxWidth(),
        title = "Keystore",
        isActionButtonEnabled = !isLoading
    ) {
        val inputModifier = Modifier
            .fillMaxWidth()
            .padding(top = MarginPaddingSizeSmall)

        FilePickerTextField(
            modifier = inputModifier.padding(top = MarginPaddingSizeMedium),
            enabled = !isLoading,
            onFileResult = onKeystoreFileResult,
            label = "Keystore Path",
            fileType = keystoreInputType,
            pickerTitle = "Keystore"
        )

        OutlinedTextField(
            modifier = inputModifier,
            value = passwordText,
            enabled = !isLoading,
            onValueChange = { text ->
                onPasswordFieldChange(text)
            },
            label = { Text("Keystore Password") }
        )

        OutlinedTextField(
            modifier = inputModifier,
            value = aliasText,
            enabled = !isLoading,
            onValueChange = { text ->
                onAliasFieldChange(text)
            },
            label = { Text("Alias") }
        )

        OutlinedTextField(
            modifier = inputModifier,
            value = keyPasswordText,
            enabled = !isLoading,
            onValueChange = { text ->
                onKeyPasswordFieldChange(text)
            },
            label = { Text("Key Password") }
        )
    }
}

@Composable
@Preview
private fun UploadFormPreview() {
    UploadForm(
        isLoading = false,
        extractOptions = fakeExtractOptions,
        onDebugKeystoreChecked = {},
        onItemSelected = {},
        onFileResult = {},
        selectedOption = fakeExtractOptions[0]
    )
}

private val fakeExtractOptions = listOf(
    RadioItem(
        text = "APK",
        isSelected = true
    ),
    RadioItem(
        text = "APKS",
        isSelected = false
    )
)