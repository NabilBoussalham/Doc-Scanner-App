package com.example.docscannerapp.data.repository

import android.app.Application
import com.example.docscannerapp.data.local.PdfDataBase
import com.example.docscannerapp.data.local.dao.pdfDao
import com.example.docscannerapp.data.models.PdfEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class PdfRepository(application: Application) {
    private val pdfDao = PdfDataBase.getInstances(application).pdfDao

    fun getPdfList() = pdfDao.getAllPdfs().flowOn(Dispatchers.IO)

    suspend fun insertPdf(pdfEntity: PdfEntity): Long{
        return pdfDao.insertPdf(pdfEntity)
    }

    suspend fun deletePdf(pdfEntity: PdfEntity): Int{
        return pdfDao.deletePdf(pdfEntity)
    }

    suspend fun updatePdf(pdfEntity: PdfEntity): Int{
        return pdfDao.updatePdf(pdfEntity)
    }
}