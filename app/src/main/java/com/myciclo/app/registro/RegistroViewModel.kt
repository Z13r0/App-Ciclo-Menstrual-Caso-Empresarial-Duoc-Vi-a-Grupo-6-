package com.myciclo.app.registro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myciclo.app.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// usamos AndroidViewModel porque necesitamos el context para abrir la base de datos
class RegistroViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = RegistroRepository(AppDatabase.obtener(app).registroDao())

    // mensaje que se muestra despues de guardar
    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    fun guardar(fecha: String, inicioPeriodo: String, nivel: Nivel, notas: String) {
        viewModelScope.launch {
            repository.guardar(
                RegistroEntity(
                    fecha = fecha,
                    inicioPeriodo = inicioPeriodo,
                    nivel = nivel.name,
                    notas = notas
                )
            )
            _mensaje.value = "Registro guardado"
        }
    }
}
