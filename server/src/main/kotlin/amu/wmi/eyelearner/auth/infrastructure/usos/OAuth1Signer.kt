package amu.wmi.eyelearner.auth.infrastructure.usos

import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object OAuth1Signer {

    fun percentEncode(value: String): String {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name())
            .replace("+", "%20")
            .replace("*", "%2A")
            .replace("%7E", "~")
    }

    fun normalizeUrl(rawUrl: String): String {
        val uri = URI(rawUrl)
        val scheme = uri.scheme.lowercase()
        val host = uri.host.lowercase()
        val port = uri.port
        val portPart = if (port == -1 || (scheme == "http" && port == 80) || (scheme == "https" && port == 443)) {
            ""
        } else {
            ":$port"
        }
        val path = uri.path ?: ""
        return "$scheme://$host$portPart$path"
    }

    fun normalizeParameters(params: Map<String, String>): String {
        return params.entries
            .map { (k, v) -> percentEncode(k) to percentEncode(v) }
            .sortedWith(compareBy({ it.first }, { it.second }))
            .joinToString("&") { "${it.first}=${it.second}" }
    }

    fun buildSignatureBaseString(
        httpMethod: String,
        url: String,
        params: Map<String, String>
    ): String {
        val normalizedUrl = normalizeUrl(url)
        val normalizedParams = normalizeParameters(params)
        return "${httpMethod.uppercase()}&${percentEncode(normalizedUrl)}&${percentEncode(normalizedParams)}"
    }

    fun calculateSignature(
        baseString: String,
        consumerSecret: String,
        tokenSecret: String? = null
    ): String {
        val key = "${percentEncode(consumerSecret)}&${percentEncode(tokenSecret ?: "")}"
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), "HmacSHA1"))
        val rawHmac = mac.doFinal(baseString.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(rawHmac)
    }

    fun generateNonce(): String = UUID.randomUUID().toString().replace("-", "")

    fun currentTimestamp(): String = (System.currentTimeMillis() / 1000).toString()

    fun buildAuthorizationHeader(
        oauthParams: Map<String, String>
    ): String {
        val items = oauthParams.entries.joinToString(", ") { (k, v) ->
            "${percentEncode(k)}=\"${percentEncode(v)}\""
        }
        return "OAuth $items"
    }

    fun parseFormUrlEncoded(body: String): Map<String, String> {
        if (body.isBlank()) return emptyMap()
        return body.split("&").mapNotNull { pair ->
            val idx = pair.indexOf('=')
            if (idx == -1) null
            else {
                val key = pair.substring(0, idx)
                val value = pair.substring(idx + 1)
                java.net.URLDecoder.decode(key, StandardCharsets.UTF_8.name()) to
                        java.net.URLDecoder.decode(value, StandardCharsets.UTF_8.name())
            }
        }.toMap()
    }
}
