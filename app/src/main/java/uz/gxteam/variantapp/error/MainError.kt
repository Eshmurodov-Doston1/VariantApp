package uz.gxteam.variantapp.error

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.shashank.sony.fancydialoglib.Animation
import com.shashank.sony.fancydialoglib.FancyAlertDialog
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire
import uz.gxteam.variantapp.R


fun getError(context: Context,appError: AppError){

    var str:String = ""
    when(appError.code){
        401-> ({ str = appError.message.toString() }).toString()
        503->{
            str = context.getString(R.string.no_internet)
        }
        in 400..499->{
            if (appError.error?.errors!=null){
                str+=appError.error?.errors?.message
            }
        }
        in 500..599->{
            str = context.getString(R.string.server_error)
        }
        else->{
            ""
        }
    }



    var dialog =  FancyAlertDialog.Builder
        .with(context)
        .setTitle(context.getString(R.string.error))
        .setBackgroundColor(Color.WHITE) // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
        .setMessage(str)
        .setNegativeBtnText("Закрыть")
        .setPositiveBtnBackground(Color.WHITE) // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
        .setPositiveBtnText("Ok")
        .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8")) // for @ColorRes use setNegativeBtnBackgroundRes(R.color.colorvalue)
        .setAnimation(Animation.POP)
        .isCancellable(true)
        .setIcon(R.drawable.ic_error, View.VISIBLE)
        .onPositiveClicked { dialog: Dialog? ->

        }
        .onNegativeClicked { dialog: Dialog? ->
            Toast.makeText(
                context,
                "Cancel",
                Toast.LENGTH_SHORT
            ).show()
        }
        .build()
        .show()

}

fun noInternet(context: Context,mainContainer:ViewGroup,lifecycle:Lifecycle){
    NoInternetSnackbarFire.Builder(
        mainContainer,
        lifecycle
    ).apply {
        snackbarProperties.apply {
            connectionCallback = object : ConnectionCallback { // Optional
                override fun hasActiveConnection(hasActiveConnection: Boolean) {

                }
            }
            duration = Snackbar.LENGTH_LONG // Optional
            noInternetConnectionMessage =context.getString(R.string.no_internet) // Optional
            onAirplaneModeMessage =  context.getString(R.string.plane)// Optional
            snackbarActionText =  context.getString(R.string.settings)// Optional
            showActionToDismiss = true // Optional
            snackbarDismissActionText = "OK" // Optional
        }
    }.build()
}