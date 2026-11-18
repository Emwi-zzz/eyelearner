package amu.wmi.eyelearner.infrastructure.usos

import amu.wmi.eyelearner.config.UsosProperties
import amu.wmi.eyelearner.infrastructure.security.OAuth1Signer
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class UsosOAuthClientImpl @Autowired constructor(
    private val usosProperties: UsosProperties,
    webClientBuilder: WebClient.Builder = WebClient.builder()
) : UsosOAuthClient {

    private val webClient = webClientBuilder.build()
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getRequestToken(callbackUrl: String, scopes: String?): UsosRequestToken {
        val requestUrl = "${usosProperties.baseUrl}/services/oauth/request_token"

        val params = mutableMapOf(
            "oauth_callback" to callbackUrl,
            "oauth_consumer_key" to usosProperties.consumerKey,
            "oauth_nonce" to OAuth1Signer.generateNonce(),
            "oauth_signature_method" to "HMAC-SHA1",
            "oauth_timestamp" to OAuth1Signer.currentTimestamp(),
            "oauth_version" to "1.0"
        )
        if (!scopes.isNullOrBlank()) {
            params["scopes"] = scopes
        }

        val baseString = OAuth1Signer.buildSignatureBaseString("POST", requestUrl, params)
        val signature = OAuth1Signer.calculateSignature(baseString, usosProperties.consumerSecret)
        params["oauth_signature"] = signature

        val authHeader = OAuth1Signer.buildAuthorizationHeader(params)

        try {
            val responseBody = webClient.post()
                .uri(requestUrl)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .bodyToMono(String::class.java)
                .awaitSingle()

            val parsed = OAuth1Signer.parseFormUrlEncoded(responseBody)
            val oauthToken = parsed["oauth_token"]
                ?: throw UsosApiException("Missing oauth_token in request_token response: $responseBody")
            val oauthTokenSecret = parsed["oauth_token_secret"]
                ?: throw UsosApiException("Missing oauth_token_secret in request_token response: $responseBody")
            val callbackConfirmed = parsed["oauth_callback_confirmed"]?.toBoolean() ?: true

            return UsosRequestToken(
                oauthToken = oauthToken,
                oauthTokenSecret = oauthTokenSecret,
                callbackConfirmed = callbackConfirmed
            )
        } catch (ex: WebClientResponseException) {
            throw UsosApiException("USOS request_token failed with HTTP ${ex.statusCode.value()}: ${ex.responseBodyAsString}", ex.statusCode.value(), ex)
        } catch (ex: Exception) {
            if (ex is UsosApiException) throw ex
            throw UsosApiException("Failed to communicate with USOS API: ${ex.message}", cause = ex)
        }
    }

    override suspend fun getAccessToken(
        oauthToken: String,
        oauthVerifier: String,
        requestTokenSecret: String
    ): UsosAccessToken {
        val requestUrl = "${usosProperties.baseUrl}/services/oauth/access_token"

        val params = mutableMapOf(
            "oauth_consumer_key" to usosProperties.consumerKey,
            "oauth_nonce" to OAuth1Signer.generateNonce(),
            "oauth_signature_method" to "HMAC-SHA1",
            "oauth_timestamp" to OAuth1Signer.currentTimestamp(),
            "oauth_token" to oauthToken,
            "oauth_verifier" to oauthVerifier,
            "oauth_version" to "1.0"
        )

        val baseString = OAuth1Signer.buildSignatureBaseString("POST", requestUrl, params)
        val signature = OAuth1Signer.calculateSignature(baseString, usosProperties.consumerSecret, requestTokenSecret)
        params["oauth_signature"] = signature

        val authHeader = OAuth1Signer.buildAuthorizationHeader(params)

        try {
            val responseBody = webClient.post()
                .uri(requestUrl)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .bodyToMono(String::class.java)
                .awaitSingle()

            val parsed = OAuth1Signer.parseFormUrlEncoded(responseBody)
            val accessToken = parsed["oauth_token"]
                ?: throw UsosApiException("Missing oauth_token in access_token response: $responseBody")
            val accessTokenSecret = parsed["oauth_token_secret"]
                ?: throw UsosApiException("Missing oauth_token_secret in access_token response: $responseBody")

            return UsosAccessToken(
                oauthToken = accessToken,
                oauthTokenSecret = accessTokenSecret
            )
        } catch (ex: WebClientResponseException) {
            throw UsosApiException("USOS access_token failed with HTTP ${ex.statusCode.value()}: ${ex.responseBodyAsString}", ex.statusCode.value(), ex)
        } catch (ex: Exception) {
            if (ex is UsosApiException) throw ex
            throw UsosApiException("Failed to communicate with USOS API: ${ex.message}", cause = ex)
        }
    }

    override suspend fun getUserProfile(
        accessToken: String,
        accessTokenSecret: String
    ): UsosUserProfile {
        val requestUrl = "${usosProperties.baseUrl}/services/users/user"
        val fields = "id|first_name|last_name"

        val params = mutableMapOf(
            "fields" to fields,
            "oauth_consumer_key" to usosProperties.consumerKey,
            "oauth_nonce" to OAuth1Signer.generateNonce(),
            "oauth_signature_method" to "HMAC-SHA1",
            "oauth_timestamp" to OAuth1Signer.currentTimestamp(),
            "oauth_token" to accessToken,
            "oauth_version" to "1.0"
        )

        val baseString = OAuth1Signer.buildSignatureBaseString("GET", requestUrl, params)
        val signature = OAuth1Signer.calculateSignature(baseString, usosProperties.consumerSecret, accessTokenSecret)
        params["oauth_signature"] = signature

        val authHeader = OAuth1Signer.buildAuthorizationHeader(params)

        try {
            val responseBody = webClient.get()
                .uri("$requestUrl?fields={fields}", fields)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String::class.java)
                .awaitSingle()

            return json.decodeFromString<UsosUserProfile>(responseBody)
        } catch (ex: WebClientResponseException) {
            throw UsosApiException("USOS user profile failed with HTTP ${ex.statusCode.value()}: ${ex.responseBodyAsString}", ex.statusCode.value(), ex)
        } catch (ex: Exception) {
            if (ex is UsosApiException) throw ex
            throw UsosApiException("Failed to fetch user profile from USOS: ${ex.message}", cause = ex)
        }
    }
}
