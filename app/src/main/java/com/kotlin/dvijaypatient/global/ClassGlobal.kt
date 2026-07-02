
package com.kotlin.dvijaypatient.global

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider

import com.google.android.material.textfield.TextInputLayout
import com.kotlin.dvijaypatient.MainActivity
import com.kotlin.dvijaypatient.R


import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

open class ClassGlobal {

    companion object {

        //-------------------------------- Live URL ----------------------------------------------//


        //New URL by Chinmay Sir
       /* const val BASE_URL = "http://192.168.1.253:85/D_vijay/mobileapi/patient_app/"
        const val IMAGE_URL = "http://192.168.1.253:85/D_vijay/uploads/"*/
        const val BASE_URL = "https://dvijaypharma.com/mobileApi/patient_app/"

        const val IMAGE_URL = "https://dvijaypharma.com/uploads/"

        const val PREFERENCES = "DvijayPatient"
        const val APP_NAME = "DvijayPatient"
        var DIRECTORY_NAME = "DvijayPatient"
        var SIGNATURE_IMAGE_NAME = ""
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
        const val dateFormat = "dd-MM-yyyy"
        const val dateTimeFormat = "yyyy-MM-dd hh:mm:ss"
        const val DF_DD_MM_YYYY = "dd-MM-yyyy"
        const val TF_HH_MM_AA = "hh:mm a"
        const val TF_HH_MM_SS_AA = "hh:mm:ss a"
        const val timeFormat = "hh:mm aa"
        const val DF_DD_MM_YYYY_EEEE = "dd-MM-yyyy EEEE"
        const val EXPENSE_IMAGES = 1016
        const val RECORD_VIDEO: Int = 1022
        const val LOCATION_UPDATE = "location_update"
        const val LOCATION = "location"
        const val DF_YYYY_MM_DD_HH_MM_A = "yyyy-MM-dd hh:mm a"
        const val DF_YYYY_MM_DD_HH_MM_SS_A: String = "yyyy-MM-dd hh:mm:ss a"

        var BROADCAST_NAME: String = "com.syndicateagri.kotlin.ON_LOCATION_CHANGED"
        var OPERATOR_URL: String? = null
        var HELPER_URL: String? = null
        var SUPERVISOR_URL: String? = null
        var strPausestatusIs: String = "No"
        var strPauseTime: String = ""
        private var isDialogShown = false
        const val TYPE_FROM_DATE = 1
        const val TYPE_TO_DATE = 2
        const val ALLOWED = 0
        const val ALREADY_EXIST = 1
        const val ONLY_IN_PUNCHED = 2
        const val LEAVE = 3
        fun openPDF(fileName: String, activity: Activity) {
            try {
                val pdfFile = File(
                    activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                        .toString() + "/" + ClassGlobal.DIRECTORY_NAME + "/" + fileName
                )
                val target = Intent(Intent.ACTION_VIEW)
                val pdfURI = FileProvider.getUriForFile(
                    activity,
                    activity.applicationContext.packageName + ".provider",
                    pdfFile
                )
                target.flags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_CLEAR_TOP
                target.setDataAndType(pdfURI, "application/pdf")
                val intent = Intent.createChooser(target, "Open File")
                try {
                    activity.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        activity,
                        "No Application Found For Open PDF.!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        val mediaScanIntent = Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
                        )
                        val contentUri = Uri.fromFile(
                            File(
                                activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                                    .toString() + "/" + ClassGlobal.DIRECTORY_NAME + "/" + fileName
                            )
                        )
                        mediaScanIntent.data = contentUri
                        activity.sendBroadcast(mediaScanIntent)
                    } else {
                        activity.sendBroadcast(
                            Intent(
                                Intent.ACTION_MEDIA_MOUNTED, Uri.parse(
                                    "file://" + activity.getExternalFilesDir(
                                        Environment.DIRECTORY_DOCUMENTS
                                    ) + "/" + ClassGlobal.DIRECTORY_NAME + "/"
                                )
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun hideKeyboard(activity: Activity) {
            try {
                val imm =
                    activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                var view = activity.currentFocus
                if (view == null) {
                    view = View(activity)
                }
                val finalView: View = view
                view.post(Runnable {
                    imm.hideSoftInputFromWindow(finalView.windowToken, 0)
                })
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        fun AllowPermissions(activity: Activity) {
            val external_storage_Permission = ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val external_storage_write_Permission = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val listPermissionsNeeded: MutableList<String> = ArrayList()
            if (external_storage_Permission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (external_storage_write_Permission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                    activity,
                    listPermissionsNeeded.toTypedArray<String>(),
                    ClassGlobal.REQUEST_ID_MULTIPLE_PERMISSIONS
                )
            }
        }

        fun getOnResumeBackground(context: Context) {
            //  println("strMinutesstrMinutes===" + strMinutes)
            var strMinutes = 2
            if (strPausestatusIs == "Yes") {
                val df: DateFormat = SimpleDateFormat("dd MM yyyy, HH:mm a", Locale.ENGLISH)
                val dateFormat = SimpleDateFormat("dd MM yyyy, HH:mm a", Locale.ENGLISH)

                val date = df.format(Calendar.getInstance().time)
                val stringDateTime = date.split(",")

                val strResumeTime = stringDateTime[1].trim()

                val simpleDateFormat = SimpleDateFormat("HH:mm a", Locale.ENGLISH)

                try {
                    val date1: Date = simpleDateFormat.parse(strResumeTime)
                    val date2: Date = simpleDateFormat.parse(strPauseTime)

                    if (date1.after(date2)) {
                        val difference = date1.time - date2.time
                        val min = (difference / (1000 * 60)).toInt()

                        if (min > strMinutes) {
                            val intent = Intent(context, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }
                    } else {
                        // Handle the case where date1 is not later than date2.
                        // You might want to log an error or take a different action.
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        }

        fun getAlertDialog(
            context: Context?,
            title: String?,
            message: String?,
            positiveClickListener: DialogInterface.OnClickListener?,
            negativeClickListener: DialogInterface.OnClickListener?,
            showNegativeButton: Boolean
        ): android.app.AlertDialog? {
            val builder = android.app.AlertDialog.Builder(context)
            builder.setMessage(message)
            builder.setCancelable(false)
            if (showNegativeButton) builder.setNegativeButton(
                "Cancel"
            ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            builder.setPositiveButton(
                "Ok",
                positiveClickListener
                    ?: DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.dismiss() })
            return builder.create()
//        alertDialog.show();
        }

        fun extractYoutubeId(url: String): String? {
            var videoId = ""
            if (url.contains("youtube.com")) {
                val parts = url.split("v=".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                if (parts.size > 1) {
                    videoId = parts[1]
                    val ampersandIndex = videoId.indexOf("&")
                    if (ampersandIndex != -1) {
                        videoId = videoId.substring(0, ampersandIndex)
                    }
                }
            } else if (url.contains("youtu.be")) {
                val parts = url.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                if (parts.size > 1) {
                    videoId = parts[parts.size - 1]
                    val questionMarkIndex = videoId.indexOf("?")
                    if (questionMarkIndex != -1) {
                        videoId = videoId.substring(0, questionMarkIndex)
                    }
                }
            }
            return videoId
        }


        fun setFadeAnimation(view: View) {
            val anim = ScaleAnimation(
                0.0f,
                1.0f,
                0.0f,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            anim.duration = 1000
            view.startAnimation(anim)
        }


        //------------------------------------ Getting GeoAddress ---------------------------------------//
        fun getGeoAddress(context: Context?, latitude: Double, longitude: Double): String? {
            var address = "No Address Found"
            try {
                val geocoder = Geocoder(context!!, Locale.ENGLISH)
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses != null && addresses.size > 0) {
                    address =
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return address
        }


        fun showSuccessDialog(
            context: Context,
            message: String?,
            listener: View.OnClickListener?
        ) {
            val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.layout_success_dailog, null)
            builder.setView(view)
            (view.findViewById<View>(R.id.textTitle) as TextView).text =
                context.getString(R.string.app_name)
            (view.findViewById<View>(R.id.textMessage) as TextView).text =
                message
            (view.findViewById<View>(R.id.buttonAction) as Button).text =
                context.getString(R.string.okay)
            (view.findViewById<View>(R.id.imageIcon) as ImageView).setImageResource(R.drawable.done)
            val alertDialog = builder.create()
            view.findViewById<View>(R.id.buttonAction).setOnClickListener { view1: View? ->
                alertDialog.dismiss()
                listener?.onClick(view1)
            }
            if (alertDialog.window != null) {
                alertDialog.setCanceledOnTouchOutside(false)
                alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            if (alertDialog != null && !alertDialog.isShowing) {
                alertDialog.show()
            }
        }

        fun showWarningDialog(
            context: Context?,
            message: String?,
            listener: View.OnClickListener?
        ) {
            context?.let { showWarningDialog(it, message, listener, false) }
        }

        fun showWarningDialog(
            context: Context,
            message: String?,
            listener: View.OnClickListener?,
            showNegativeBtn: Boolean
        ) {
            val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.layout_warning_dailog, null)
            val btnNegative = view.findViewById<View>(R.id.buttonNo) as Button
            builder.setView(view)
            (view.findViewById<View>(R.id.textTitle) as TextView).text =
                context.getString(R.string.app_name)
            (view.findViewById<View>(R.id.textMessage) as TextView).text = message
            (view.findViewById<View>(R.id.imageIcon) as ImageView).setImageResource(R.drawable.warning)
            if (showNegativeBtn) {
                (view.findViewById<View>(R.id.buttonYes) as Button).text =
                    context.getString(R.string.yes)
                (view.findViewById<View>(R.id.buttonYes) as Button).background =
                    AppCompatResources.getDrawable(context, R.drawable.button_warning_background)
                btnNegative.text = context.getString(R.string.no)
                btnNegative.visibility = View.VISIBLE
            } else {
                (view.findViewById<View>(R.id.buttonYes) as Button).text =
                    context.getString(R.string.okay)
            }
            val alertDialog = builder.create()
            view.findViewById<View>(R.id.buttonYes).setOnClickListener { view1: View? ->
                alertDialog.dismiss()
                listener?.onClick(view1)
            }
            btnNegative.setOnClickListener { v: View? -> alertDialog.dismiss() }
            if (alertDialog.window != null) {
                alertDialog.setCanceledOnTouchOutside(false)
                alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            alertDialog.show()
        }

        fun showErrorDialog(context: Context, message: String, listener: View.OnClickListener?) {
            val builder =
                androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogTheme)
            val view = LayoutInflater.from(context).inflate(R.layout.layout_error_dailog, null)
            builder.setView(view)

            view.findViewById<TextView>(R.id.textTitle).text = context.getString(R.string.app_name)
            view.findViewById<TextView>(R.id.textMessage).text = message
            view.findViewById<Button>(R.id.buttonAction).text = context.getString(R.string.okay)
            view.findViewById<ImageView>(R.id.imageIcon).setImageResource(R.drawable.error)

            val alertDialog = builder.create()

            view.findViewById<Button>(R.id.buttonAction).setOnClickListener {
                alertDialog.dismiss()
                listener?.onClick(it)
            }

            alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
            alertDialog.show()
        }

        fun getBase64FromImgPath(imagePath: String?): String? {
            var bitmap: Bitmap? = null
            bitmap = BitmapFactory.decodeFile(imagePath)
            //encodedImage = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
            val byteStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteStream)
            // bitmap = ((BitmapDrawable) imagePath.getDrawable()).getBitmap();
            val byteArray = byteStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        fun getEditTextError(editText: EditText): String? {
            val textInputLayout = editText.parent.parent as TextInputLayout
            if (textInputLayout != null) {
                if (textInputLayout.error != null && textInputLayout.error!!.length > 0) return textInputLayout.error.toString()
            }
            return null
        }

        fun showEditTextError(editText: EditText, errorMsg: String?) {
            val textInputLayout = editText.parent.parent as TextInputLayout
            if (textInputLayout != null) {
                textInputLayout.error = errorMsg
                textInputLayout.isErrorEnabled = true
            }
        }

        fun removeEditTextError(editText: EditText) {
            val textInputLayout = editText.parent.parent as TextInputLayout
            if (textInputLayout != null) {
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
            }


        }


        fun getAddress(context: Context?, mLocation: Location): Address? {
            val geocoder = Geocoder(context!!, Locale.ENGLISH)
            try {
                val addresses = geocoder.getFromLocation(mLocation.latitude, mLocation.longitude, 1)
                if (!addresses!!.isEmpty()) return addresses[0]
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null
        }

        fun getStringAddress(address: Address): String? {
            val strReturnedAddress = StringBuilder()
            for (i in 0..address.maxAddressLineIndex) {
                strReturnedAddress.append(address.getAddressLine(i)).append("\n")
            }
            return strReturnedAddress.toString().trim { it <= ' ' }
        }



        fun getOnPauseBackground() {
            strPausestatusIs = "Yes"
            val df: DateFormat = SimpleDateFormat("dd MM yyyy, HH:mm a", Locale.ENGLISH)
            val date = df.format(Calendar.getInstance().time)
            val stringDateTime = date.split(",")
            strPauseTime = stringDateTime[1].trim()
        }

        ////////////////////////////////////////////////////////////////


        fun getAlertDialogSec(
            context: Context,
            title: String?,
            message: String?,
            positiveClickListener: DialogInterface.OnClickListener?,
            negativeClickListener: DialogInterface.OnClickListener?,
            showNegativeButton: Boolean
        ): android.app.AlertDialog {
            val builder = android.app.AlertDialog.Builder(context)
            builder.setMessage(message)
            builder.setCancelable(false)
            if (showNegativeButton) {
                builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            }
            builder.setPositiveButton(
                "Ok",
                positiveClickListener
                    ?: DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            return builder.create()
        }


        open fun roundFromString(value: Double, places: Int): Double {
            var value = value
            require(places >= 0)
            val factor = Math.pow(10.0, places.toDouble()).toLong()
            value = value * factor
            val tmp = Math.round(value)
            return tmp.toDouble() / factor
        }

        fun roundTarget(value: Double, places: Int): String? {
            var result = "0.00"
            try {
                result = String.format("%." + places + "f", value)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return result.convertDevanagariToArabic()
        }

        fun String.convertDevanagariToArabic(): String {
            return this.replace("०", "0")
                .replace("१", "1")
                .replace("२", "2")
                .replace("३", "3")
                .replace("४", "4")
                .replace("५", "5")
                .replace("६", "6")
                .replace("७", "7")
                .replace("८", "8")
                .replace("९", "9")
        }

        fun getTextInputLayout(editText: EditText): TextInputLayout? {
            return editText.parent.parent as TextInputLayout
        }

        fun startCall(
            activity: Activity?,
            callPhonePermissionResult: ActivityResultLauncher<String?>,
            mobileNo: String
        ) {
            if (activity == null) return
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                callPhonePermissionResult.launch(Manifest.permission.CALL_PHONE)
            } else {
                activity.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$mobileNo")))
            }
        }

        fun startSms(context: Context?, mobileNo: String) {
            if (context == null) return

            val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$mobileNo"))
            context.startActivity(smsIntent)
        }

        fun startWhatsAppChat(context: Context?, mobileNo: String) {
            if (context == null) return

            val url = "https://api.whatsapp.com/send?phone=$mobileNo"
            val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                `package` = "com.whatsapp"
            }

            try {
                context.startActivity(whatsappIntent)
            } catch (e: ActivityNotFoundException) {
                Log.e("WhatsApp", "WhatsApp not installed.", e)
                Toast.makeText(
                    context,
                    "WhatsApp is not installed on this device",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e("WhatsApp", "Error opening WhatsApp chat.", e)
                Toast.makeText(
                    context,
                    "WhatsApp is not available for this number",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


}