package feature.nav.repository

import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import shared.settings.AppSettings

class NavRepository(
    private val appSettings: AppSettings
) {

    suspend fun getIsFirstAccess() = flow {
        emitAll(appSettings.isFirstAccess)
    }
}