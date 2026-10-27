package com.myciclo.app.contenido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// estados posibles de la pantalla
sealed class ContenidoEstado {
    object Cargando : ContenidoEstado()
    data class Listo(val guias: List<Guia>, val sinConexion: Boolean) : ContenidoEstado()
}

// api es la fuente remota y repositorioRemoto decide si es supabase o el spring boot
// local son las guias que estan escritas en el codigo, se usan si la remota falla
class ContenidoViewModel(
    private val api: ContenidoRepository = repositorioRemoto(),
    private val local: ContenidoRepository = ContenidoLocalRepository()
) : ViewModel() {

    private val _estado = MutableStateFlow<ContenidoEstado>(ContenidoEstado.Cargando)
    val estado: StateFlow<ContenidoEstado> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _estado.value = ContenidoEstado.Cargando
        viewModelScope.launch {
            try {
                _estado.value = ContenidoEstado.Listo(api.obtenerGuias(), sinConexion = false)
            } catch (e: Exception) {
                // si falla la api mostramos las guias locales
                _estado.value = ContenidoEstado.Listo(local.obtenerGuias(), sinConexion = true)
            }
        }
    }
}
