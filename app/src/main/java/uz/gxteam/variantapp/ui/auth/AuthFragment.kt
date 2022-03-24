package uz.gxteam.variantapp.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import uz.gxteam.variantapp.ActivityListener
import uz.gxteam.variantapp.App
import uz.gxteam.variantapp.MainActivity
import uz.gxteam.variantapp.R
import uz.gxteam.variantapp.databinding.FragmentAuthBinding
import uz.gxteam.variantapp.error.getError
import uz.gxteam.variantapp.error.noInternet
import uz.gxteam.variantapp.models.login.ReqLogin
import uz.gxteam.variantapp.utils.VariantResourse
import uz.gxteam.variantapp.viewModels.loginViewModel.LoginViewModel
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AuthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AuthFragment : Fragment(R.layout.fragment_auth) {
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
    lateinit var loginViewModel: LoginViewModel
    lateinit var activityListener: ActivityListener
    private val binding:FragmentAuthBinding by viewBinding()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            if (!loginViewModel.getShared().accessToken.equals("")){
                activityListener.showLoading()
                findNavController().navigate(R.id.action_authFragment_to_mainFragment)
            }
            activityListener.hideToolbar()
            phoneNumber.requestFocus()
            phoneNumber.doAfterTextChanged {
                if (it?.length==9){
                    password.requestFocus()
                }
            }
            login.setOnClickListener {
                val phoneNumber = "998${phoneNumber.text.toString().trim()}"
                val password = password.text.toString().trim()
                if (phoneNumber.isEmpty()){
                    textInput.error = getString(R.string.no_phone)
                }else if (phoneNumber.length<9){
                    textInput.error = getString(R.string.error_phone)
                }else if (password.isEmpty()){
                    textInput1.error = getString(R.string.no_password)
                }else if (password.length<8){
                    textInput1.error = getString(R.string.password_length)
                } else if (phoneNumber.isNotEmpty() && password.isNotEmpty()){
                    var reqLogin = ReqLogin(password,phoneNumber)
                    lifecycleScope.launch {
                        loginViewModel.login(reqLogin).collect {
                            when(it){
                                is VariantResourse.Loading->{
                                    activityListener.showLoading()
                                }
                                is VariantResourse.SuccessLogin->{
                                    var bundle = Bundle()
                                    val anim = NavOptions.Builder()
                                        .setEnterAnim(R.anim.enter)
                                        .setExitAnim(R.anim.exit)
                                        .setPopEnterAnim(R.anim.pop_enter)
                                        .setPopExitAnim(R.anim.pop_exit)
                                        .setPopUpTo(R.id.action_authFragment_to_mainFragment, false)
                                    findNavController().navigate(R.id.action_authFragment_to_mainFragment,bundle,anim.build())
                                }
                                is VariantResourse.Error->{
                                    if (it.appError.internetConnection==true){
                                        activityListener.hideLoading()
                                        getError(requireContext(),it.appError,findNavController())
                                    }else{
                                        activityListener.hideLoading()
                                        noInternet(requireActivity() as MainActivity,requireContext(),binding.cons,requireActivity().lifecycle)
                                    }
                                }
                            }
                        }
                    }

                }
            }

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AuthFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AuthFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityListener  = activity as ActivityListener
    }
}