package com.ejemplo.apppresupuesto.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate

import com.ejemplo.apppresupuesto.model.Transaction
import com.ejemplo.apppresupuesto.model.TransactionType
import com.ejemplo.apppresupuesto.model.AppDatabase

class TransactionDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: TransactionDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = db.transactionDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndRetrieveTransaction() = runBlocking {
        val transactionDate = LocalDate.of(2025, 8, 26)

        val transaction = Transaction(
            id = 0, // si Room autogenera, puedes omitir o dejar en 0
            amount = 1500.0,
            category = "Alimentación",
            description = "Compra en supermercado",
            date = transactionDate,
            type = TransactionType.EXPENSE
        )

        dao.insert(transaction)

        val result = dao.getAllTransactions()
        assertEquals(1, result.size)

        val retrieved = result[0]
        assertEquals(TransactionType.EXPENSE, retrieved.type)
        assertEquals(transactionDate, retrieved.date)
        assertEquals("Alimentación", retrieved.category)
        assertEquals("Compra en supermercado", retrieved.description)
        assertEquals(1500.0, retrieved.amount, 0.01)
    }
}