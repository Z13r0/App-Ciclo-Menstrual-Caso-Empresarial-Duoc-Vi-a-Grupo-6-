package com.myciclo.app.auth

import com.myciclo.app.BuildConfig
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

data class Credentials(val email: String, val password: String)

data class SignupRequest(
    val email: String,
    val password: String,
    val options: SignupOptions
)

data class SignupOptions(
    @SerializedName("email_redirect_to") val emailRedirectTo: String
)

data class AuthResponse(
    val access_token: String? = null,
    val refresh_token: String? = null,
    val expires_in: Long? = null,
    val user: AuthUser? = null,
    val msg: String? = null,
    val error_description: String? = null
)

data class AuthUser(
    val id: String? = null,
    val email: String? = null,
    val email_confirmed_at: String? = null
)

interface AuthApi {
    @Headers("Content-Type: application/json")
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apikey: String,
        @Body credentials: SignupRequest
    ): AuthResponse

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(
        @Header("apikey") apikey: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): AuthResponse

    @Headers("Content-Type: application/json")
    @POST("auth/v1/recover")
    suspend fun recover(
        @Header("apikey") apikey: String,
        @Body body: Map<String, String>
    ): AuthResponse

    @FormUrlEncoded
    @POST("auth/v1/token?grant_type=refresh_token")
    suspend fun refresh(
        @Header("apikey") apikey: String,
        @Field("refresh_token") refreshToken: String
    ): AuthResponse

    @POST("auth/v1/logout")
    suspend fun logout(
        @Header("apikey") apikey: String,
        @Header("Authorization") authorization: String
    )

    @Headers("Content-Type: application/json")
    @PUT("auth/v1/user")
    suspend fun updateUser(
        @Header("apikey") apikey: String,
        @Header("Authorization") authorization: String,
        @Body body: Map<String, String>
    ): AuthResponse
}

object AuthConfig {
    val enabled: Boolean
        get() = BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_ANON_KEY.isNotBlank()

    val redirectUrl: String = "myciclo://auth-callback"
}

object AuthClient {
    val api: AuthApi by lazy {
        require(AuthConfig.enabled) { "Supabase Auth no está configurado" }
        Retrofit.Builder()
            .baseUrl(BuildConfig.SUPABASE_URL.trimEnd('/') + "/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}