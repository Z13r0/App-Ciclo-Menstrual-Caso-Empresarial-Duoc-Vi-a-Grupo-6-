package com.myciclo.app.contenido

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// api que publica supabase sola sobre la tabla guias
// la ruta queda en /rest/v1/guias y los filtros van como parametros de la url
interface SupabaseApi {

    // supabase pide la llave en dos headers: apikey y Authorization
    // select dice que columnas traer y order con que columna ordenar
    @GET("guias")
    suspend fun obtenerGuias(
        @Header("apikey") apikey: String,
        @Header("Authorization") autorizacion: String,
        @Query("select") columnas: String,
        @Query("order") orden: String
    ): List<Guia>
}

object SupabaseClient {
    // lazy para que el cliente se arme recien cuando se use
    // si lo armaramos al inicio con la url vacia retrofit reclamaria al abrir la app
    val api: SupabaseApi by lazy {
        Retrofit.Builder()
            .baseUrl(SupabaseConfig.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApi::class.java)
    }
}

// tercera fuente de guias, usa la misma interfaz que las otras dos
// las columnas de la tabla se llaman igual que los campos de Guia, asi gson las calza solo
class ContenidoSupabaseRepository : ContenidoRepository {
    override suspend fun obtenerGuias(): List<Guia> {
        // si falta la url o la llave tiramos el error a proposito
        // el viewmodel ya atrapa las excepciones y muestra las guias locales
        if (!SupabaseConfig.configurado) {
            throw IllegalStateException("falta la url o la llave en SupabaseConfig")
        }
        return SupabaseClient.api.obtenerGuias(
            apikey = SupabaseConfig.ANON_KEY,
            autorizacion = "Bearer " + SupabaseConfig.ANON_KEY,
            columnas = "*",
            orden = "id"
        )
    }
}

// decide de donde bajar las guias sin que la pantalla tenga que saberlo:
// si supabase esta configurado va a supabase, si no al spring boot de siempre
fun repositorioRemoto(): ContenidoRepository =
    if (SupabaseConfig.configurado) ContenidoSupabaseRepository() else ContenidoApiRepository()
