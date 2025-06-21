package io.github.feliperce.aabtoapk.feature.extractor.view

import aabtoapk.remoteapp.generated.resources.Res
import aabtoapk.remoteapp.generated.resources.ic_aabtoapk
import aabtoapk.remoteapp.generated.resources.ic_kotlin
import aabtoapk.remoteapp.generated.resources.apks
import aabtoapk.remoteapp.generated.resources.universal_apk
import aabtoapk.remoteapp.generated.resources.upload_and_extract
import aabtoapk.remoteapp.generated.resources.download
import aabtoapk.remoteapp.generated.resources.built_with
import aabtoapk.remoteapp.generated.resources.file_extracted_success
import aabtoapk.remoteapp.generated.resources.success
import aabtoapk.remoteapp.generated.resources.upload_lag_message
import aabtoapk.remoteapp.generated.resources.upload_lag
import aabtoapk.remoteapp.generated.resources.extractor
import aabtoapk.remoteapp.generated.resources.aab_path
import aabtoapk.remoteapp.generated.resources.aab
import aabtoapk.remoteapp.generated.resources.max_size
import aabtoapk.remoteapp.generated.resources.use_debug_keystore
import aabtoapk.remoteapp.generated.resources.keystore
import aabtoapk.remoteapp.generated.resources.keystore_path
import aabtoapk.remoteapp.generated.resources.keystore_password
import aabtoapk.remoteapp.generated.resources.hide_password
import aabtoapk.remoteapp.generated.resources.show_password
import aabtoapk.remoteapp.generated.resources.alias
import aabtoapk.remoteapp.generated.resources.enter_aab_file
import aabtoapk.remoteapp.generated.resources.enter_keystore_file
import aabtoapk.remoteapp.generated.resources.enter_keystore_key_alias
import aabtoapk.remoteapp.generated.resources.enter_keystore_key_password
import aabtoapk.remoteapp.generated.resources.enter_keystore_password
import aabtoapk.remoteapp.generated.resources.generic_error
import aabtoapk.remoteapp.generated.resources.key_password
import aabtoapk.remoteapp.generated.resources.max_file_size
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.feature.extractor.model.ExtractorOption
import io.github.feliperce.aabtoapk.feature.extractor.model.ExtractorResponseDto
import io.github.feliperce.aabtoapk.feature.extractor.state.ExtractorIntent
import io.github.feliperce.aabtoapk.feature.extractor.viewmodel.ExtractorViewModel
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ui.components.*
import ui.handler.DefaultErrorType
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
            text = stringResource(Res.string.apks),
            data = ExtractorOption.APKS,
            isSelected = true
        ),
        RadioItem(
            id = ExtractorOption.APK.id,
            data = ExtractorOption.APK,
            text = stringResource(Res.string.universal_apk)
        )
    )

    var selectedOption by remember { mutableStateOf(extractorOptionsList[0]) }

    val genericErrorMsg = stringResource(Res.string.generic_error)
    val keystorePasswordErrorMsg = stringResource(Res.string.enter_keystore_password)
    val keystoreKeyAliasErrorMsg = stringResource(Res.string.enter_keystore_key_alias)
    val keystoreKeyPasswordErrorMsg = stringResource(Res.string.enter_keystore_key_password)
    val keystoreFileErrorMsg = stringResource(Res.string.enter_keystore_file)
    val aabFileErrorMsg = stringResource(Res.string.enter_aab_file)
    val maxFileSizeErrorMsg = stringResource(Res.string.max_file_size, ServerConstants.MAX_AAB_UPLOAD_MB)

    LaunchedEffect(extractorUiState.errorMsg?.id) {
        extractorUiState.errorMsg?.let { error ->
            val errorMessage = when (error.type) {
                DefaultErrorType.GENERIC -> genericErrorMsg
                DefaultErrorType.KEYSTORE_PASSWORD -> keystorePasswordErrorMsg
                DefaultErrorType.KEYSTORE_KEY_ALIAS -> keystoreKeyAliasErrorMsg
                DefaultErrorType.KEYSTORE_KEY_PASSWORD -> keystoreKeyPasswordErrorMsg
                DefaultErrorType.KEYSTORE_FILE -> keystoreFileErrorMsg
                DefaultErrorType.AAB_FILE -> aabFileErrorMsg
                DefaultErrorType.MAX_FILE_SIZE -> maxFileSizeErrorMsg
                DefaultErrorType.NONE -> ""
            }
            snackbarHostState
                .showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Long
                )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Footer()
        }
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
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                ExtractorContent(
                    isLoading = extractorUiState.loading,
                    extractOptions = extractorOptionsList,
                    extractorResponseDto = extractorUiState.extractorResponseDto,
                    onDebugKeystoreChecked = {
                        showKeystoreForm = !it
                        extractorViewModel.sendIntent(
                            ExtractorIntent.UpdateKeystoreDebug(isDebugKeystore = it)
                        )
                    },
                    onItemSelected = {
                        selectedOption = it
                        extractorViewModel.sendIntent(
                            ExtractorIntent.UpdateExtractorOption(extractorOption = it.data as ExtractorOption)
                        )
                    },
                    onFileResult = {
                        extractorViewModel.sendIntent(
                            ExtractorIntent.UpdateAabFile(file = it)
                        )
                    },
                    selectedOption = selectedOption,
                    showKeystoreForm = showKeystoreForm,
                    onKeystoreFileResult = {
                        extractorViewModel.sendIntent(
                            ExtractorIntent.UpdateKeystoreFile(file = it)
                        )
                    },
                    onPasswordFieldChange = {
                        passwordText = it
                        extractorViewModel.sendIntent(
                            ExtractorIntent.UpdateKeystorePassword(password = it)
                        )
                    },
                    onAliasFieldChange = {
                        aliasText = it
                        extractorViewModel.sendIntent(
                            ExtractorIntent.UpdateKeystoreAlias(alias = it)
                        )
                    },
                    onKeyPasswordFieldChange = {
                        keyPasswordText = it
                        extractorViewModel.sendIntent(
                            ExtractorIntent.UpdateKeystoreKeyPassword(keyPassword = it)
                        )
                    },
                    onUploadButtonClick = {
                        extractorViewModel.sendIntent(ExtractorIntent.ResetExtractorResponse)
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
                enabled = !isLoading,
                content = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(Res.string.upload_and_extract))
                    }
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
                    text = stringResource(Res.string.download),
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
fun Footer() {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = MarginPaddingSizeMedium,
                end = MarginPaddingSizeMedium,
                bottom = MarginPaddingSizeMedium
            ),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(Res.string.built_with))
        Spacer(modifier = Modifier.padding(start = MarginPaddingSizeSmall))
        Icon(
            modifier = Modifier
                .height(20.dp)
                .clickable {
                    uriHandler.openUri("https://kotlinlang.org/docs/wasm-overview.html")
                },
            painter = painterResource(Res.drawable.ic_kotlin),
            contentDescription = null
        )
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
                msg = stringResource(Res.string.file_extracted_success, extractorResponse.fileName),
                title = stringResource(Res.string.success),
                cardType = CardType.SUCCESS
            )
        } ?: run {
            if (isLoading) {
                MessageCard(
                    msg = stringResource(Res.string.upload_lag_message),
                    title = stringResource(Res.string.upload_lag),
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
        title = stringResource(Res.string.extractor),
        formCardContent = {
            FilePickerTextField(
                modifier = inputModifier,
                enabled = !isLoading,
                onFileResult = { platformFile ->
                    onFileResult(platformFile)
                },
                label = stringResource(Res.string.aab_path),
                fileType = aabInputType,
                pickerTitle = stringResource(Res.string.aab),
                supportingText = stringResource(Res.string.max_size, ServerConstants.MAX_AAB_UPLOAD_MB)
            )

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioGroup(
                    radioOptions = extractOptions,
                    isEnabled = !isLoading,
                    onItemSelected = onItemSelected,
                    selectedOption = selectedOption
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MarginPaddingSizeSmall),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(
                        checked = isChecked,
                        enabled = !isLoading,
                        onCheckedChange = {
                            isChecked = it
                            onDebugKeystoreChecked(it)
                        },
                    )

                    Text(
                        modifier = Modifier.padding(start = MarginPaddingSizeSmall),
                        text = stringResource(Res.string.use_debug_keystore),
                        softWrap = true
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
    var passwordVisible by remember { mutableStateOf(false) }
    var keyPasswordVisible by remember { mutableStateOf(false) }

    FormCard(
        modifier = modifier.fillMaxWidth(),
        title = stringResource(Res.string.keystore),
        isActionButtonEnabled = !isLoading
    ) {
        val inputModifier = Modifier
            .fillMaxWidth()
            .padding(top = MarginPaddingSizeSmall)

        FilePickerTextField(
            modifier = inputModifier.padding(top = MarginPaddingSizeMedium),
            enabled = !isLoading,
            onFileResult = onKeystoreFileResult,
            label = stringResource(Res.string.keystore_path),
            fileType = keystoreInputType,
            pickerTitle = stringResource(Res.string.keystore)
        )

        OutlinedTextField(
            modifier = inputModifier,
            value = passwordText,
            enabled = !isLoading,
            onValueChange = { text ->
                onPasswordFieldChange(text)
            },
            label = { Text(stringResource(Res.string.keystore_password)) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) stringResource(Res.string.hide_password) else stringResource(Res.string.show_password)
                    )
                }
            }
        )

        OutlinedTextField(
            modifier = inputModifier,
            value = aliasText,
            enabled = !isLoading,
            onValueChange = { text ->
                onAliasFieldChange(text)
            },
            label = { Text(stringResource(Res.string.alias)) }
        )

        OutlinedTextField(
            modifier = inputModifier,
            value = keyPasswordText,
            enabled = !isLoading,
            onValueChange = { text ->
                onKeyPasswordFieldChange(text)
            },
            label = { Text(stringResource(Res.string.key_password)) },
            visualTransformation = if (keyPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = { keyPasswordVisible = !keyPasswordVisible },
                    enabled = !isLoading
                ) {
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
        text = "Universal APK",
        isSelected = true
    ),
    RadioItem(
        text = "APKS",
        isSelected = false
    )
)
