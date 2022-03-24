package uz.gxteam.variantapp.iterceptor

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

/** this is class SharedPreference create and save storage data accessToken and refreshToken and token type **/

class MySharedPreference @Inject constructor(private val context: Context){
    private val NAME = "Imadic"
    private val MODE = Context.MODE_PRIVATE
    private val preferences: SharedPreferences = context.getSharedPreferences(NAME, MODE)

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    /** clear sharedPreference **/
    fun clear(){
        var edite = preferences.edit()
        edite.remove("accessToken")
        edite.remove("refreshToken")
        edite.remove("tokenType")
//        edite.remove("token_socet")
        edite.apply()
    }

    var operator: String?
        get() = preferences.getString("operator", "")
        set(value) = preferences.edit {
            if (value != null) {
                it.putString("operator", value)
            }
        }

    var token_socet: String?
        get() = preferences.getString("token_socet", "")
        set(value) = preferences.edit {
            if (value != null) {
                it.putString("token_socet", value)
            }
        }

    /** save accessToken **/
    var accessToken: String?
        get() = preferences.getString("accessToken", "")
        set(value) = preferences.edit {
            if (value != null) {
                it.putString("accessToken", value)
            }
        }
    /** save refreshToken **/
    var refreshToken: String?
        get() = preferences.getString("refreshToken", "")
        set(value) = preferences.edit {
            if (value != null) {
                it.putString("refreshToken", value)
            }
        }
    /** save tokenType **/
    var tokenType: String?
        get() = preferences.getString("tokenType", "")
        set(value) = preferences.edit {
            if (value != null) {
                it.putString("tokenType", value)
            }
        }

    var language: String?
        get() = preferences.getString("language", "uz")
        set(value) = preferences.edit {
            if (value != null) {
                it.putString("language", value)
            }
        }

    var isFirst: Boolean?
        get() = preferences.getBoolean("isFirst", false)
        set(value) = preferences.edit {
            if (value != null) {
                it.putBoolean("isFirst", value)
            }
        }


    var theme: Boolean?
        get() = preferences.getBoolean("theme", false)
        set(value) = preferences.edit {
            if (value != null) {
                it.putBoolean("theme", value)
            }
        }




}