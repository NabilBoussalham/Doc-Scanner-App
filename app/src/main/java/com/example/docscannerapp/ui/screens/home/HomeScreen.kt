package com.example.docscannerapp.ui.screens.home

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.docscannerapp.R
import com.example.docscannerapp.data.models.PdfEntity
import com.example.docscannerapp.ui.screens.common.ErrorScreen
import com.example.docscannerapp.ui.screens.common.LoadingDialog
import com.example.docscannerapp.ui.screens.home.components.PdfLayout
import com.example.docscannerapp.ui.screens.home.components.RenameDeleteDialog
import com.example.docscannerapp.utils.copyPdfFileToAppDirectory
import com.example.docscannerapp.utils.showToast
import com.example.docscannerapp.ui.viewmodels.PdfViewModel
import com.example.docscannerapp.utils.Resource
import com.example.docscannerapp.utils.getFileSize
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(pdfViewModel: PdfViewModel) {
    RenameDeleteDialog(pdfViewModel = pdfViewModel)
    LoadingDialog(pdfViewModel = pdfViewModel)
    val activity = LocalContext.current as Activity
    val context = LocalContext.current
    val pdfState by pdfViewModel.pdfStateFlow.collectAsState()
    val message = pdfViewModel.message
        LaunchedEffect(Unit) {
            message.collect{
                when(it){
                    is Resource.Success -> {
                        context.showToast(it.data)
                    }
                    is Resource.Error -> {
                        context.showToast(it.message)
                    }
                    Resource.Idle -> TODO()
                    Resource.Loading -> TODO()

                }
            }
        }
    val scannerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK){
                val scanningResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                scanningResult?.pdf?.let { pdf ->
                    Log.d("pdfName",pdf.uri.lastPathSegment.toString())
                    val date = Date()
                    val fileName = SimpleDateFormat(
                        "dd-MMM-yyyy HH:mm:ss",
                        Locale.getDefault()
                    ).format(date) + ".pdf"
                    copyPdfFileToAppDirectory(context,
                        pdf.uri,fileName)
                    val pdfEntity = PdfEntity(UUID.randomUUID().toString(),fileName,
                        getFileSize(context, fileName),date)

                    pdfViewModel.insertPdf(
                        pdfEntity
                    )
                }
            }
        }
    val scanner = remember {
        GmsDocumentScanning.getClient(
            GmsDocumentScannerOptions.Builder().setGalleryImportAllowed(true).setResultFormats(
                GmsDocumentScannerOptions.RESULT_FORMAT_PDF).setScannerMode(
                GmsDocumentScannerOptions.SCANNER_MODE_FULL).build()
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = stringResource(id = R.string.app_name))

            })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick =  {
                scanner.getStartScanIntent(activity).addOnSuccessListener {
                    scannerLauncher.launch(
                        IntentSenderRequest.Builder(it).build()
                    )
                }.addOnFailureListener {
                    it.printStackTrace()
                    context.showToast(it.message.toString())
                }
            }, text = {
                Text(text = stringResource(R.string.scan))
            }, icon = {
                Icon(painter = painterResource(id = R.drawable.baseline_camera_24), contentDescription = "camera")
            })
        }
    ){ paddingValue ->
        pdfState.DisplayResult(onLoading = {
//            if circular progress bar use in screen
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
        }, onSuccess = {pdfList ->
            if (pdfList.isEmpty()){
                ErrorScreen(message = "No Pdf")
            }else{


                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValue)
                ) {
                    items(items = pdfList, key = {pdfEntity ->
                        pdfEntity.id
                    }){ pdfEntity ->
                        PdfLayout(pdfEntity = pdfEntity, pdfViewModel = pdfViewModel)

                    }
                }
            }
        }, onError = {
            ErrorScreen(message = it)

        })

    }




}