package amu.wmi.eyelearner.auth.domain.usos.service

import amu.wmi.eyelearner.auth.domain.usos.model.UsosAccessToken
import amu.wmi.eyelearner.auth.domain.usos.model.UsosRequestToken
import amu.wmi.eyelearner.auth.domain.usos.model.UsosUserProfile

interface UsosOAuthClient {
    suspend fun getRequestToken(callbackUrl: String, scopes: String? = null): UsosRequestToken
    suspend fun getAccessToken(oauthToken: String, oauthVerifier: String, requestTokenSecret: String): UsosAccessToken
    suspend fun getUserProfile(accessToken: String, accessTokenSecret: String): UsosUserProfile
}
