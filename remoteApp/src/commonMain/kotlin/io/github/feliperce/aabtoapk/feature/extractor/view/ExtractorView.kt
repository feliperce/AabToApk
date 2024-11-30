package io.github.feliperce.aabtoapk.feature.extractor.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.feliperce.aabtoapk.feature.extractor.model.AabFileDto
import io.github.feliperce.aabtoapk.feature.extractor.model.ExtractorOption
import io.github.feliperce.aabtoapk.feature.extractor.model.KeystoreDto
import io.github.feliperce.aabtoapk.feature.extractor.state.ExtractorIntent
import io.github.feliperce.aabtoapk.feature.extractor.viewmodel.ExtractorViewModel
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ui.components.*
import ui.theme.MarginPaddingSizeMedium
import ui.theme.MarginPaddingSizeSmall

@Composable
fun ExtractorScreen() {
    val extractorViewModel: ExtractorViewModel = koinViewModel()

    val extractorUiState by extractorViewModel.extractorState.collectAsState()

    var isDebugKeystore by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    val extractorOptionsList = listOf(
        RadioItem(
            id = ExtractorOption.APKS.name,
            text = "APKS",
            data = ExtractorOption.APKS,
            isSelected = true
        ),
        RadioItem(
            id = ExtractorOption.APK.name,
            data = ExtractorOption.APK,
            text = "Universal APK"
        )
    )

    var selectedOption by remember { mutableStateOf(extractorOptionsList[0]) }

    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ExtractorContent(
                isLoading = false,
                extractOptions = extractorOptionsList,
                onDebugKeystoreChecked = {
                    isDebugKeystore = it
                    extractorUiState.keystore = extractorUiState.keystore.copy(
                        isDebugKeystore = it
                    )
                },
                onItemSelected = {
                    selectedOption = it
                },
                onFileResult = {
                    scope.launch {
                        extractorUiState.aabFileDto = extractorUiState.aabFileDto.copy(
                            aabByteArray = it.readBytes(),
                            fileName = it.name
                        )
                    }
                },
                selectedOption = selectedOption,
                showKeystoreForm = !isDebugKeystore,
                onKeystoreFileResult = {
                    scope.launch {
                        extractorUiState.keystore = extractorUiState.keystore.copy(
                            keystoreFileName = it.name,
                            keystoreByteArray = it.readBytes()
                        )
                    }
                },
                onPasswordFieldChange = {
                    extractorUiState.keystore = extractorUiState.keystore.copy(
                        password = it
                    )
                },
                onAliasFieldChange = {
                    extractorUiState.keystore = extractorUiState.keystore.copy(
                        alias = it
                    )
                },
                onKeyPasswordFieldChange = {
                    extractorUiState.keystore = extractorUiState.keystore.copy(
                        keyPassword = it
                    )
                },
                onUploadButtonClick = {
                    extractorViewModel.sendIntent(
                        ExtractorIntent.UploadAndExtract(
                            keystoreDto = extractorUiState.keystore,
                            aabFileDto = extractorUiState.aabFileDto
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun ExtractorContent(
    isLoading: Boolean,
    showKeystoreForm: Boolean,
    extractOptions: List<RadioItem>,
    selectedOption: RadioItem,
    onDebugKeystoreChecked: (isChecked: Boolean) -> Unit,
    onItemSelected: (item: RadioItem) -> Unit,
    onFileResult: (platformFile: PlatformFile) -> Unit,
    onKeystoreFileResult: (platformFile: PlatformFile) -> Unit,
    onPasswordFieldChange: (text: String) -> Unit,
    onAliasFieldChange: (text: String) -> Unit,
    onKeyPasswordFieldChange: (text: String) -> Unit,
    onUploadButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MarginPaddingSizeMedium)
    ) {
        UploadForm(
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
                onKeyPasswordFieldChange = onKeyPasswordFieldChange
            )
        }

        Button(
            modifier = Modifier
                .padding(top = MarginPaddingSizeMedium)
                .fillMaxWidth(),
            onClick = onUploadButtonClick,
            content = {
                Text("UPLOAD AND EXTRACT")
            }
        )
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
                pickerTitle = "AAB"
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
    onKeyPasswordFieldChange: (text: String) -> Unit
) {
    var passwordText by remember { mutableStateOf("") }
    var aliasText by remember { mutableStateOf("") }
    var keyPasswordText by remember { mutableStateOf("") }

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
                passwordText = text
                onPasswordFieldChange(text)
            },
            label = { Text("Keystore Password") }
        )

        OutlinedTextField(
            modifier = inputModifier,
            value = aliasText,
            enabled = !isLoading,
            onValueChange = { text ->
                aliasText = text
                onAliasFieldChange(text)
            },
            label = { Text("Alias") }
        )

        OutlinedTextField(
            modifier = inputModifier,
            value = keyPasswordText,
            enabled = !isLoading,
            onValueChange = { text ->
                keyPasswordText = text
                onKeyPasswordFieldChange(text)
            },
            label = { Text("Key Password") }
        )
    }
}

@Composable
@Preview
fun UploadFormPreview() {
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