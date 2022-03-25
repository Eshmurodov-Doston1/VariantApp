package uz.gxteam.variantapp.ui.main

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
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
import uz.gxteam.variantapp.adapters.mainViewAdapter.MainViewPagerAdapter
import uz.gxteam.variantapp.databinding.FragmentMainBinding
import uz.gxteam.variantapp.databinding.ItemTabBinding
import uz.gxteam.variantapp.error.getError
import uz.gxteam.variantapp.error.noInternet
import uz.gxteam.variantapp.models.webSocket.sendSocket.SendSocketData
import uz.gxteam.variantapp.utils.VariantResourse
import uz.gxteam.variantapp.viewModels.appViewModel.AppViewModel
import uz.gxteam.webSocet.EchoWebSocketListener
import uz.gxteam.webSocet.SocketData
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment(R.layout.fragment_main) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @Inject
    lateinit var appViewModel:AppViewModel

    lateinit var activityListener: ActivityListener
    private val binding:FragmentMainBinding by viewBinding()
    lateinit var listCategory:ArrayList<String>
    lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    lateinit var isRefreshingApp:MutableLiveData<Boolean>
    var client: OkHttpClient? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            loadCategory()
            activityListener.hideLoading()
            activityListener.showToolbar()
            activityListener.hideBackIcon()
//            binding.viewPager2.isUserInputEnabled =false
            lifecycleScope.launch {
                appViewModel.getUserData().collect {
                    when(it){
                        is VariantResourse.Loading->{
                            activityListener.showLoading()
                        }
                        is VariantResourse.SuccessUserData->{
                          activityListener.hideLoading()
                            it.userData?.let { it1 -> activityListener.toolText(it1) }
                        }
                        is VariantResourse.Error->{
                            activityListener.hideLoading()
                            if (it.appError.internetConnection==true){
                                if(it.appError.code==401) {
                                    val navOptions = NavOptions.Builder().setPopUpTo(R.id.authFragment, true)
                                    var bundle = Bundle()
                                    findNavController().navigate(R.id.authFragment,bundle,navOptions.build())
                                }else{
                                    getError(requireContext(), it.appError,findNavController())
                                }
                            }else{
                                noInternet(requireActivity() as MainActivity,requireContext(),binding.root,lifecycle)
                            }
                        }
                    }
                }
            }






            mainViewPagerAdapter = MainViewPagerAdapter(requireActivity())
            viewPager2.adapter = mainViewPagerAdapter
            TabLayoutMediator(tabLayout,viewPager2){tab,position->
                var itemTabBinding = ItemTabBinding.inflate(LayoutInflater.from(requireContext()),null,false)
                if (position==0){
                    itemTabBinding.cons.setBackgroundResource(R.drawable.tab_layout_set)
                    itemTabBinding.name.text = listCategory[position]
                }else{
                    itemTabBinding.cons.setBackgroundResource(R.drawable.tab_layout_no_select)
                    itemTabBinding.name.text = listCategory[position]
                }
                tab.customView = itemTabBinding.root
            }.attach()
            isRefreshingApp = MutableLiveData<Boolean>()
            viewPager2.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    val tabCount = tabLayout.tabCount
                    for (i in 0 until tabCount){
                        tabLayout.getTabAt(i).let {
                            var itemTabBinding = it?.customView?.let { it1 -> ItemTabBinding.bind(it1) }
                            if(i==0){
                                itemTabBinding?.cons?.setBackgroundResource(R.drawable.tab_layout_no_select)
                            }else{
                                itemTabBinding?.cons?.setBackgroundResource(R.drawable.tab_layout_no_select)
                            }
                            itemTabBinding?.name?.text = listCategory[i]
                        }
                    }
                    tabLayout.getTabAt(position).let {
                        var itemTab1Binding = it?.customView?.let { it1 -> ItemTabBinding.bind(it1) }
                        if (position!=0){
                            itemTab1Binding?.name?.text = listCategory[position]
                            itemTab1Binding?.cons?.setBackgroundResource(R.drawable.tab_layout_set)
                        } else{
                            itemTab1Binding?.cons?.setBackgroundResource(R.drawable.tab_layout_set)
                            itemTab1Binding?.name?.text = listCategory[position]
                        }
                    }

                }
            })

        }
    }


    private fun loadCategory() {
        listCategory = ArrayList()
        listCategory.add("Открытие")
        listCategory.add("Завершенные")
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityListener = activity as ActivityListener
    }
}