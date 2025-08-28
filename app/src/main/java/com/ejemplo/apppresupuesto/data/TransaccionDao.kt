package com.ejemplo.apppresupuesto.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaccionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaccion: Transaccion)

    @Delete
    suspend fun delete(transaccion: Transaccion)

    @Query("SELECT * FROM transacciones ORDER BY fecha DESC")
    fun getAll(): Flow<List<Transaccion>>
}
