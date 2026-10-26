package com.myciclo.app.registro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myciclo.app.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// usamos AndroidViewModel porque necesitamos el context para abrir la base de datos
class RegistroViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = RegistroRepository(AppDatabase.obtener(app).registroDao())

    // Historial completo ordenado por fecha descendente (el más reciente arriba)
    val registros: StateFlow<List<RegistroEntity>> = repository.registros
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    /**
     * Guarda o actualiza un registro.
     * Como la fecha es PrimaryKey y usamos OnConflictStrategy.REPLACE,
     * si ya existe un registro ese día se sobrescribe (edición).
     */
    fun guardar(
        fecha: String,
        inicioPeriodo: String,
        nivel: Nivel,
        notas: String,
        foto: String
    ) {
        viewModelScope.launch {
            repository.guardar(
                RegistroEntity(
                    fecha = fecha,
                    inicioPeriodo = inicioPeriodo,
                    nivel = nivel.name,
                    notas = notas,
                    foto = foto
                )
            )
            _mensaje.value = "Registro guardado"
        }
    }

    /** Borra el registro de un día concreto. */
    fun borrar(fecha: String) {
        viewModelScope.launch {
            repository.borrar(fecha)
            _mensaje.value = "Registro eliminado"
        }
    }

    /** Limpia el mensaje después de mostrarlo en la UI. */
    fun limpiarMensaje() {
        _mensaje.value = ""
    }
}