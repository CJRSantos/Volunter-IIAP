package com.gdcj.voluntariadoiiap.data.remote

import android.content.Context
import android.util.Log
import com.gdcj.voluntariadoiiap.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

object RetrofitClient {

    private const val BASE_URL = "https://api-voluntariado.iiap.gob.pe/"
    private lateinit var sessionManager: SessionManager

    fun init(context: Context) {
        sessionManager = SessionManager(context)
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            // 1. Primero añadimos el interceptor de Auth
            .addInterceptor { chain ->
                val token = sessionManager.fetchAuthToken()
                val requestBuilder = chain.request().newBuilder()

                if (!token.isNullOrEmpty()) {
                    val finalToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                    requestBuilder.header("Authorization", finalToken)
                    Log.d("TOKEN_DEBUG", "Token inyectado: ${finalToken.take(20)}...")
                } else {
                    Log.d("TOKEN_DEBUG", "Token NO inyectado (vacío)")
                }

                chain.proceed(requestBuilder.build())
            }
            // 2. Al final el logging para que capture el request ya con el token
            .addInterceptor(logging)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getUnsafeOkHttpClient())
            .build()
    }

    val authService: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    val userService: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
    val studyService: StudyApiService by lazy { retrofit.create(StudyApiService::class.java) }
    val experienceService: ExperienceApiService by lazy { retrofit.create(ExperienceApiService::class.java) }
    val roleService: RoleApiService by lazy { retrofit.create(RoleApiService::class.java) }
    val areaService: AreaApiService by lazy { retrofit.create(AreaApiService::class.java) }
    val projectService: ProjectApiService by lazy { retrofit.create(ProjectApiService::class.java) }
    val skillService: SkillApiService by lazy { retrofit.create(SkillApiService::class.java) }
    val applicationStatusService: ApplicationStatusApiService by lazy { retrofit.create(ApplicationStatusApiService::class.java) }
    val applicationService: ApplicationApiService by lazy { retrofit.create(ApplicationApiService::class.java) }
}
