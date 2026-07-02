package com.kotlin.dvijaypatient.ui.fragment.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.kotlin.dvijaypatient.MainActivity
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.databinding.FragmentLoginBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.model.LoginApiResponse
import com.kotlin.dvijaypatient.network.RetrofitInstance
import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import com.kotlin.dvijaypatient.ui.fragment.otp.VerifyOtpFragment
import com.kotlin.dvijaypatient.utils.ClassConnectionDetector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : BaseFragment() {


    private lateinit var fragmentLoginBinding: FragmentLoginBinding
    private var cd: ClassConnectionDetector? = null
    var androidId: String = ""
      var  firebaseToken: String = ""

        @SuppressLint("SuspiciousIndentation")
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
        fragmentLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        fragmentLoginBinding.lifecycleOwner = this


        androidId = Settings.Secure.getString(requireActivity()!!.contentResolver, Settings.Secure.ANDROID_ID)
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseToken = task.result
                    Log.e("firebaseToken","firebaseToken="+firebaseToken)
                }
            }


            cd = ClassConnectionDetector(requireContext())

        fragmentLoginBinding.btnSignUp.setOnClickListener {
            val username = fragmentLoginBinding.etUserName.text.toString().trim()
            val password = fragmentLoginBinding.etUserPassword.text.toString().trim()

            if (username == ""){
              //  context?.toast("Please enter username")
                ClassGlobal.showWarningDialog(activity, "Please Enter Mobile Number", null)
                fragmentLoginBinding.etUserName.requestFocus()
            }else if(username.length<10){
                ClassGlobal.showWarningDialog(activity, "Please Enter Valid Mobile Number", null)
                fragmentLoginBinding.etUserName.requestFocus()
            }else{
                if (cd!!.isConnectingToInternet){
                    callLoginApi()
                   // findNavController().navigate(R.id.action_nav_login_to_nav_otp)
                }else{
                    ClassGlobal.showWarningDialog(activity, "No internet Connection", null)
                }

            }

        }
        return fragmentLoginBinding.root
    }



    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()



    private fun callLoginApi() {
        showBaseProgressDialog()

        RetrofitInstance.api.userLogin(params()).enqueue(object :
            Callback<LoginApiResponse> {
            override fun onResponse(call: Call<LoginApiResponse>, response: Response<LoginApiResponse>) {
                hideBaseProgressDialog()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("API_RESPONSE", apiResponse.toString())
                    Log.d("API_RESPONSE1", apiResponse!!.status.toString())

                    if (apiResponse?.status!!){

                        context?.toast(response.body()!!.message)

                        val user_id= apiResponse?.patient_id
                        val user_name= apiResponse?.patient_name
                        val otp= apiResponse?.otp
                        val mobile= apiResponse?.patient_mobile


                        Log.e("otp22",apiResponse?.otp.toString())


                        val prefs = activity!!.getSharedPreferences(
                            ClassGlobal.PREFERENCES,
                            Context.MODE_PRIVATE
                        )
                        val edit = prefs.edit()
                        edit.putString("user_id", user_id)
                        edit.putString("user_name", user_name)
                        edit.putBoolean("is_login", true)
                        edit.putString("firebaseToken", firebaseToken)
                        edit.putString("userLoginName", fragmentLoginBinding.etUserName.text.toString().trim())
                        edit.putString("userPassword", fragmentLoginBinding.etUserPassword.text.toString().trim())
                        edit.commit()

                        activity?.runOnUiThread {

                            val fragment = VerifyOtpFragment()

                            val bundle = Bundle()
                            bundle.putString("mobile", mobile)
                            bundle.putString("otp", otp)

                            fragment.arguments = bundle

                            requireActivity().supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.nav_host_fragment_content_home, fragment)
                                .addToBackStack(null)
                                .commit()
                        }
                        /* val intent = Intent(context, MainActivity::class.java)
                         startActivity(intent)
                         requireActivity().finish()*/
                    }else{
                        context?.toast(response.body()!!.message)
                    }
                } else {
                    context?.toast("Login Successful")
                }
            }

            override fun onFailure(call: Call<LoginApiResponse>, t: Throwable) {
                Log.d("API_RESPONSE", "API call failed: ${t.message}")
                hideBaseProgressDialog()
                ClassGlobal.showErrorDialog(requireContext(),t.message.toString(),null)
            }
        })
    }


    private fun params(): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["mobile"] = fragmentLoginBinding.etUserName.text.toString().trim()
        map["gcm_id"] = firebaseToken


        for ((key, value) in map) {
            Log.e("TAG", "Key: $key:$value")
        }
        return map
    }

    fun getMobileInfo(): String {
        var model = ""
        var apkVersion = ""
        var apiVersion = ""
        var firebaseToken = ""
        var sdkVersion = ""
        var mobileInfo = ""

        try {
            val manufacturer = Build.MANUFACTURER
            val mModel = Build.MODEL
            model = if (mModel.startsWith(manufacturer)) {
                "MODEL_$mModel"
            } else {
                "MODEL_$manufacturer $mModel"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val packageManager = context?.packageManager
            val packageName = context?.packageName
            val packageInfo = packageName?.let { packageManager?.getPackageInfo(it, 0) }
            apkVersion = "#APK_${packageInfo?.versionName}"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            apiVersion = "#API_${Build.VERSION.SDK_INT}"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseToken = task.result
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            sdkVersion = "#OS_${Build.VERSION.RELEASE}"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            mobileInfo = "$model$apkVersion$apiVersion$sdkVersion$firebaseToken"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mobileInfo
    }


    override fun onResume() {
        super.onResume()
        initTitleBar("Login", 1)

    }

}
