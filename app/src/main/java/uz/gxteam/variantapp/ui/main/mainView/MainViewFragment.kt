package uz.gxteam.variantapp.ui.main.mainView

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import uz.gxteam.variantapp.ActivityListener
import uz.gxteam.variantapp.App
import uz.gxteam.variantapp.MainActivity
import uz.gxteam.variantapp.R
import uz.gxteam.variantapp.adapters.mainViewAdapter.MainRvAdapter
import uz.gxteam.variantapp.databinding.FragmentMainViewBinding
import uz.gxteam.variantapp.error.getError
import uz.gxteam.variantapp.error.noInternet
import uz.gxteam.variantapp.models.getApplications.DataApplication
import uz.gxteam.variantapp.models.mainClass.Request
import uz.gxteam.variantapp.models.oneApplication.sendToken.SendToken
import uz.gxteam.variantapp.models.webSocket.sendSocket.SendSocketData
import uz.gxteam.variantapp.utils.VariantResourse
import uz.gxteam.variantapp.viewModels.appViewModel.AppViewModel
import uz.gxteam.webSocet.SocketData
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainViewFragment : Fragment(R.layout.fragment_main_view),CoroutineScope{
    // TODO: Rename and change types of parameters
    private var param1: Int? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    @Inject
    lateinit var appViewModel: AppViewModel

    private val binding:FragmentMainViewBinding by viewBinding()

    lateinit var mainRvAdapter: MainRvAdapter
    lateinit var activityListener:ActivityListener
    var client: OkHttpClient? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.noData.visibility =View.GONE
        binding.rv.visibility =View.GONE
        client = OkHttpClient()


        binding.addCard.setOnClickListener {
            launch {
                appViewModel.createApplication().collect{
                    when(it){
                        is VariantResourse.SuccessCreateApplication->{
                            loadCreateChat(it.application?.token.toString())
                        }
                        is VariantResourse.Error->{
                            activityListener.hideLoading()
                            if (it.appError.internetConnection==true){
                                getError(requireContext(),it.appError,findNavController())
                            }else{
                                noInternet(requireActivity() as MainActivity,requireContext(),binding.root,lifecycle)
                            }
                        }
                    }
                }
            }
        }

    }

    private fun getSuccessAppLications() {
        binding.addCard.visibility =View.GONE
        mainRvAdapter = MainRvAdapter(requireContext(),object:MainRvAdapter.OnItemClickListener{
            override fun onItemClick(dataApplication: DataApplication, position: Int) {

            }
        },param1?:0)

        lifecycleScope.launch {
            appViewModel.getAllApplicationsSuccess().collect {
                when(it){
                    is VariantResourse.ApplicationsSuccess->{
                        activityListener.hideLoading()
                        if (it.applications?.data?.isEmpty() == true){
                            binding.noData.visibility =View.VISIBLE
                            binding.rv.visibility =View.GONE
                        }else{
                            mainRvAdapter.submitList(it.applications?.data)
                            binding.rv.adapter = mainRvAdapter
                            binding.rv.visibility =View.VISIBLE
                            binding.circleProgress.visibility = View.GONE
                        }
                        binding.refreshLayout.isRefreshing = false
                    }
                    is VariantResourse.Error->{
                        activityListener.hideLoading()
                        if (it.appError.internetConnection==true){
                            getError(requireContext(),it.appError,findNavController())
                        }else{
                            noInternet(requireActivity() as MainActivity,requireContext(),binding.root,lifecycle)
                        }
                    }
                }
            }
        }
    }

    private fun loadCreateChat(token:String) {
        lifecycleScope.launch {
            appViewModel.getOneApplication(SendToken(token)).collect {
                when(it){
                    is VariantResourse.Loading->{
                        activityListener.showLoading()
                    }
                    is VariantResourse.SuccessGetApplication->{
                        Log.e("data", it.oneApplication?.toString().toString())
                        activityListener.hideLoading()
                        var bundle = Bundle()
                        bundle.putString("token",token)
                        val oneApplication = Gson().toJson(it.oneApplication)
                        bundle.putString("data",oneApplication)
                        findNavController().navigate(R.id.action_mainFragment_to_chatFragment,bundle)
                    }
                    is VariantResourse.Error->{
                        activityListener.hideLoading()
                        if (it.appError.internetConnection==true){
                            getError(requireContext(),it.appError,findNavController())
                        }else{
                            noInternet(requireActivity() as MainActivity,requireContext(),binding.root,lifecycle)
                        }
                    }
                }
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            appViewModel.getApplications().collect {
                    when(it){
                        is VariantResourse.Loading->{
                            progressingVisible()
                        }
                        is VariantResourse.SuccessApplications->{
                            progressingGone()
                            if (it.applications?.data?.isNotEmpty() == true){
                                binding.noData.visibility =View.GONE
                                binding.rv.visibility =View.VISIBLE
                            }else if(it.applications?.data?.isEmpty() == true){
                                progressingGone()
                                binding.noData.visibility = View.VISIBLE
                            }
                            mainRvAdapter.submitList(it.applications?.data)
                            binding.rv.adapter = mainRvAdapter
                            binding.refreshLayout.isRefreshing = false
                        }
                        is VariantResourse.Error->{
                            progressingGone()
                            if (it.appError.internetConnection==true){
                                getError(requireContext(),it.appError,findNavController())
                            }else{
                                noInternet(requireActivity() as MainActivity,requireContext(),binding.root,lifecycle)
                            }
                        }
                    }
                }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityListener = activity as ActivityListener
    }

    override fun onResume() {
        super.onResume()
        when(param1){
            0->{
                binding.refreshLayout.setOnRefreshListener {
                    loadData()
                }

                mainRvAdapter = MainRvAdapter(requireContext(),object:MainRvAdapter.OnItemClickListener{
                    override fun onItemClick(dataApplication: DataApplication, position: Int) {
                        var bundle = Bundle()
                        val toJson = Gson().toJson(dataApplication)
                        bundle.putString("dataClass",toJson)
                        bundle.putString("token",dataApplication.token)
                        bundle.putSerializable("data",null)
                        findNavController().navigate(R.id.action_mainFragment_to_chatFragment,bundle)
                    }
                },param1?:0)

                activityListener.connectedInternet().observe(viewLifecycleOwner){
                    if (it){
                        loadData()
                        binding.addCard.visibility = View.VISIBLE
                    }else{
                        noInternet(requireActivity() as MainActivity,requireContext(),binding.root,lifecycle)
                    }
                }
            }
            1->{
                binding.refreshLayout.setOnRefreshListener {
                    getSuccessAppLications()
                }
            getSuccessAppLications()
            }
        }
    }





    fun progressingVisible(){
        binding.circleProgress.visibility = View.VISIBLE
    }
    fun progressingGone(){
        binding.circleProgress.visibility = View.GONE
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Int) =
            MainViewFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}