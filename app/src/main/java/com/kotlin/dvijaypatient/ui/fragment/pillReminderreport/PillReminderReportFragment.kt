package com.kotlin.dvijaypatient.ui.fragment.pillReminderreport

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.dvijaypatient.adapter.CompliancereportAdapter
import com.kotlin.dvijaypatient.adapter.DocumentsAdapter
import com.kotlin.dvijaypatient.databinding.FragmentPillReminderReportBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.model.BaseResponse
import com.kotlin.dvijaypatient.model.CompliancereportDetails
import com.kotlin.dvijaypatient.model.DocumentsModel
import com.kotlin.dvijaypatient.network.RetrofitInstance
import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PillReminderReportFragment : BaseFragment() {

    companion object {
        fun newInstance(): PillReminderReportFragment {
            return PillReminderReportFragment()
        }
    }

    private lateinit var viewModel: PillReminderReportViewModel
    private var _binding: FragmentPillReminderReportBinding? = null
    private val binding get() = _binding!!
    private var isLoggedIn: Boolean = false
    private var user_id: String = ""
    private var user_name: String = ""

    private val ComplianceDosesDatailsList = ArrayList<CompliancereportDetails>()
    private lateinit var adapter: CompliancereportAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPillReminderReportBinding.inflate(inflater, container, false)

        val prefs = requireActivity()!!.getSharedPreferences(ClassGlobal.PREFERENCES, Context.MODE_PRIVATE)
        isLoggedIn = prefs.getBoolean("is_login", false)
        user_id = prefs.getString("user_id", "")!!
        user_name = prefs.getString("user_name", "")!!
        binding.swipeRefreshLayout.setOnRefreshListener {
            getComplianceDosesDate() // Your API call

            // Stop refresh after data is loaded
            binding.swipeRefreshLayout.isRefreshing = false
        }
        getComplianceDosesDate()
        return binding.root
    }


    private fun getComplianceDosesDate()
    {

        showBaseProgressDialog()

        RetrofitInstance.api.get_upcoming_doses(params()).enqueue(object :
            Callback<BaseResponse<CompliancereportDetails>> {
            override fun onResponse(call: Call<BaseResponse<CompliancereportDetails>>, response: Response<BaseResponse<CompliancereportDetails>>) {
                hideBaseProgressDialog()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("API_RESPONSE", apiResponse.toString())
                    Log.d("API_RESPONSE1", apiResponse!!.status.toString())

                    if (apiResponse?.status!!){
                        binding.rvCompliance.visibility= View.VISIBLE
                        context?.toast(response.body()!!.message)
                        ComplianceDosesDatailsList.clear()
                        ComplianceDosesDatailsList.addAll(response.body()!!.result)

                        adapter = CompliancereportAdapter(ComplianceDosesDatailsList)

                        binding.rvCompliance.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvCompliance.adapter = adapter

                    }else{
                        binding.rvCompliance.visibility= View.GONE
                        context?.toast(response.body()!!.message)
                      //  binding.imgRecordNotfound.visibility=View.VISIBLE
                    }
                } else {
                    context?.toast("Login Successful")
                }
            }

            override fun onFailure(call: Call<BaseResponse<CompliancereportDetails>>, t: Throwable) {
                Log.d("API_RESPONSE", "API call failed: ${t.message}")
                hideBaseProgressDialog()
                ClassGlobal.showErrorDialog(requireContext(),t.message.toString(),null)
            }
        })
    }

    private fun params(): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["patient_id"] = user_id


        return map
    }

    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}