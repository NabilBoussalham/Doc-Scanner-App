package com.example.docscannerapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.docscannerapp.data.local.converters.DateTypeConverter
import com.example.docscannerapp.data.local.dao.pdfDao
import com.example.docscannerapp.data.models.PdfEntity

@Database(
    entities = [PdfEntity::class], version = 1, exportSchema = false
)
@TypeConverters(DateTypeConverter::class)
abstract class PdfDataBase: RoomDatabase() {
    abstract val pdfDao : pdfDao
    companion object{
        @Volatile
        private var INSTANCES : PdfDataBase? = null
        fun getInstances(context: Context): PdfDataBase{
            synchronized(this){
                return INSTANCES ?: Room.databaseBuilder(
                    context.applicationContext, PdfDataBase::class.java,"pdf_db"
                ).build().also {
                    INSTANCES = it
                }
            }
        }
    }
}