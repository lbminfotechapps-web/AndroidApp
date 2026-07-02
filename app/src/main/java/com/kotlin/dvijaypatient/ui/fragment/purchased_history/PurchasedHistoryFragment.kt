package com.kotlin.dvijaypatient.ui.fragment.purchased_history

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.adapter.CompliancereportAdapter
import com.kotlin.dvijaypatient.adapter.PurchasedListAdapter
import com.kotlin.dvijaypatient.databinding.BottomSheetInvoiceDetailsBinding
import com.kotlin.dvijaypatient.databinding.FragmentPurchasedHistoryBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.model.BaseResponse
import com.kotlin.dvijaypatient.model.BaseTargetResponse
import com.kotlin.dvijaypatient.model.CompliancereportDetails
import com.kotlin.dvijaypatient.model.MedicineModel
import com.kotlin.dvijaypatient.model.PurchasedDetailsResponse
import com.kotlin.dvijaypatient.model.PurchasedHistoryResponse
import com.kotlin.dvijaypatient.network.RetrofitInstance

import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import com.kotlin.dvijaypatient.ui.fragment.home.HomeFragment
import com.kotlin.dvijaypatient.utils.ClassConnectionDetector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator


class PurchasedHistoryFragment : BaseFragment(), PurchasedListAdapter.OnStatusClickListener{

    private lateinit var binding: FragmentPurchasedHistoryBinding
    private var cd: ClassConnectionDetector? = null
    private var user_id: String = ""
    private var fromDate = ""
    private var toDate = ""

    private var loginUserDesg: String = ""
    private val viewModel: PurchasedListViewModel by viewModels()
    private var purchasedHistoryResponse :MutableList<PurchasedHistoryResponse> = mutableListOf()
    private var pageLimit = 0
    private var isLoading = false
    private var isLastPage = false
    private lateinit var adapter: PurchasedListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_purchased_history, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    @SuppressLint("SimpleDateFormat")
    private fun init() {
       // initTitleBar("Purchased History", 0)


        val prefs =
            requireActivity().getSharedPreferences(ClassGlobal.PREFERENCES, Context.MODE_PRIVATE)
        user_id = prefs.getString("user_id", "") ?: ""


        cd = ClassConnectionDetector(requireContext())
       /* binding.rvMedicine.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {

                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager =
                        recyclerView.layoutManager as LinearLayoutManager


                    val visibleItemCount =
                        layoutManager.childCount

                    val totalItemCount =
                        layoutManager.itemCount

                    val firstVisible =
                        layoutManager.findFirstVisibleItemPosition()


                    if (!isLoading &&
                        !isLastPage &&
                        (visibleItemCount + firstVisible >= totalItemCount - 3)
                    ) {


                        pageLimit=pageLimit+20

                       // getPurchasedHistory()
                    }

                }
            }
        )*/
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val currentDate = sdf.format(Date())

        fromDate = currentDate
        toDate = currentDate

        binding.tvFromDate.text = currentDate
        binding.tvToDate.text = currentDate

        binding.layoutFromDate.setOnClickListener {

            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->

                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)

                    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                    fromDate = sdf.format(selectedDate.time)

                    binding.tvFromDate.text = fromDate

                    // Clear To Date when From Date changes
                    toDate = ""
                    binding.tvToDate.text = ""

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )


            // Disable previous dates
        //    datePicker.datePicker.minDate = System.currentTimeMillis()

            datePicker.show()
        }



        binding.layoutToDate.setOnClickListener {

            if (fromDate.isEmpty()) {

                Toast.makeText(
                    requireContext(),
                    "Please select From Date first",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->

                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)

                    val sdf = SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault()
                    )

                    toDate = sdf.format(selectedDate.time)

                    binding.tvToDate.text = toDate
                    getPurchasedHistory()

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )


            try {

                val sdf = SimpleDateFormat(
                    "dd-MM-yyyy",
                    Locale.getDefault()
                )

                val from = sdf.parse(fromDate)

                if (from != null) {
                    datePicker.datePicker.minDate = from.time
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }


            datePicker.show()

        }
        binding.swipeRefreshLayout.setOnRefreshListener {


            isLastPage = false
           // currentPage = 1
            pageLimit=0
            getPurchasedHistory()


            binding.swipeRefreshLayout.isRefreshing=false

        }
        getPurchasedHistory()


        Log.e("loginUserDesg", loginUserDesg)



    }


