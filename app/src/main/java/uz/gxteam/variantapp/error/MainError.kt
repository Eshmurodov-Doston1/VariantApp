package uz.gxteam.variantapp.error

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.shashank.sony.fancydialoglib.Animation
import com.shashank.sony.fancydialoglib.FancyAlertDialog
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.pendulum.NoInternetDialogPendulum
import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire
import uz.gxteam.variantapp.MainActivity
import uz.gxteam.variantapp.R


fun getError(context: Context,appError: AppError,navController: NavController){
    var str:String = ""
    when(appError.code){
        401-> ({
            val popUpTo = NavOptions.Builder().setPopUpTo(R.id.authFragment, true)
            var bundle = Bundle()
            navController.navigate(R.id.authFragment,bundle,popUpTo.build())
            str = appError.error?.errors?.message.toString()
        }).toString()
        503->{
            str = context.getString(R.string.no_internet)
        }
        in 400..499->{
            if (appError.error?.errors!=null){
                str+=appError.error?.errors?.message
            }
            if (appError.errorList!=null){
                appError.errorList?.errors1?.forEach {
                    str+="${it.message}\n"
                }
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

fun noInternet(activity: MainActivity,context: Context,mainContainer:ViewGroup,lifecycle:Lifecycle){

    NoInternetDialogPendulum.Builder(
        activity,
        lifecycle
    ).apply {
        dialogProperties.apply {
            connectionCallback = object : ConnectionCallback { // Optional
                override fun hasActiveConnection(hasActiveConnection: Boolean) {
                    // ...
                }
            }

            cancelable = false // Optional
            noInternetConnectionTitle = context.getString(R.string.no_internet) // Optional
            noInternetConnectionMessage = context.getString(R.string.chek_internet) // Optional
            showInternetOnButtons = true // Optional
            pleaseTurnOnText =  context.getString(R.string.turn_on)// Optional
            wifiOnButtonText = "Wi-Fi" // Optional
            mobileDataOnButtonText = "Mobile data" // Optional

            onAirplaneModeTitle = context.getString(R.string.no_internet) // Optional
            onAirplaneModeMessage = context.getString(R.string.airplane_mode) // Optional
            pleaseTurnOffText = context.getString(R.string.turn_off) // Optional
            airplaneModeOffButtonText =  context.getString(R.string.airplane)// Optional
            showAirplaneModeOffButtons = true // Optional
        }
    }.build()



}