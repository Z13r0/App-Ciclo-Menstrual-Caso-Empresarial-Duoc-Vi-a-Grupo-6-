package com.myciclo.app.registro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myciclo.app.data.AppDatabase
import com.myciclo.app.util.FechaUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// usamos AndroidViewModel porque necesitamos el context para abrir la base de datos
class RegistroViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = RegistroRepository(AppDatabase.obtener(app).registroDao())

    // Estado para filtro por texto de notas/fecha y nivel EVA
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _filtroNivel = MutableStateFlow<Nivel?>(null)
    val filtroNivel: StateFlow<Nivel?> = _filtroNivel.asStateFlow()

    // Historial completo filtrado en tiempo real
    val registros: StateFlow<List<RegistroEntity>> = combine(
        repository.registros,
        _busqueda,
        _filtroNivel
    ) { lista, query, nivel ->
        lista.filter { reg ->
            val coincideTexto = query.isBlank() ||
                reg.notas.contains(query, ignoreCase = true) ||
                reg.fecha.contains(query, ignoreCase = true)
            val coincideNivel = nivel == null || reg.nivel == nivel.name
            coincideTexto && coincideNivel
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    fun actualizarBusqueda(query: String) {
        _busqueda.value = query
    }

    fun actualizarFiltroNivel(nivel: Nivel?) {
        _filtroNivel.value = nivel
    }

    /**
     * Guarda o actualiza un registro convirtiendo primero las fechas a ISO estándar ("yyyy-MM-dd").
     * Como la fecha es PrimaryKey y usamos OnConflictStrategy.REPLACE,
     * si ya existe un registro ese día se sobrescribe (edición).
     */
    fun guardar(
        fechaInput: String,
        inicioPeriodoInput: String,
        nivel: Nivel,
        notas: String,
        foto: String
    ) {
        val fechaParsed = FechaUtils.parsear(fechaInput)
        val inicioParsed = FechaUtils.parsear(inicioPeriodoInput)

        if (fechaParsed == null || inicioParsed == null) {
            _mensaje.value = "Error: Una o ambas fechas no son válidas. Usa yyyy-MM-dd o dd/MM/yyyy."
            return
        }

        val fechaIso = FechaUtils.aFormatoIso(fechaParsed)
        val inicioIso = FechaUtils.aFormatoIso(inicioParsed)

        viewModelScope.launch {
            repository.guardar(
                RegistroEntity(
                    fecha = fechaIso,
                    inicioPeriodo = inicioIso,
                    nivel = nivel.name,
                    notas = notas,
                    foto = foto
                )
            )
            _mensaje.value = "Registro guardado"
        }
    }

    /** Borra el registro de un día concreto. */
    fun borrar(fechaInput: String) {
        val fechaIso = FechaUtils.parsear(fechaInput)?.let { FechaUtils.aFormatoIso(it) } ?: fechaInput
        viewModelScope.launch {
            repository.borrar(fechaIso)
            _mensaje.value = "Registro eliminado"
        }
    }

    /** Limpia el mensaje después de mostrarlo en la UI. */
    fun limpiarMensaje() {
        _mensaje.value = ""
    }
}
