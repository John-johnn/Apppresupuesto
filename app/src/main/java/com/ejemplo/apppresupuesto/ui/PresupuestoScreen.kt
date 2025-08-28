package com.ejemplo.apppresupuesto.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ejemplo.apppresupuesto.data.Transaccion
import com.ejemplo.apppresupuesto.data.TransactionType
import com.ejemplo.apppresupuesto.ui.viewmodel.PresupuestoUiState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun PresupuestoScreen(
    uiState: PresupuestoUiState,
    onSave: (Transaccion) -> Unit,
    onDelete: (Transaccion) -> Unit,
    onExportToExcel: () -> Unit,
    onExportToWord: () -> Unit
) {
    var montoTexto by remember { mutableStateOf("") }
    var descripcionTexto by remember { mutableStateOf("") }
    var tipoTransaccion by remember { mutableStateOf(TransactionType.EGRESO) }
    val esValido = montoTexto.toDoubleOrNull() != null && descripcionTexto.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Summary Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Ingresos: $${uiState.totalIngresos}", color = Color.Green)
                Text("Egresos: $${uiState.totalEgresos}", color = Color.Red)
                Text("Balance: $${uiState.balance}")
            }
        }

        // Input Section
        OutlinedTextField(
            value = descripcionTexto,
            onValueChange = { descripcionTexto = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = montoTexto,
            onValueChange = { nuevo ->
                if (nuevo.all { it.isDigit() || it == '.' }) {
                    montoTexto = nuevo
                }
            },
            label = { Text("Monto") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = tipoTransaccion == TransactionType.INGRESO,
                onClick = { tipoTransaccion = TransactionType.INGRESO }
            )
            Text("Ingreso")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = tipoTransaccion == TransactionType.EGRESO,
                onClick = { tipoTransaccion = TransactionType.EGRESO }
            )
            Text("Egreso")
        }

        Button(
            onClick = {
                val monto = montoTexto.toDoubleOrNull()
                if (monto != null) {
                    val transaccion = Transaccion(
                        descripcion = descripcionTexto,
                        monto = if (tipoTransaccion == TransactionType.INGRESO) monto else -monto,
                        fecha = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                        tipo = tipoTransaccion
                    )
                    onSave(transaccion)
                    montoTexto = ""
                    descripcionTexto = ""
                }
            },
            enabled = esValido,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onExportToExcel, modifier = Modifier.weight(1f)) {
                Text("Exportar a Excel")
            }
            Button(onClick = onExportToWord, modifier = Modifier.weight(1f)) {
                Text("Exportar a Word")
            }
        }

        Divider()

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.transacciones) { tx ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Descripción: ${tx.descripcion}", style = MaterialTheme.typography.bodyLarge)
                            Text("Monto: $${tx.monto}", style = MaterialTheme.typography.bodyLarge)
                            Text("Fecha: ${tx.fecha}", style = MaterialTheme.typography.bodySmall)
                            Text("Tipo: ${tx.tipo}", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { onDelete(tx) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            }
        }
    }
}
