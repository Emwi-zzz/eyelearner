package amu.wmi.eyelearner.app.core.platform

expect fun openUrl(url: String)
expect fun getCurrentUrl(): String
expect fun getAuthCallbackParams(): Pair<String?, String?>
expect fun clearAuthCallbackParams()
