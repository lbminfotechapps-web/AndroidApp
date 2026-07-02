package com.kotlin.dvijaypatient.ui.fragment.otp

import android.annotation.SuppressLint
import android.app.Dialog
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
import com.google.firebase.messaging.FirebaseMessaging
import com.kotlin.dvijaypatient.MainActivity
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.databinding.FragmentLoginBinding
import com.kotlin.dvijaypatient.databinding.FragmentOtpBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.model.LoginApiResponse
import com.kotlin.dvijaypatient.network.RetrofitInstance
import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import com.kotlin.dvijaypatient.utils.ClassConnectionDetector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyOtpFragment : BaseFragment() {


    private lateinit var fragmentLoginBinding: FragmentOtpBinding
    private var cd: ClassConnectionDetector? = null
    var otp: String? = ""
    var mobile: String? = ""

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLoginBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_otp, container, false)
        fragmentLoginBinding.lifecycleOwner = this
        initTitleBar("Login", 0)

        mobile = arguments?.getString("mobile")
        otp = arguments?.getString("otp")
        Log.e("otp11",otp.toString())
        otp?.let {
            if (it.length == 4) {

                fragmentLoginBinding.otp1.setText(it[0].toString())
                fragmentLoginBinding.otp2.setText(it[1].toString())
                fragmentLoginBinding.otp3.setText(it[2].toString())
                fragmentLoginBinding.otp4.setText(it[3].toString())

                fragmentLoginBinding.otp4.requestFocus()

                // ✅ Show Progress Dialog
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.progress_dialog) // custom loader layout
                dialog.setCancelable(false)
                dialog.show()

                // ✅ Delay (2 seconds)
                fragmentLoginBinding.root.postDelayed({

                    dialog.dismiss()   // hide loader
                    verifyOtp()        // call verification

                }, 2000) // 2000 = 2 sec
            }
        }

        cd = ClassConnectionDetector(requireContext())

        fragmentLoginBinding.otp1.addTextChangedListener(
            GenericTextWatcher(
                fragmentLoginBinding.otp1,
                fragmentLoginBinding.otp2
            )
        )
        fragmentLoginBinding.otp2.addTextChangedListener(
            GenericTextWatcher(
                fragmentLoginBinding.otp2,
                fragmentLoginBinding.otp3
            )
        )
        fragmentLoginBinding.otp3.addTextChangedListener(
            GenericTextWatcher(
                fragmentLoginBinding.otp3,
                fragmentLoginBinding.otp4
            )
        )
        fragmentLoginBinding.otp4.addTextChangedListener(
            GenericTextWatcher(
                fragmentLoginBinding.otp4,
                null
            )
        )

// Move backward (on delete)
        fragmentLoginBinding.otp2.setOnKeyListener(
            GenericKeyEvent(
                fragmentLoginBinding.otp2,
                fragmentLoginBinding.otp1
            )
        )
        fragmentLoginBinding.otp3.setOnKeyListener(
            GenericKeyEvent(
                fragmentLoginBinding.otp3,
                fragmentLoginBinding.otp2
            )
        )
        fragmentLoginBinding.otp4.setOnKeyListener(
            GenericKeyEvent(
                fragmentLoginBinding.otp4,
                fragmentLoginBinding.otp3
            )
        )


        return fragmentLoginBinding.root
    }

    fun verifyOtp() {

        val otp1 = fragmentLoginBinding.otp1.text.toString().trim()
        val otp2 = fragmentLoginBinding.otp2.text.toString().trim()
        val otp3 = fragmentLoginBinding.otp3.text.toString().trim()
        val otp4 = fragmentLoginBinding.otp4.text.toString().trim()

        val enteredOtp = otp1 + otp2 + otp3 + otp4

        if (enteredOtp.length != 4) {
            Toast.makeText(requireContext(), "Enter valid OTP", Toast.LENGTH_SHORT).show()
            return
        }

        if (enteredOtp != otp) {
            Toast.makeText(requireContext(), "Incorrect OTP", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(requireContext(), "OTP Verified", Toast.LENGTH_SHORT).show()

        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()




    override fun onResume() {
        super.onResume()
        initTitleBar("Verify OTP", 1)

    }

}
