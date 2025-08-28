package com.ejemplo.apppresupuesto.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ejemplo.apppresupuesto.data.Transaccion
import com.ejemplo.apppresupuesto.data.TransaccionRepository
import com.ejemplo.apppresupuesto.data.TransactionType
import com.ejemplo.apppresupuesto.services.ExportService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PresupuestoUiState(
    val transacciones: List<Transaccion> = emptyList(),
    val totalIngresos: Double = 0.0,
    val totalEgresos: Double = 0.0,
    val balance: Double = 0.0
)

class PresupuestoViewModel(
    private val repository: TransaccionRepository,
    private val exportService: ExportService
) : ViewModel() {

    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    val uiState: StateFlow<PresupuestoUiState> = repository.allTransactions
        .map { transacciones ->
            val ingresos = transacciones.filter { it.tipo == TransactionType.INGRESO }.sumOf { it.monto }
            val egresos = transacciones.filter { it.tipo == TransactionType.EGRESO }.sumOf { it.monto }
            PresupuestoUiState(
                transacciones = transacciones,
                totalIngresos = ingresos,
                totalEgresos = egresos,
                balance = ingresos + egresos // Egresos are negative
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PresupuestoUiState()
        )

    fun addTransaction(transaccion: Transaccion) {
        viewModelScope.launch {
            repository.insert(transaccion)
        }
    }

    fun deleteTransaction(transaccion: Transaccion) {
        viewModelScope.launch {
            repository.delete(transaccion)
        }
    }

    fun exportToExcel(context: Context) {
        viewModelScope.launch {
            val success = exportService.exportToExcel(context, uiState.value.transacciones, "Presupuesto_Excel")
            if (success) {
                _events.emit("Exportado a Excel con éxito")
            } else {
                _events.emit("Error al exportar a Excel")
            }
        }
    }

    fun exportToWord(context: Context) {
        viewModelScope.launch {
            val success = exportService.exportToWord(context, uiState.value.transacciones, "Presupuesto_Word")
            if (success) {
                _events.emit("Exportado a Word con éxito")
            } else {
                _events.emit("Error al exportar a Word")
            }
        }
    }
}

class PresupuestoViewModelFactory(
    private val repository: TransaccionRepository,
    private val exportService: ExportService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PresupuestoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PresupuestoViewModel(repository, exportService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
