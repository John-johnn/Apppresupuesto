package com.ejemplo.apppresupuesto.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(tableName = "transacciones")
data class Transaccion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val descripcion: String,
    val monto: Double,
    val fecha: LocalDate,
    val tipo: TransactionType
)

enum class TransactionType {
    INGRESO,
    EGRESO
}
