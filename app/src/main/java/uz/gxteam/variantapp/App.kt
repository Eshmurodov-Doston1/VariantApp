package uz.gxteam.variantapp

import android.app.Application
import com.droidnet.DroidNet
import io.socket.client.IO
import io.socket.client.Socket
import org.bouncycastle.jce.provider.BouncyCastleProvider
import uz.gxteam.variantapp.di.AppComponent
import uz.gxteam.variantapp.di.DaggerAppComponent
import uz.gxteam.variantapp.utils.AppConstant.BASE_URL

import javax.inject.Inject


class App @Inject constructor() :Application(){
    companion object{
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        DroidNet.init(this)
        appComponent = DaggerAppComponent.factory().create(this,applicationContext)
    }



    override fun onLowMemory() {
        super.onLowMemory()
        DroidNet.getInstance().removeAllInternetConnectivityChangeListeners()
    }
}