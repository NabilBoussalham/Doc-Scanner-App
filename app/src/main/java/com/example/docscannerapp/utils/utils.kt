package com.example.docscannerapp.utils

import android.content.Context
import android.net.Uri
import android.os.Message
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.datatransport.runtime.Destination
import java.io.File
import java.io.FileOutputStream

fun Context.showToast(message: String){
    Toast.makeText(this,message, Toast.LENGTH_LONG).show()
}

fun getFileSize(context: Context,fileName: String): String{
    val file = File(context.filesDir,fileName)
    val fileSizeBytes = file.length()
    val fileSizeKB = fileSizeBytes/1024
    return if (fileSizeKB > 1024){
        val fileSizeMB = fileSizeKB/1024
        "$fileSizeMB Mb"
    }else{
        "$fileSizeKB KB"
    }
}

fun copyPdfFileToAppDirectory(context: Context,pdffUri: Uri, destinationFileName: String){
    val inputStream = context.contentResolver.openInputStream(pdffUri)
    val outputFile = File(context.filesDir,destinationFileName)
    FileOutputStream(outputFile).use {
        inputStream?.copyTo(it)
    }
}

fun renameFile(context: Context,aldFileName: String, newFileName: String){
    val oldFile = File(context.filesDir, aldFileName)
    val newFile = File(context.filesDir, newFileName)
    oldFile.renameTo(newFile)

}

fun deleteFile(context: Context,fileName: String): Boolean{
    val file = File(context.filesDir,fileName)
    return file.deleteRecursively()
}
fun getFileUri(context: Context,fileName: String):Uri {
    val file = File(context.filesDir,fileName)
    return FileProvider.getUriForFile(context,"${context.packageName}.provider",file)
}