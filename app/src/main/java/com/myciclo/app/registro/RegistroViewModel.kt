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

class RegistroViewModel @JvmOverloads constructor(
    app: Application,
    private val userId: String = ""
) : AndroidViewModel(app) {

    private val repository = RegistroRepository(
        AppDatabase.obtener(app).registroDao(),
        userId
    )

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _filtroNivel = MutableStateFlow<Nivel?>(null)
    val filtroNivel: StateFlow<Nivel?> = _filtroNivel.asStateFlow()

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

        if (fechaParsed.isAfter(java.time.LocalDate.now())) {
            _mensaje.value = "La fecha del registro no puede ser futura."
            return
        }
        if (inicioParsed.isAfter(fechaParsed)) {
            _mensaje.value = "El inicio del período no puede ser posterior al registro."
            return
        }
        if (notas.length > 1000) {
            _mensaje.value = "Las notas no pueden superar los 1000 caracteres."
            return
        }

        viewModelScope.launch {
            repository.guardar(
                RegistroEntity(
                    userId = userId,
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

    fun borrar(fechaInput: String) {
        val fechaIso = FechaUtils.parsear(fechaInput)?.let { FechaUtils.aFormatoIso(it) } ?: fechaInput
        viewModelScope.launch {
            repository.borrar(fechaIso)
            _mensaje.value = "Registro eliminado"
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = ""
    }
}
