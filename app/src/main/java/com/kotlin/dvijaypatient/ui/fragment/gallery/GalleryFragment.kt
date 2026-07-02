package com.kotlin.dvijaypatient.ui.fragment.gallery

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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.adapter.CompliancereportAdapter
import com.kotlin.dvijaypatient.adapter.DocumentsAdapter
import com.kotlin.dvijaypatient.adapter.MediaAdapter
import com.kotlin.dvijaypatient.databinding.FragmentGalleryBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.model.BaseResponse
import com.kotlin.dvijaypatient.model.CompliancereportDetails
import com.kotlin.dvijaypatient.model.DocumentsModel
import com.kotlin.dvijaypatient.model.MediaModel
import com.kotlin.dvijaypatient.network.RetrofitInstance
import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GalleryFragment : BaseFragment() {

    companion object {
        fun newInstance() = GalleryFragment()
    }
    private lateinit var binding: FragmentGalleryBinding
    private val viewModel: GalleryViewModel by viewModels()

    private var isLoggedIn: Boolean = false
    private var user_id: String = ""
    private var user_name: String = ""
    private val documentList = ArrayList<DocumentsModel>()
    private lateinit var adapter: DocumentsAdapter

    private val videoList = ArrayList<MediaModel>()




    private lateinit var adapterVideo: MediaAdapter
   override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGalleryBinding.inflate(inflater, container, false)



        val prefs = requireActivity()!!.getSharedPreferences(ClassGlobal.PREFERENCES, Context.MODE_PRIVATE)
        isLoggedIn = prefs.getBoolean("is_login", false)
        user_id = prefs.getString("user_id", "")!!
        user_name = prefs.getString("user_name", "")!!

        println("patient_id:"+user_id)

        getVideo()
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Video"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Documents"))

        // Tab Click Listener
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    binding.layoutGallery.visibility = View.VISIBLE
                    binding.layoutDocument.visibility = View.GONE
                    getVideo()
                } else {
                    binding.layoutGallery.visibility = View.GONE
                    binding.layoutDocument.visibility = View.VISIBLE
                    getDocument()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.swipeRefreshVideo.setOnRefreshListener {
            getVideo()

            // Stop refresh after data is loaded
            binding.swipeRefreshVideo.isRefreshing = false
        }

        binding.swipeRefreshDocument.setOnRefreshListener {
            getDocument()

            // Stop refresh after data is loaded
            binding.swipeRefreshDocument.isRefreshing = false
        }


        return binding.root
    }
    private fun getVideo()
    {

        showBaseProgressDialog()

        RetrofitInstance.api.get_video_list(params()).enqueue(object :
            Callback<BaseResponse<MediaModel>> {
            override fun onResponse(call: Call<BaseResponse<MediaModel>>, response: Response<BaseResponse<MediaModel>>) {
                hideBaseProgressDialog()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("API_RESPONSE", apiResponse.toString())
                    Log.d("API_RESPONSE1", apiResponse!!.status.toString())

                    if (apiResponse?.status!!){

                        context?.toast(response.body()!!.message)
                        videoList.clear()
                        videoList.addAll(response.body()!!.result)

                        adapterVideo = MediaAdapter(videoList)
                        binding.recyclerViewVideo.layoutManager = LinearLayoutManager(requireContext())
                        binding.recyclerViewVideo.adapter = adapterVideo


                    }else{
                        context?.toast(response.body()!!.message)
                    }
                } else {
                    context?.toast("Login Successful")
                }
            }

            override fun onFailure(call: Call<BaseResponse<MediaModel>>, t: Throwable) {
                Log.d("API_RESPONSE", "API call failed: ${t.message}")
                hideBaseProgressDialog()
                ClassGlobal.showErrorDialog(requireContext(),t.message.toString(),null)
            }
        })



    }
    private fun getDocument()
    {

        showBaseProgressDialog()

        RetrofitInstance.api.get_document_list(params()).enqueue(object :
            Callback<BaseResponse<DocumentsModel>> {
            override fun onResponse(call: Call<BaseResponse<DocumentsModel>>, response: Response<BaseResponse<DocumentsModel>>) {
                hideBaseProgressDialog()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("API_RESPONSE", apiResponse.toString())
                    Log.d("API_RESPONSE1", apiResponse!!.status.toString())

                    if (apiResponse?.status!!){

                        context?.toast(response.body()!!.message)
                        documentList.clear()
                        documentList.addAll(response.body()!!.result)

                        adapter = DocumentsAdapter(documentList)
                        binding.recyclerViewDocuments.layoutManager = LinearLayoutManager(requireContext())
                        binding.recyclerViewDocuments.adapter = adapter


                    }else{
                        context?.toast(response.body()!!.message)
                    }
                } else {
                    context?.toast("Login Successful")
                }
            }

            override fun onFailure(call: Call<BaseResponse<DocumentsModel>>, t: Throwable) {
                Log.d("API_RESPONSE", "API call failed: ${t.message}")
                hideBaseProgressDialog()
                ClassGlobal.showErrorDialog(requireContext(),t.message.toString(),null)
            }
        })



    }

    private fun params(): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["patient_id"] = user_id

        for ((key, value) in map) {
            Log.e("TAG", "Key: $key:$value")
        }
        return map
    }

    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

}