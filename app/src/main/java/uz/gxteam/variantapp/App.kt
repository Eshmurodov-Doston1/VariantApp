package uz.gxteam.variantapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.droidnet.DroidNet
import io.socket.client.IO
import io.socket.client.Socket
import net.gotev.uploadservice.UploadServiceConfig
import uz.gxteam.variantapp.di.AppComponent
import uz.gxteam.variantapp.di.DaggerAppComponent
import uz.gxteam.variantapp.utils.AppConstant.BASE_URL
import java.net.URISyntaxException
import javax.inject.Inject


class App @Inject constructor() :Application(){
    companion object{
        lateinit var appComponent: AppComponent
        const val notificationChannelID = "TestChannel"
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                notificationChannelID,
                "TestApp Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onCreate() {
        super.onCreate()
        DroidNet.init(this)
        appComponent = DaggerAppComponent.factory().create(this,applicationContext)
        createNotificationChannel()
        UploadServiceConfig.initialize(
            context = this,
            defaultNotificationChannel = notificationChannelID,
            debug = BuildConfig.DEBUG
        )
    }


    override fun onLowMemory() {
        super.onLowMemory()
        DroidNet.getInstance().removeAllInternetConnectivityChangeListeners()
    }
}