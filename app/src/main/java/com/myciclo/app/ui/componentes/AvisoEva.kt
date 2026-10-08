package com.myciclo.app.ui.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// aviso que pide el caso (punto 5.1), lo usamos en varias pantallas
@Composable
fun AvisoEva() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "EVA es una herramienta de autoconocimiento y educación. " +
                "No es una evaluación clínica, tratamiento ni método anticonceptivo.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp)
        )
    }
}
