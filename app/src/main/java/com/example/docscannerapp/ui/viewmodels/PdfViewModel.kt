package com.example.docscannerapp.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docscannerapp.data.models.PdfEntity
import com.example.docscannerapp.data.repository.PdfRepository
import com.example.docscannerapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PdfViewModel(application: Application) : ViewModel() {
    var isSplashScreen by mutableStateOf(false)
    var showRenameDialog by mutableStateOf(false)
    var loadingDialog by mutableStateOf(false)

    private val pdfRepository = PdfRepository(application)
    private val _pdfStateFlow =
        MutableStateFlow<Resource<List<PdfEntity>>>(Resource.Idle)
    val pdfStateFlow : StateFlow<Resource<List<PdfEntity>>>
        get() = _pdfStateFlow

    var currentPdfEntity: PdfEntity? by mutableStateOf(null)
    private val _message : Channel<Resource<String>> =Channel()
    val message = _message.receiveAsFlow()

    init {
        viewModelScope.launch {
            delay(2000)
            isSplashScreen = false
        }
//        if dialog use
        viewModelScope.launch {
            pdfStateFlow.collect {
                when(it){
                    is Resource.Error -> {
                        loadingDialog = false
                    }
                    Resource.Idle -> {}
                    Resource.Loading -> {
                        loadingDialog = true
                    }
                    is Resource.Success -> {
                        loadingDialog = false
                    }
                }
            }
        }


        viewModelScope.launch(Dispatchers.IO) {
            _pdfStateFlow.emit(Resource.Loading)
            delay(2000)
            pdfRepository.getPdfList().catch {
                _pdfStateFlow.emit(Resource.Error(it.message ?:"Something went wrong"))

                it.printStackTrace()
            }.collect {
                _pdfStateFlow.emit(Resource.Success(it))
            }
        }
    }

    fun insertPdf(pdfEntity: PdfEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                _pdfStateFlow.emit(Resource.Loading)
                loadingDialog = true
                val result = pdfRepository.insertPdf(pdfEntity)
                if (result.toInt() != -1){
                    _message.send(Resource.Success("Inserted Pdf Successfully"))
                }else{
                    _message.send(Resource.Error("Something Went Wrong"))

                }
            }catch (e: Exception){
                e.printStackTrace()
                _message.send(Resource.Error(e.message ?: "Something Went Wrong"))

            }
        }
    }

    fun deletePdf(pdfEntity: PdfEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                _pdfStateFlow.emit(Resource.Loading)
                loadingDialog = true
                pdfRepository.deletePdf(pdfEntity)
                _message.send(Resource.Success("Deleted Pdf Successfully"))

            }catch (e: Exception){
                _message.send(Resource.Error(e.message ?: "Something Went Wrong"))

            }

        }
    }

    fun updatePdf(pdfEntity: PdfEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                _pdfStateFlow.emit(Resource.Loading)
                loadingDialog = true
                delay(2000)
                val result = pdfRepository.updatePdf(pdfEntity)
                _message.send(Resource.Success("Updated Pdf Successfully"))

            }catch (e: Exception){
                _message.send(Resource.Error(e.message ?: "Something Went Wrong"))

            }

        }
    }
}