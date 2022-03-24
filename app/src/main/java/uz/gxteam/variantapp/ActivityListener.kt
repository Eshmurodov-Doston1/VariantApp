package uz.gxteam.variantapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.gxteam.variantapp.models.userData.UserData

interface ActivityListener {
    fun showToolbar()
    fun hideToolbar()
    fun hideBackIcon()
    fun showBackIcon()
    fun showLoading()
    fun hideLoading()
    fun connectedInternet():LiveData<Boolean>
    fun showLoadingCamera()
    fun hideLoadingCamera()


    fun toolText(userData:UserData)
}