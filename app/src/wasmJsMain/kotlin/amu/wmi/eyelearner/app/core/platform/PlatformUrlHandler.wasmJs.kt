package amu.wmi.eyelearner.app.core.platform

import kotlinx.browser.window

actual fun openUrl(url: String) {
    window.location.href = url
}

actual fun getCurrentUrl(): String {
    return window.location.origin
}

actual fun getAuthCallbackParams(): Pair<String?, String?> {
    val search = window.location.search.ifBlank {
        val href = window.location.href
        if (href.contains("?")) "?" + href.substringAfter("?").substringBefore("#") else ""
    }
    if (search.isBlank()) return Pair(null, null)
    val params = search.removePrefix("?").split("&").associate {
        val parts = it.split("=")
        val key = parts.getOrNull(0) ?: ""
        val value = parts.getOrNull(1) ?: ""
        key to value
    }
    val token = params["oauth_token"]?.takeIf { it.isNotBlank() }
    val verifier = params["oauth_verifier"]?.takeIf { it.isNotBlank() }
    return Pair(token, verifier)
}

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
actual fun clearAuthCallbackParams() {
    try {
        window.history.replaceState(null, "", window.location.origin)
    } catch (_: Exception) {}
}
