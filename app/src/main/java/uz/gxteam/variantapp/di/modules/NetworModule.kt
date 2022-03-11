package uz.gxteam.variantapp.di.modules

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.gxteam.variantapp.iterceptor.TokenInterceptor
import uz.gxteam.variantapp.network.appNetwork.AppService
import uz.gxteam.variantapp.network.login.LoginService
import uz.gxteam.variantapp.utils.AppConstant.BASE_URL
import javax.inject.Singleton

@Module
class NetworModule {

    @Singleton
    @Provides
    fun provideBaseURL(): String = BASE_URL

    @Provides
    @Singleton
    fun provideOkhttp(tokenInterceptor: TokenInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(tokenInterceptor).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl:String,okHttpClient: OkHttpClient):Retrofit{
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideLoginService(retrofit: Retrofit): LoginService = retrofit.create(LoginService::class.java)

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit):AppService = retrofit.create(AppService::class.java)

}