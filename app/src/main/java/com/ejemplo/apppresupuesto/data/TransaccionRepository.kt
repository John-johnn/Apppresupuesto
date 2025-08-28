package com.ejemplo.apppresupuesto.data

import kotlinx.coroutines.flow.Flow

class TransaccionRepository(private val transaccionDao: TransaccionDao) {

    val allTransactions: Flow<List<Transaccion>> = transaccionDao.getAll()

    suspend fun insert(transaccion: Transaccion) {
        transaccionDao.insert(transaccion)
    }

    suspend fun delete(transaccion: Transaccion) {
        transaccionDao.delete(transaccion)
    }
}
