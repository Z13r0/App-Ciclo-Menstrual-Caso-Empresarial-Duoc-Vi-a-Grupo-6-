package com.myciclo.app.contenido

// interfaz para no amarrar la pantalla a una sola fuente de datos
// hoy hay dos: la api (retrofit) y una lista local de respaldo
// si el profe deja usar firebase se agrega otra clase que implemente esto
interface ContenidoRepository {
    suspend fun obtenerGuias(): List<Guia>
}

// guias escritas a mano para que la app funcione sin internet o sin backend
class ContenidoLocalRepository : ContenidoRepository {
    override suspend fun obtenerGuias(): List<Guia> = listOf(
        Guia(
            1, "Cómo usar EVA en 4 pasos",
            "1. Tomar: deja una pequeña muestra de saliva en el portaobjeto.\n" +
                "2. Secar: espera que se seque por completo (aprox. una hora).\n" +
                "3. Observar: enciende EVA y mira el patrón con el aumento 50x.\n" +
                "4. Registrar: anota el resultado en la app.",
            "Manual"
        ),
        Guia(
            2, "¿Qué significa el patrón de helecho?",
            "Antes de la ovulación suben los estrógenos y la saliva seca puede formar " +
                "cristales parecidos a hojas de helecho. El primer helecho marca el inicio " +
                "de la ventana fértil, que dura los 8 días siguientes.",
            "Educación"
        ),
        Guia(
            3, "¿EVA es un método anticonceptivo?",
            "No. EVA es una herramienta de autoconocimiento y educación. No reemplaza " +
                "una evaluación clínica.",
            "Preguntas frecuentes"
        )
    )
}

// version que pide las guias a la api de spring boot
class ContenidoApiRepository : ContenidoRepository {
    override suspend fun obtenerGuias(): List<Guia> = RetrofitClient.api.obtenerGuias()
}
