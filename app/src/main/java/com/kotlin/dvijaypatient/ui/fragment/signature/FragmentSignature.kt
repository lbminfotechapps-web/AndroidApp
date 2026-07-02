package com.kotlin.dvijaypatient.ui.fragment.signature

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import com.kotlin.dvijaypatient.utils.DrawingView
import com.kotlin.dvijaypatient.utils.FragmentCallback22
import com.kotlin.dvijaypatient.utils.SharedViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.Random

class FragmentSignature : BaseFragment() {

    companion object {
        var isSignatureDrawn = false
        fun newInstance() = FragmentSignature()
    }

    private lateinit var drawingView: DrawingView
    private lateinit var btnSave: Button
    private lateinit var btnReset: Button
    private var pDialog: ProgressDialog? = null
    private var fname: String = ""
    private var totalSize: Long = 0
    private lateinit var sharedViewModel: SharedViewModel
    var fragmentCallback: FragmentCallback22? = null
    fun setFragmentCalback(fragmentCallback: FragmentCallback22?) {
        this.fragmentCallback = fragmentCallback
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.xml_canvas, container, false)
        isSignatureDrawn = false

        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        drawingView = view.findViewById(R.id.drawingView)
        drawingView.isDrawingCacheEnabled = true

        btnSave = view.findViewById(R.id.btn_save)
        btnReset = view.findViewById(R.id.btn_reset)
        ClassGlobal.SIGNATURE_IMAGE_NAME = ""
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        btnReset.setOnClickListener {
            isSignatureDrawn = false
            ClassGlobal.SIGNATURE_IMAGE_NAME = ""
            drawingView.isDrawingCacheEnabled = false
            drawingView.erase()
        }

        btnSave.setOnClickListener {
            ClassGlobal.SIGNATURE_IMAGE_NAME = ""
            drawingView.isDrawingCacheEnabled = true
            val bitmap = drawingView.drawingCache
            val matrix = Matrix()
            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (isSignatureDrawn) {
                saveImage(rotatedBitmap)
            } else {
                Toast.makeText(requireActivity(), "Please draw signature..!", Toast.LENGTH_SHORT).show()
            }
        }

        initTitleBar("Signature",0)
        return view
    }

    private fun saveImage(finalBitmap: Bitmap) {
        isSignatureDrawn = false
        val generator = Random()
        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()
        val n = generator.nextInt(10000)
        fname = "Signature_$ts.png"

        try {
            val mediaStorageDir = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                ClassGlobal.APP_NAME
            )
            mediaStorageDir.mkdirs()

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.e(ClassGlobal.APP_NAME, "Oops! Failed to create ${ClassGlobal.APP_NAME} directory")
            }

            val file = File(mediaStorageDir, fname)
            if (file.exists()) file.delete()

            FileOutputStream(file).use { out ->
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
            }

            UploadFileToServer(file).execute()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(File(Environment.DIRECTORY_PICTURES + "/KVAT/" + fname))
            mediaScanIntent.data = contentUri
            requireActivity().sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private inner class UploadFileToServer(private val sourceFile: File) : AsyncTask<Void, Int, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            pDialog = ProgressDialog(requireActivity()).apply {
                setMessage("Uploading signature. Please wait...")
                isIndeterminate = false
                max = 100
                show()
                progress = 0
            }
        }

        override fun onProgressUpdate(vararg progress: Int?) {
            super.onProgressUpdate(*progress)
            pDialog?.progress = progress[0] ?: 0
        }

        override fun doInBackground(vararg params: Void?): String? {
            return uploadFile()
        }

        private fun uploadFile(): String? {
            val client = OkHttpClient()
            val mediaType = "image/jpeg".toMediaTypeOrNull()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    sourceFile.name,
                    sourceFile.asRequestBody(mediaType)
                )
                .build()

            val request = Request.Builder()
                .url("${ClassGlobal.BASE_URL}upload_sign")
                .post(requestBody)
                .build()

            return try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.string()
                } else {
                    "Error occurred! HTTP Status Code: ${response.code}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                e.toString()
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            pDialog?.dismiss()

            Log.e("UPLOAD_RESPONSE", "Server response = $result")

            if (result.isNullOrEmpty()) {
                showError("Server returned empty response.")
                return
            }

            // If server sends plain text (Error...) → NOT JSON
            if (!result.trim().startsWith("{")) {
                showError(result)
                return
            }

            try {
                val jsonObj = JSONObject(result)
                val status = jsonObj.getString("status")

                if (status == "success") {
                    handleSuccess(jsonObj)
                } else {
                    showError("Unable to upload signature..! File size is too large..!")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                showError("Invalid server response. Not JSON.")
            }
        }

        private fun showError(msg: String) {
            AlertDialog.Builder(requireActivity())
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok", null)
                .show()
        }

        private fun handleSuccess(jsonObj: JSONObject) {
            AlertDialog.Builder(requireActivity())
                .setMessage("Signature Uploaded Successfully..!")
                .setCancelable(false)
                .setPositiveButton("Ok") { _, _ ->
                    ClassGlobal.SIGNATURE_IMAGE_NAME = fname

                    sharedViewModel.signatureCallback.value?.sendMessageToParent(true, fname)
                    fragmentCallback?.sendMessageToParent(true)

                    requireActivity().supportFragmentManager.popBackStack()
                }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
       // ClassGlobal.getOnResumeBackground(requireActivity())
    }

    override fun onPause() {
        super.onPause()
      //  ClassGlobal.getOnPauseBackground()
    }

}
