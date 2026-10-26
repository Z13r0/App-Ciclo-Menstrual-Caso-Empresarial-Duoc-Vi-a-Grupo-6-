package com.myciclo.app.contenido

// interfaz para no amarrar la pantalla a una sola fuente de datos
// hoy hay dos: la api (retrofit) y una lista local de respaldo
interface ContenidoRepository {
    suspend fun obtenerGuias(): List<Guia>
}

// Guías locales de respaldo (mismas que el backend).
// Así la app se ve completa aunque no haya internet ni servidor.
class ContenidoLocalRepository : ContenidoRepository {
    override suspend fun obtenerGuias(): List<Guia> = listOf(
        Guia(
            1,
            "Cómo usar EVA en 4 pasos",
            "1. Tomar: deja una pequeña muestra de saliva en el portaobjeto.\n" +
                    "2. Secar: espera que se seque por completo (aprox. una hora).\n" +
                    "3. Observar: enciende EVA y mira el patrón con el aumento 50x.\n" +
                    "4. Registrar: anota el resultado en la app myCiclo.",
            "Manual"
        ),
        Guia(
            2,
            "¿Qué significa el patrón de helecho?",
            "Antes de la ovulación suben los estrógenos y la saliva seca puede formar " +
                    "cristales parecidos a hojas de helecho. El primer helecho marca el inicio " +
                    "de la ventana fértil, que dura los 8 días siguientes.",
            "Educación"
        ),
        Guia(
            3,
            "¿EVA es un método anticonceptivo?",
            "No. EVA es una herramienta de autoconocimiento y educación. No reemplaza " +
                    "una evaluación clínica ni un método anticonceptivo.",
            "Preguntas frecuentes"
        ),
        Guia(
            4,
            "Duración de la ventana fértil",
            "La ventana fértil en EVA comienza el día del primer patrón de helecho " +
                    "y se mantiene durante los 8 días siguientes. No indica por sí sola que " +
                    "la ovulación ya ocurrió.",
            "Educación"
        ),
        Guia(
            5,
            "El estrógeno y la saliva",
            "Durante la fase folicular el estrógeno eleva la concentración de sales " +
                    "en la saliva. Al secarse puede formar cristales tipo helecho. EVA observa " +
                    "ese efecto visual, no mide hormonas directamente.",
            "Educación"
        ),
        Guia(
            6,
            "Cuidado del dispositivo EVA",
            "Limpia el lente y el portaobjeto con un paño seco. No uses agua. " +
                    "Carga con USB-C. Es reutilizable y está pensado para uso continuo.",
            "Manual"
        ),
        Guia(
            7,
            "Privacidad de tus datos",
            "El diario de observaciones se guarda solo en tu celular. " +
                    "La app no envía tus registros personales a ningún servidor. " +
                    "Solo se descarga contenido educativo (guías).",
            "Preguntas frecuentes"
        )
    )
}

// Versión que pide las guías a la API de Spring Boot
class ContenidoApiRepository : ContenidoRepository {
    override suspend fun obtenerGuias(): List<Guia> = RetrofitClient.api.obtenerGuias()
}