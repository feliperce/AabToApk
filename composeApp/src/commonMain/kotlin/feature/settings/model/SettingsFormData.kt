package feature.settings.model

data class SettingsFormData(
    val outputApksPath: String = "",
    val buildToolsPath: String = "",
    val adbPath: String = ""
)

data class SettingsFormDataCallback(
    val onSaveButtonClick: () -> Unit
)