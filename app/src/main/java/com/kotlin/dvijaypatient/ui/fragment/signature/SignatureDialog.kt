package com.kotlin.dvijaypatient.ui.fragment.signature

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.utils.DrawingView
import com.kotlin.dvijaypatient.utils.FragmentCalback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class SignatureDialog(
    private val callback: FragmentCalback,
    private val title:String
) : DialogFragment() {

    private lateinit var drawingView: DrawingView
    private lateinit var btnSave: Button
    private lateinit var btnReset: Button
    private lateinit var tv_title: TextView
    private lateinit var ivCross: ImageView
    private var pDialog: ProgressDialog? = null

    private var fname = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val view = LayoutInflater.from(context).inflate(R.layout.xml_canvas, null)

        drawingView = view.findViewById(R.id.drawingView)
        btnSave = view.findViewById(R.id.btn_save)
        btnReset = view.findViewById(R.id.btn_reset)
        tv_title = view.findViewById(R.id.tv_title)
        ivCross = view.findViewById(R.id.ivCross)



        tv_title.setText(title+" Signature")
        drawingView.isDrawingCacheEnabled = true
        ClassGlobal.SIGNATURE_IMAGE_NAME = ""

        btnReset.setOnClickListener {
            drawingView.erase()
            ClassGlobal.SIGNATURE_IMAGE_NAME = ""
            FragmentSignature.isSignatureDrawn = false
        }

        btnSave.setOnClickListener {
            drawingView.isDrawingCacheEnabled = true

            if (!FragmentSignature.isSignatureDrawn) {
                Toast.makeText(requireActivity(), "Please draw signature..!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val bitmap = drawingView.drawingCache
            val matrix = Matrix()
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )

            saveImage(rotatedBitmap)
        }

        ivCross.setOnClickListener {
            dismiss()   // Close the dialog
        }
        builder.setView(view)
        return builder.create()
    }


    private fun saveImage(finalBitmap: Bitmap) {
        FragmentSignature.isSignatureDrawn = false

        val ts = System.currentTimeMillis().toString()
        fname = "Signature_$ts.png"

        try {
            val folder =
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            val appFolder = File(folder, ClassGlobal.APP_NAME)
            appFolder.mkdirs()

            val file = File(appFolder, fname)
            if (file.exists()) file.delete()

            FileOutputStream(file).use { out ->
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            UploadFileToServer(file).execute()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private inner class UploadFileToServer(
        private val sourceFile: File
    ) : AsyncTask<Void, Int, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            pDialog = ProgressDialog(requireActivity())
            pDialog!!.setMessage("Uploading signature. Please wait...")
            pDialog!!.show()
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
                ).build()

            val request = Request.Builder()
                .url("${ClassGlobal.BASE_URL}upload_sign")
                .post(requestBody)
                .build()

            return try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) response.body?.string()
                else "HTTP Error: ${response.code}"
            } catch (e: Exception) {
                e.printStackTrace()
                e.toString()
            }
        }

        override fun onPostExecute(result: String?) {
            pDialog?.dismiss()

            if (result == null || !result.trim().startsWith("{")) {
                showError("Server error: $result")
                return
            }

            val jsonObj = JSONObject(result)
            if (jsonObj.getString("status") == "success") {
                AlertDialog.Builder(requireActivity())
                    .setMessage("Signature Uploaded Successfully!")
                    .setPositiveButton("OK") { _, _ ->
                        ClassGlobal.SIGNATURE_IMAGE_NAME = fname
                        callback.sendMessageToParent(true,fname)
                        dismiss()
                    }
                    .show()
            } else {
                showError("Failed to upload!")
            }
        }

        private fun showError(msg: String) {
            AlertDialog.Builder(requireActivity())
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show()
        }
    }

}

