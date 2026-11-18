package amu.wmi.eyelearner.infrastructure.security

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OAuth1SignerTest {

    @Test
    fun `percentEncode correctly encodes according to RFC 3986`() {
        assertEquals("abcXYZ019", OAuth1Signer.percentEncode("abcXYZ019"))
        assertEquals("-._~", OAuth1Signer.percentEncode("-._~"))
        assertEquals("%20", OAuth1Signer.percentEncode(" "))
        assertEquals("%2A", OAuth1Signer.percentEncode("*"))
        assertEquals("%2B", OAuth1Signer.percentEncode("+"))
        assertEquals("%3D", OAuth1Signer.percentEncode("="))
        assertEquals("%26", OAuth1Signer.percentEncode("&"))
        assertEquals("%25", OAuth1Signer.percentEncode("%"))
    }

    @Test
    fun `normalizeUrl strips default ports and normalizes case`() {
        assertEquals("http://example.com/api", OAuth1Signer.normalizeUrl("HTTP://EXAMPLE.COM:80/api"))
        assertEquals("https://example.com/api", OAuth1Signer.normalizeUrl("HTTPS://EXAMPLE.COM:443/api"))
        assertEquals("https://example.com:8443/api", OAuth1Signer.normalizeUrl("https://example.com:8443/api"))
    }

    @Test
    fun `normalizeParameters sorts by encoded key and value`() {
        val params = mapOf(
            "b" to "2",
            "a" to "1",
            "a_key" to "hello world",
            "c" to "*"
        )
        val normalized = OAuth1Signer.normalizeParameters(params)
        assertEquals("a=1&a_key=hello%20world&b=2&c=%2A", normalized)
    }

    @Test
    fun `calculateSignature produces correct HMAC-SHA1 signature`() {
        // RFC 5849 Section 3.4.1.2 test vector
        val baseString = "POST&http%3A%2F%2Fexample.com%2Frequest&a2%3Dr%2520b%26a3%3D2%2520q%26a3%3Da%26b5%3D%253D%25253D%26c%2540%3D%26c2%3D%26oauth_consumer_key%3D9djdj82h48djs9d2%26oauth_nonce%3D7d8f3e4a%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D137131201%26oauth_token%3Dkkk9d7dh3k39sjv7"
        val consumerSecret = "j49sk3j29djd"
        val tokenSecret = "dh893hdasih9"

        val signature = OAuth1Signer.calculateSignature(baseString, consumerSecret, tokenSecret)
        // RFC 5849 Errata corrected signature for POST
        assertEquals("r6/TJjbCOr97/+UU0NsvSne7s5g=", signature)
    }

    @Test
    fun `buildAuthorizationHeader formats correctly`() {
        val oauthParams = mapOf(
            "oauth_consumer_key" to "key 1",
            "oauth_signature" to "sig+1="
        )
        val header = OAuth1Signer.buildAuthorizationHeader(oauthParams)
        assertTrue(header.startsWith("OAuth "))
        assertTrue(header.contains("oauth_consumer_key=\"key%201\""))
        assertTrue(header.contains("oauth_signature=\"sig%2B1%3D\""))
    }

    @Test
    fun `parseFormUrlEncoded correctly parses query pairs`() {
        val body = "oauth_token=token123&oauth_token_secret=sec456&oauth_callback_confirmed=true"
        val parsed = OAuth1Signer.parseFormUrlEncoded(body)
        assertEquals("token123", parsed["oauth_token"])
        assertEquals("sec456", parsed["oauth_token_secret"])
        assertEquals("true", parsed["oauth_callback_confirmed"])
    }
}
