package com.myciclo.app.contenido

// datos del proyecto de supabase
// supabase arma la api rest sola a partir de la tabla, asi no hay que tener
// el spring boot corriendo en el pc para que aparezcan las guias
object SupabaseConfig {

    // la url del proyecto, termina en /rest/v1/ porque retrofit la necesita con la barra
    // queda parecida a: https://abcdefghijk.supabase.co/rest/v1/
    const val URL = ""

    // la llave anon, que es la publica y es la que va en apps de celular
    // la service_role NO se pone aca nunca, esa deja escribir toda la base
    const val ANON_KEY = ""

    // mientras falte cualquiera de los dos datos la app sigue usando el spring boot
    // asi el que no tenga las llaves igual puede trabajar sin que se le caiga nada
    val configurado: Boolean
        get() = URL.isNotBlank() && ANON_KEY.isNotBlank()
}