/* private fun getPurchasedHistory() {

  showBaseProgressDialog()

     val adapter = PurchasedListAdapter(requireContext(), this)
     binding.rvMedicine.layoutManager = LinearLayoutManager(
         requireContext(),
         LinearLayoutManager.VERTICAL,
         false
     )
     binding.rvMedicine.adapter = adapter

     viewModel.attendance_data.observe(viewLifecycleOwner, Observer { response ->
         response?.let {
             binding.rvMedicine.visibility=View.VISIBLE
             Log.e("getOrderHistory1",it.status.toString())
             hideBaseProgressDialog()
             if (it.status) {

                // binding.tvNoRecord.visibility=View.GONE
                 attendanceList.clear()
                 attendanceList.addAll(it.result)
                 Log.e("getOrderHistory2",it.result.toString())
                 adapter.submitList(attendanceList)
                 adapter.notifyDataSetChanged()
                 hideBaseProgressDialog()

             } else {
                 hideBaseProgressDialog()
                 binding.rvMedicine.visibility=View.GONE
                 binding.imgRecordNotfound.visibility=View.VISIBLE

             }
         }
     })

     val params = HashMap<String, String>()
     params["patient_id"] = user_id




     for ((key, value) in params) {
         Log.e("TAG", "Key: $key:$value")
     }
     viewModel.getAttendance(params)
 }*/






 private fun getPurchasedHistory() {

     showBaseProgressDialog()

     RetrofitInstance.api.getPatientInvoiceHistory(params())
         .enqueue(object :
             Callback<BaseTargetResponse<List<PurchasedHistoryResponse>>> {

             override fun onResponse(
                 call: Call<BaseTargetResponse<List<PurchasedHistoryResponse>>>,
                 response: Response<BaseTargetResponse<List<PurchasedHistoryResponse>>>
             ) {

                 hideBaseProgressDialog()

                 if (response.isSuccessful) {

                     val apiResponse = response.body()

                     Log.d("API_RESPONSE", apiResponse.toString())

                     if (apiResponse?.status == true) {
                        // binding.imgRecordNotfound.visibility = View.GONE
                         binding.rvMedicine.visibility = View.VISIBLE

                         purchasedHistoryResponse.clear()

                         apiResponse.result?.let {
                             purchasedHistoryResponse.addAll(it)
                         }


                         adapter = PurchasedListAdapter(
                             requireContext(),
                             this@PurchasedHistoryFragment
                         )



                         binding.rvMedicine.layoutManager =
                             LinearLayoutManager(requireContext())
                         adapter.submitList(purchasedHistoryResponse)
                         binding.rvMedicine.adapter = adapter


                     } else {

                         context?.toast(apiResponse?.message ?: "")

                        // binding.imgRecordNotfound.visibility = View.VISIBLE
                         binding.rvMedicine.visibility = View.GONE
                     }

                 }

             }


             override fun onFailure(
                 call: Call<BaseTargetResponse<List<PurchasedHistoryResponse>>>,
                 t: Throwable
             ) {

                 hideBaseProgressDialog()

                 Log.d(
                     "API_RESPONSE",
                     "API call failed: ${t.message}"
                 )

                 ClassGlobal.showErrorDialog(
                     requireContext(),
                     t.message.toString(),
                     null
                 )
             }

         })
 }
 fun Context.toast(message: CharSequence) =
     Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


 private fun params(): HashMap<String, String> {
     val map = HashMap<String, String>()
     map["patient_id"] = user_id

     map["fromDate"] = binding.tvFromDate.text.toString().trim()
     map["toDate"] = binding.tvToDate.text.toString().trim()


     map["startLimit"] = pageLimit.toString()


     for ((key, value) in map) {
         Log.e("TAG", "Key: $key:$value")
     }
     return map
 }

 override fun onStatusClick(
     item: List<PurchasedDetailsResponse>,
     position: Int
 ) {

     // open details screen / dialog here
     if (item.isNullOrEmpty()) return
     val bottomSheet = InvoiceDetailsBottomSheet()
     val gson = Gson()

     val json = gson.toJson(item)

     val bundle = Bundle()
     bundle.putString("products", json)

     bottomSheet.arguments = bundle
     bottomSheet.show(
         childFragmentManager,
         "InvoiceBottomSheet"
     )
 }


 class InvoiceDetailsBottomSheet : BottomSheetDialogFragment() {


     private lateinit var binding: BottomSheetInvoiceDetailsBinding


     override fun onCreateView(
         inflater: LayoutInflater,
         container: ViewGroup?,
         savedInstanceState: Bundle?
     ): View {


         binding = BottomSheetInvoiceDetailsBinding.inflate(
             inflater,
             container,
             false
         )


         setData()

         return binding.root
     }

     class InvoiceProductAdapter(
         private val list:List<PurchasedDetailsResponse>
     ):
         RecyclerView.Adapter<InvoiceProductAdapter.ViewHolder>() {


         class ViewHolder(view:View):
             RecyclerView.ViewHolder(view){

             val name:TextView=view.findViewById(R.id.tvProductName)
             val code: TextView =view.findViewById(R.id.tvProductCode)
             val qty:TextView=view.findViewById(R.id.tvQty)
             val rate:TextView=view.findViewById(R.id.tvRate)
             val amount:TextView=view.findViewById(R.id.tvAmount)

         }


         override fun onCreateViewHolder(
             parent: ViewGroup,
             viewType:Int
         ):ViewHolder{


             val view=LayoutInflater.from(parent.context)
                 .inflate(
                     R.layout.item_invoice_product,
                     parent,
                     false
                 )


             return ViewHolder(view)

         }



         override fun onBindViewHolder(
             holder:ViewHolder,
             position:Int
         ){


             val item=list[position]


             holder.name.text=item.fld_prod_name+"( "+item.fld_prod_code+")"
             holder.code.text=item.fld_prod_code
             holder.qty.text="Qty : ${item.fld_Qty}"
             holder.rate.text="Rate: ₹ ${item.fld_Rate}"
             holder.amount.text="Amt: ₹ ${item.fld_Amt}"


         }



         override fun getItemCount():Int=list.size


     }

     private fun setData() {

         binding.btnClose.setOnClickListener {
             dismiss()
         }

         val json = arguments?.getString("products")

         if (!json.isNullOrEmpty()) {

             val type = object : TypeToken<ArrayList<PurchasedDetailsResponse>>() {}.type

             val productList: ArrayList<PurchasedDetailsResponse> =
                 Gson().fromJson(json, type)

             binding.rvProducts.layoutManager =
                 LinearLayoutManager(requireContext())

             binding.rvProducts.adapter =
                 InvoiceProductAdapter(productList)
         }
     }
}}

