package com.gdcj.voluntariadoiiap.data.remote

import com.gdcj.voluntariadoiiap.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Obtener el token del SessionManager
        val token = sessionManager.fetchAuthToken()

        // Si el token existe, añadirlo al header
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
