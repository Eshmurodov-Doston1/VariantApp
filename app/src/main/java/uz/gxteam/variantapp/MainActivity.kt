package uz.gxteam.variantapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.viewbinding.library.activity.viewBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import coil.load
import com.droidnet.DroidListener
import com.droidnet.DroidNet
import okhttp3.OkHttpClient
import uz.gxteam.variantapp.databinding.ActivityMainBinding
import uz.gxteam.variantapp.models.userData.UserData
import uz.gxteam.variantapp.viewModels.appViewModel.AppViewModel
import uz.gxteam.variantapp.viewModels.loginViewModel.LoginViewModel
import javax.inject.Inject


class MainActivity : AppCompatActivity(),ActivityListener,DroidListener {
    private val binding:ActivityMainBinding by viewBinding()
    @Inject
    lateinit var loginViewModel: LoginViewModel

    @Inject
    lateinit var appViewModel: AppViewModel

    var client: OkHttpClient? = null
    lateinit var  mDroidNet: DroidNet
    var networkInternet:MutableLiveData<Boolean> = MutableLiveData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        App.appComponent.inject(this)
        mDroidNet = DroidNet.getInstance();
        mDroidNet.addInternetConnectivityListener(this);

        window.statusBarColor = resources.getColor(R.color.background)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        if (loginViewModel.getShared().theme==true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }



        binding.back.setOnClickListener {
            findNavController(R.id.fragment).popBackStack()
        }

        binding.settings.setOnClickListener {
            var bundle = Bundle()
            val anim = NavOptions.Builder()
                .setEnterAnim(R.anim.enter)
                .setExitAnim(R.anim.exit)
                .setPopEnterAnim(R.anim.pop_enter)
                .setPopExitAnim(R.anim.pop_exit)
            findNavController(R.id.fragment).navigate(R.id.settingsFragment,bundle,anim.build())
        }
















    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            var name = data?.getStringExtra("firstName") + " " + data?.getStringExtra("lastName");
        }
    }

    override fun onBackPressed() {
        if (findNavController(R.id.fragment).currentDestination?.id==R.id.mainFragment){
            finish()
        }else if (findNavController(R.id.fragment).currentDestination?.id==R.id.authFragment){
           finish()
        }else{
            findNavController(R.id.fragment).popBackStack()
        }
    }

    override fun onNavigateUp(): Boolean {
        return findNavController(R.id.fragment).navigateUp()

    }

    override fun showToolbar() {
        binding.toolbar.visibility = View.VISIBLE
    }

    override fun hideToolbar() {
        binding.toolbar.visibility = View.GONE
    }

    override fun hideBackIcon() {
        binding.back.visibility = View.GONE
        binding.settings.visibility = View.VISIBLE
    }

    override fun showBackIcon() {
        binding.back.visibility = View.VISIBLE
        binding.settings.visibility = View.GONE
    }


    override fun showLoading() {
        binding.include.consLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.include.consLoading.visibility = View.GONE
    }


    override fun connectedInternet(): LiveData<Boolean> {
        return networkInternet
    }

    override fun showLoadingCamera() {
        binding.camera.consLoading.visibility = View.VISIBLE
    }

    override fun hideLoadingCamera() {
        binding.camera.consLoading.visibility = View.GONE
    }

    override fun toolText(userData: UserData) {
        binding.name.text = "${userData?.name} ${userData?.surname}"
        if (userData?.photo!=null){
            binding.imageUser.load(userData.photo.toString())
        }else{
            binding.imageUser.setImageResource(R.drawable.logo)
        }
        binding.type.text = userData.type
    }


    override fun onInternetConnectivityChanged(isConnected: Boolean) {
        networkInternet.postValue(isConnected)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mDroidNet.removeInternetConnectivityChangeListener(this);
    }


}