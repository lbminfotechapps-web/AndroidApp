package com.kotlin.dvijaypatient.ui.fragment.compliancereport

import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.adapter.CompliancereportAdapter
import com.kotlin.dvijaypatient.databinding.FragmentCompliancereportBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.model.BaseResponse
import com.kotlin.dvijaypatient.model.CompliancereportDetails
import com.kotlin.dvijaypatient.model.MissdosedetailsResponse
import com.kotlin.dvijaypatient.model.MissingdatesResponse
import com.kotlin.dvijaypatient.network.RetrofitInstance
import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import com.kotlin.dvijaypatient.ui.fragment.home.HomeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompliancereportFragment : BaseFragment() {

    private var _binding: FragmentCompliancereportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CompliancereportViewModel by viewModels()

    companion object {
        fun newInstance() = CompliancereportFragment()
    }
    private var isLoggedIn: Boolean = false
    private var user_id: String = ""
    private var user_name: String = ""
    private val ComplianceDosesDatailsList = ArrayList<CompliancereportDetails>()
    private lateinit var adapter: CompliancereportAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentCompliancereportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI() {

        val prefs = requireActivity()!!.getSharedPreferences(
            ClassGlobal.PREFERENCES,
            Context.MODE_PRIVATE
        )
        isLoggedIn = prefs.getBoolean("is_login", false)
        user_id = prefs.getString("user_id", "")!!
        user_name = prefs.getString("user_name", "")!!

        if(HomeFragment.complianceType.equals("compliance"))
        {
            binding.tvReportTitle.setText("Compliance Report")
        }
        else
        {
            binding.tvReportTitle.setText("Missed Pills Report")
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            getComplianceDosesDate()

            // Stop refresh after data is loaded
            binding.swipeRefreshLayout.isRefreshing = false
        }
        getComplianceDosesDate()
    }


    private fun getComplianceDosesDate()
    {

        showBaseProgressDialog()

        RetrofitInstance.api.get_compliance_report(params()).enqueue(object :
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
                       // binding.imgRecordNotfound.visibility=View.VISIBLE
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
        map["type"] = HomeFragment.complianceType
        /*map["from_date"] = "2026-04-01"*/
        /*map["to_date"] = "2026-04-30"*/
       // map["product_name"] = ""
        for ((key, value) in map) {
            Log.e("TAG", "Key: $key:$value")
        }
        return map
    }

    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null   // 🔥 prevent memory leak
    }
}