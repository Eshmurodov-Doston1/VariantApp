package uz.gxteam.variantapp.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import com.github.angads25.toggle.interfaces.OnToggledListener
import com.github.angads25.toggle.model.ToggleableView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.gxteam.variantapp.ActivityListener
import uz.gxteam.variantapp.App
import uz.gxteam.variantapp.R
import uz.gxteam.variantapp.databinding.DialogLogOutBinding
import uz.gxteam.variantapp.databinding.FragmentSettingsBinding
import uz.gxteam.variantapp.error.getError
import uz.gxteam.variantapp.error.noInternet
import uz.gxteam.variantapp.utils.VariantResourse
import uz.gxteam.variantapp.viewModels.loginViewModel.LoginViewModel
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment(R.layout.fragment_settings), OnToggledListener {
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
    private val binding:FragmentSettingsBinding by viewBinding()
    @Inject
    lateinit var loginViewModel:LoginViewModel
    lateinit var listenerActivity:ActivityListener
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            listenerActivity.showBackIcon()
            switchTheme.isOn = loginViewModel.getShared().theme==true
            switchTheme.setOnToggledListener(this@SettingsFragment)

            logOut.setOnClickListener {
                var dialogAlert = AlertDialog.Builder(requireContext(),R.style.BottomSheetDialogThem)
                val create = dialogAlert.create()
                val dialogLogOutBinding = DialogLogOutBinding.inflate(LayoutInflater.from(requireContext()), null, false)
                dialogLogOutBinding.closeButton.setOnClickListener {
                    create.dismiss()
                }
                dialogLogOutBinding.okBtn.setOnClickListener {
                    lifecycleScope.launch {
                        loginViewModel.logOut().collect {
                            when(it){
                                is VariantResourse.Loading->{
                                    listenerActivity.showLoading()
                                }
                                is VariantResourse.SuccessLogOut->{
                                    listenerActivity.hideLoading()
                                    create.dismiss()

                                  NavOptions.Builder()
                                      .setPopUpTo(R.id.authFragment,true)

                                }
                                is VariantResourse.Error->{
                                    listenerActivity.hideLoading()
                                    if (it.appError.internetConnection==true){
                                        getError(requireContext(),it.appError)
                                    }else{
                                        create.dismiss()
                                        noInternet(binding.root.context,binding.root,lifecycle)
                                    }
                                }
                            }
                        }
                    }
                }
                create.setView(dialogLogOutBinding.root)
                create.setCancelable(false)
                create.show()
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listenerActivity = activity as ActivityListener
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {
        if (isOn){
            loginViewModel.getShared().theme = true
            binding.switchTheme.isOn = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.root.setBackgroundColor(Color.WHITE)
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE
        }else{
            loginViewModel.getShared().theme = false
            binding.switchTheme.isOn = false
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.root.setBackgroundColor(resources.getColor(R.color.background))
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}