package com.ejemplo.apppresupuesto

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ejemplo.apppresupuesto.services.ExportService
import com.ejemplo.apppresupuesto.ui.PresupuestoScreen
import com.ejemplo.apppresupuesto.ui.theme.AppPresupuestoTheme
import com.ejemplo.apppresupuesto.ui.viewmodel.PresupuestoViewModel
import com.ejemplo.apppresupuesto.ui.viewmodel.PresupuestoViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: PresupuestoViewModel by viewModels {
        PresupuestoViewModelFactory(
            (application as PresupuestoApp).repository,
            ExportService()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppPresupuestoTheme {
                Surface(
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsState()
                    val context = LocalContext.current

                    LaunchedEffect(Unit) {
                        viewModel.events.collectLatest { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    PresupuestoScreen(
                        uiState = uiState,
                        onSave = viewModel::addTransaction,
                        onDelete = viewModel::deleteTransaction,
                        onExportToExcel = { viewModel.exportToExcel(context) },
                        onExportToWord = { viewModel.exportToWord(context) }
                    )
                }
            }
        }
    }
}