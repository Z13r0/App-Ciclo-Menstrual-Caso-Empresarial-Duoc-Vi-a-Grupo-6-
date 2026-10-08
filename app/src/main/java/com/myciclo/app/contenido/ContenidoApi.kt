package com.myciclo.app.contenido

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// endpoints de la api, solo contenido educativo (nunca datos del diario)
interface ContenidoApi {
    @GET("guias")
    suspend fun obtenerGuias(): List<Guia>
}

object RetrofitClient {
    // 10.0.2.2 es el localhost del pc visto desde el emulador
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    val api: ContenidoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ContenidoApi::class.java)
    }
}
