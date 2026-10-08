package com.myciclo.app.contenido

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myciclo.app.ui.componentes.AvisoEva

@Composable
fun ContenidoScreen(vm: ContenidoViewModel = viewModel()) {
    val estado by vm.estado.collectAsState()

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Aprende sobre EVA", style = MaterialTheme.typography.headlineSmall)

        when (val e = estado) {
            is ContenidoEstado.Cargando -> CircularProgressIndicator()
            is ContenidoEstado.Listo -> {
                if (e.sinConexion) {
                    Text("Sin conexión, mostrando guías guardadas", style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = { vm.cargar() }) { Text("Reintentar") }
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(e.guias) { guia -> TarjetaGuia(guia) }
                    item { AvisoEva() }
                }
            }
        }
    }
}

// tarjeta que se abre al tocarla para ver el contenido
@Composable
fun TarjetaGuia(guia: Guia) {
    var abierta by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth().clickable { abierta = !abierta }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(guia.categoria, style = MaterialTheme.typography.labelMedium)
            Text(guia.titulo, style = MaterialTheme.typography.titleMedium)
            if (abierta) {
                Text(guia.contenido, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
