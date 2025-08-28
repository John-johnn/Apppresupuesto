package com.ejemplo.apppresupuesto

import android.app.Application
import androidx.room.Room
import com.ejemplo.apppresupuesto.data.AppDatabase
import com.ejemplo.apppresupuesto.data.TransaccionRepository

class PresupuestoApp : Application() {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "presupuesto-db"
        ).build()
    }

    val repository: TransaccionRepository by lazy {
        TransaccionRepository(database.transaccionDao())
    }
}
