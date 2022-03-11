package uz.gxteam.variantapp.ui.main.mainView

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.mrbin99.laravelechoandroid.Echo
import net.mrbin99.laravelechoandroid.EchoOptions
import uz.gxteam.variantapp.ActivityListener
import uz.gxteam.variantapp.App
import uz.gxteam.variantapp.R
import uz.gxteam.variantapp.adapters.mainViewAdapter.MainRvAdapter
import uz.gxteam.variantapp.databinding.FragmentMainViewBinding
import uz.gxteam.variantapp.error.getError
import uz.gxteam.variantapp.error.noInternet
import uz.gxteam.variantapp.models.getApplications.Data
import uz.gxteam.variantapp.models.mainClass.Request
import uz.gxteam.variantapp.utils.AppConstant.BASE_URL
import uz.gxteam.variantapp.utils.AppConstant.PORT
import uz.gxteam.variantapp.utils.VariantResourse
import uz.gxteam.variantapp.viewModels.appViewModel.AppViewModel
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainViewFragment : Fragment(R.layout.fragment_main_view) {
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
    lateinit var echo:Echo
    var listRvMain:ArrayList<Request> = ArrayList()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.noData.visibility =View.VISIBLE
        binding.rv.visibility =View.GONE
        mainRvAdapter = MainRvAdapter(requireContext(),object:MainRvAdapter.OnItemClickListener{
            override fun onItemClick(data: Data, position: Int) {
                var bundle = Bundle()
                bundle.putSerializable("data",data)
                bundle.putString("token",data.token)
                findNavController().navigate(R.id.action_mainFragment_to_chatFragment,bundle)
            }
        })
        binding.apply {
            when(param1){
                0->{
                    lifecycleScope.launch {
                        appViewModel.getApplications().collect {
                            when(it){
                                is VariantResourse.Loading->{
                                   activityListener.showLoading()
                                }
                                is VariantResourse.SuccessApplications->{
                                    activityListener.hideLoading()
                                   if (it.applications?.data?.isNotEmpty() == true){
                                       binding.noData.visibility =View.GONE
                                       binding.rv.visibility =View.VISIBLE
                                   }
                                    mainRvAdapter.submitList(it.applications?.data)
                                    rv.adapter = mainRvAdapter
                                    Log.e("List", it.applications?.data.toString() )

                                }
                                is VariantResourse.Error->{
                                    activityListener.showLoading()
                                 if (it.appError.internetConnection==true){
                                     getError(requireContext(),it.appError)
                                 }else{
                                     noInternet(requireContext(),binding.root,lifecycle)
                                 }
                                }
                            }
                        }
                    }



                }
                1->{
                    listRvMain.clear()
//                    mainRvAdapter.submitList(listRvMain)
//                    mainRvAdapter.notifyDataSetChanged()
                    binding.noData.visibility =View.VISIBLE
                    binding.rv.visibility =View.GONE
                }
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityListener = activity as ActivityListener
    }

    override fun onDestroy() {
        super.onDestroy()
        //echo.disconnect()
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
}