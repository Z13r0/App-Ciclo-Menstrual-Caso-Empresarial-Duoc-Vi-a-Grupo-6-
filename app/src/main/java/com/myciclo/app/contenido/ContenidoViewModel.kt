package com.myciclo.app.contenido

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private data class FiltroContenido(
    val query: String,
    val categoria: String?,
    val favoritas: Set<Int>,
    val soloFavs: Boolean
)

// estados posibles de la pantalla
sealed class ContenidoEstado {
    object Cargando : ContenidoEstado()
    data class Listo(
        val guias: List<Guia>,
        val sinConexion: Boolean
    ) : ContenidoEstado()
}

// api es la fuente remota y repositorioRemoto decide si es supabase o el spring boot
// local son las guias que estan escritas en el codigo, se usan si la remota falla
class ContenidoViewModel @JvmOverloads constructor(
    app: Application,
    private val api: ContenidoRepository = repositorioRemoto(),
    private val local: ContenidoRepository = ContenidoLocalRepository()
) : AndroidViewModel(app) {

    private val prefs = FavoritasPreferences(app)

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _categoriaFiltro = MutableStateFlow<String?>(null)
    val categoriaFiltro: StateFlow<String?> = _categoriaFiltro.asStateFlow()

    private val _favoritasIds = MutableStateFlow<Set<Int>>(prefs.obtenerFavoritas())
    val favoritasIds: StateFlow<Set<Int>> = _favoritasIds.asStateFlow()

    private val _soloFavoritas = MutableStateFlow(false)
    val soloFavoritas: StateFlow<Boolean> = _soloFavoritas.asStateFlow()

    private val _guiasBase = MutableStateFlow<List<Guia>>(emptyList())
    private val _cargando = MutableStateFlow(true)
    private val _sinConexion = MutableStateFlow(false)

    private val filtroFlow = combine(
        _busqueda,
        _categoriaFiltro,
        _favoritasIds,
        _soloFavoritas
    ) { query, cat, favs, soloFavs ->
        FiltroContenido(query, cat, favs, soloFavs)
    }

    // Estado expuesto combinando en tiempo real la carga base con la búsqueda, categorías y favoritas
    val estado: StateFlow<ContenidoEstado> = combine(
        _cargando,
        _guiasBase,
        _sinConexion,
        filtroFlow
    ) { cargando, guias, sinConexion, filtro ->
        if (cargando) {
            ContenidoEstado.Cargando
        } else {
            val filtradas = guias.filter { g ->
                val coincideTexto = filtro.query.isBlank() ||
                    g.titulo.contains(filtro.query, ignoreCase = true) ||
                    g.contenido.contains(filtro.query, ignoreCase = true)
                val coincideCat = filtro.categoria == null || g.categoria.equals(filtro.categoria, ignoreCase = true)
                val coincideFav = !filtro.soloFavs || filtro.favoritas.contains(g.id)
                coincideTexto && coincideCat && coincideFav
            }
            ContenidoEstado.Listo(
                guias = filtradas,
                sinConexion = sinConexion
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ContenidoEstado.Cargando
    )

    init {
        cargar()
    }

    fun actualizarBusqueda(query: String) {
        _busqueda.value = query
    }

    fun actualizarCategoria(categoria: String?) {
        _categoriaFiltro.value = categoria
    }

    fun toggleFavorita(guiaId: Int) {
        val actuales = _favoritasIds.value
        val nuevas = if (actuales.contains(guiaId)) actuales - guiaId else actuales + guiaId
        _favoritasIds.value = nuevas
        prefs.guardarFavoritas(nuevas)
    }

    fun toggleSoloFavoritas() {
        _soloFavoritas.value = !_soloFavoritas.value
    }

    fun cargar() {
        _cargando.value = true
        viewModelScope.launch {
            try {
                _guiasBase.value = api.obtenerGuias()
                _sinConexion.value = false
            } catch (_: Exception) {
                // si falla la api mostramos las guias locales
                _guiasBase.value = local.obtenerGuias()
                _sinConexion.value = true
            } finally {
                _cargando.value = false
            }
        }
    }
}
