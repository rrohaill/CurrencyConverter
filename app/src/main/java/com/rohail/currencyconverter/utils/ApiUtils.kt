package com.rohail.currencyconverter.utils

import com.rohail.currencyconverter.interfaces.RevolutApiService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiUtils {

    companion object {
        fun create(): RevolutApiService {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .baseUrl(Constants.BASE_URL)
                .build()

            return retrofit.create(RevolutApiService::class.java)
        }

        fun getApiService(): RevolutApiService {
            val revolutApiServe by lazy {
                create()
            }
            return revolutApiServe
        }
    }


}