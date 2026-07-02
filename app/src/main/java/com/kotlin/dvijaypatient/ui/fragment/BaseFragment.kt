package com.kotlin.dvijaypatient.ui.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.kotlin.dvijaypatient.MainActivity
import com.kotlin.dvijaypatient.R

open class BaseFragment : Fragment() {

    private var layoutParams: RelativeLayout.LayoutParams? = RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

    lateinit var fragmentView: View
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_base, container, false)

        return fragmentView
    }


    fun showBaseProgressDialog() {
        // Initialize only if it is not already initialized
        if (!this::progressDialog.isInitialized) {
            progressDialog = ProgressDialog(requireContext()).apply {
                setMessage("Loading...") // Set your loading message
                setCancelable(false) // Prevent dismissal on back press
            }
        }

        if (!progressDialog.isShowing) {
            progressDialog.show()
        }
    }
    fun hideBaseProgressDialog() {
        // Dismiss the existing dialog
        if (this::progressDialog.isInitialized && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    @SuppressLint("ResourceType")
    protected fun initTitleBar(title: String?, white: Int) {

        val mainActivity = activity as? MainActivity ?: return

        if (!isAdded || view == null) return

        val binding = mainActivity.activityMainBinding ?: return
        val tvUsername = binding.appBarHome.tvUsername

        tvUsername.visibility = View.VISIBLE


            if (white == 1)
                binding.appBarHome.appbarhomee.visibility= View.GONE
            else
                binding.appBarHome.appbarhomee.visibility= View.VISIBLE


        tvUsername.text = title ?: ""

        // ❌ REMOVE THIS (causing crash)
        // tvUsername.layoutParams = layoutParams

        setDrawerLocked(false)
    }

    private fun setDrawerLocked(enabled: Boolean) {
        val mainActivity = requireActivity() as? MainActivity ?: return
        if (enabled) {
            mainActivity.activityMainBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            mainActivity.activityMainBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

   /* open fun showNotifyAlert(
        activity: Activity?, dialogTitle: String?,
        dialogMessage: String?, icon: Int
    ) {
        Alerter.create(requireActivity())
            .setTitle(dialogTitle.toString())
            .setIcon(icon)
            .setDuration(5000)
            .setBackgroundColorRes(R.color.red)
            .setText(dialogMessage.toString())
            .show()
    }*/


}