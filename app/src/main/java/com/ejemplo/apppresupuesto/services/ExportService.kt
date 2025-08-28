package com.ejemplo.apppresupuesto.services

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.ejemplo.apppresupuesto.data.Transaccion
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.OutputStream

class ExportService {

    fun exportToWord(context: Context, transacciones: List<Transaccion>, fileName: String): Boolean {
        val document = XWPFDocument()

        // Title
        val title = document.createParagraph()
        title.createRun().apply {
            setText("Reporte de Transacciones")
            isBold = true
            fontSize = 16
        }

        // Table
        val table = document.createTable()
        // Header
        val headerRow = table.getRow(0)
        headerRow.getCell(0).text = "ID"
        headerRow.addNewTableCell().text = "Descripción"
        headerRow.addNewTableCell().text = "Monto"
        headerRow.addNewTableCell().text = "Fecha"
        headerRow.addNewTableCell().text = "Tipo"

        // Data
        transacciones.forEach { transaccion ->
            val row = table.createRow()
            row.getCell(0).text = transaccion.id.toString()
            row.getCell(1).text = transaccion.descripcion
            row.getCell(2).text = transaccion.monto.toString()
            row.getCell(3).text = transaccion.fecha.toString()
            row.getCell(4).text = transaccion.tipo.name
        }

        return try {
            saveDocument(context, document, "$fileName.docx")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun exportToExcel(context: Context, transacciones: List<Transaccion>, fileName: String): Boolean {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Transacciones")

        // Header row
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("ID")
        headerRow.createCell(1).setCellValue("Descripción")
        headerRow.createCell(2).setCellValue("Monto")
        headerRow.createCell(3).setCellValue("Fecha")
        headerRow.createCell(4).setCellValue("Tipo")

        // Data rows
        transacciones.forEachIndexed { index, transaccion ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(transaccion.id.toDouble())
            row.createCell(1).setCellValue(transaccion.descripcion)
            row.createCell(2).setCellValue(transaccion.monto)
            row.createCell(3).setCellValue(transaccion.fecha.toString())
            row.createCell(4).setCellValue(transaccion.tipo.name)
        }

        return try {
            saveWorkbook(context, workbook, "$fileName.xlsx")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun saveWorkbook(context: Context, workbook: XSSFWorkbook, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/PresupuestoApp")
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                workbook.write(outputStream)
            }
        } ?: throw Exception("Failed to create new MediaStore record.")
    }

    private fun saveDocument(context: Context, document: XWPFDocument, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/PresupuestoApp")
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                document.write(outputStream)
            }
        } ?: throw Exception("Failed to create new MediaStore record.")
    }
}
